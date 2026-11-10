package repo.engine

import repo.core.exception.ComponentNotFoundException
import repo.core.exception.RepositoryException
import repo.core.model.ConflictStrategy
import repo.core.model.Coordinates
import repo.core.model.Version
import repo.spec.download.ResolutionResult
import repo.spec.download.ResolvedComponent
import java.io.File

class StorageDownloader(private val storage: Storage) {

    private data class Key(val organisation: String, val project : String)
    private val descriptors = mutableMapOf<Coordinates, DescriptorDto>()

    private fun key(c: Coordinates) = Key(c.organisation, c.project)

    private fun transitiveScopes() : Set<String>{
        if(!storage.exists((Layout.CONFIG)))
            throw RepositoryException("Repozitorijum nije definisan (nedostaje {$Layout.CONFIG}).")
        return storage.readJson(Layout.CONFIG, ConfigDto::class.java)
            .scopes.filter {it.transitive}.map { it.name }.toSet()
    }

    private fun descriptor(c:Coordinates) : DescriptorDto = descriptors.getOrPut(c) {
        if(!storage.exists(Layout.descriptor(c)))
            throw ComponentNotFoundException("Komponenta $c ne postoji u repozitorijumu")
        storage.readJson(Layout.descriptor(c), DescriptorDto::class.java)
    }

    private fun children(c:Coordinates, scopes: Set<String>) : List<Coordinates> =
        descriptor(c).dependencies
            .filter { !it.optional && it.scope in scopes }
            .map { Coordinates(it.organization, it.project, Version(it.version)) }


    private fun resolveNearest(
        roots : List<Coordinates>, scopes: Set<String>, conflicts: MutableList<String>
    ): List<Coordinates>{
        val chosen = LinkedHashMap<Key, Coordinates>()
        val queue = ArrayDeque(roots)
        while (queue.isNotEmpty()) {
            val c = queue.removeFirst()
            val existing = chosen[key(c)]
            if (existing != null) {
                if (existing.version.compareTo(c.version) != 0)
                    conflicts += "${c.organisation}:${c.project}: izabrana ${existing.version}, " +
                            "odbačena ${c.version} (najbliža korenu)"
                continue
            }
            chosen[key(c)] = c
            queue.addAll(children(c, scopes))
        }
        return chosen.values.toList()
    }
    private fun resolveNewest(
        roots: List<Coordinates>, scopes: Set<String>, conflicts: MutableList<String>
    ): List<Coordinates> {
        val floor = HashMap<Key, Version>()   // najveća verzija viđena u prethodnim prolazima
        while (true) {
            val best = HashMap<Key, Version>()
            val requested = HashMap<Key, MutableSet<String>>()
            val visited = LinkedHashSet<Key>()
            val queue = ArrayDeque(roots)
            while (queue.isNotEmpty()) {
                val c = queue.removeFirst()
                val k = key(c)
                requested.getOrPut(k) { mutableSetOf() }.add(c.version.toString())
                val eff = maxOf(c.version, floor[k] ?: c.version)
                best[k] = maxOf(eff, best[k] ?: eff)
                if (!visited.add(k)) continue
                queue.addAll(children(Coordinates(c.organisation, c.project, eff), scopes))
            }
            val changed = best.any { (k, v) -> floor[k]?.compareTo(v) != 0 }
            if (!changed) {
                for ((k, versions) in requested) if (versions.size > 1)
                    conflicts += "${k.organisation}:${k.project}: zatražene verzije $versions, " +
                            "izabrana ${best[k]} (najnovija)"
                return visited.map { Coordinates(it.organisation, it.project, best.getValue(it)) }
            }
            for ((k, v) in best) floor[k] = maxOf(v, floor[k] ?: v)
        }
    }

    fun resolve(roots: List<Coordinates>, targetDir: File, strategy: ConflictStrategy) : ResolutionResult {
        val scopes = transitiveScopes()
        val conflicts = mutableListOf<String>()
        val resolved = when (strategy){
            ConflictStrategy.NEAREST -> resolveNearest(roots, scopes, conflicts)
            ConflictStrategy.NEWEST -> resolveNewest(roots, scopes, conflicts)
        }

        targetDir.mkdirs()
        val components = resolved.map { c ->
            val ext = descriptor(c).extension
            val target = File(targetDir, "${c.organisation}-${c.project}-${c.version}.$ext")
            storage.read(Layout.file(c, ext)).use { input ->
                target.outputStream().use { input.copyTo(it) }
            }
            ResolvedComponent(c, target)
        }
        return ResolutionResult(components, conflicts)
    }

    fun resolveFromFile(listFile: File, targetDir: File, strategy: ConflictStrategy) : ResolutionResult{
        val roots = listFile.readLines()
            .map { it.trim() }
            .filter { it.isNotEmpty() && !it.startsWith('#') }
            .map { Coordinates.parse(it) }
        return resolve(roots, targetDir, strategy)
    }
}