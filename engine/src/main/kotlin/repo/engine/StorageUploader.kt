package repo.engine

import com.google.gson.Gson
import repo.core.exception.*
import repo.core.model.*
import java.io.ByteArrayInputStream
import java.io.File
import java.io.InputStreamReader

private data class ScopeDto(val name: String, val transitive: Boolean)
private data class ConfigDto(val allowedExtensions: List<String>, val scopes: List<ScopeDto>)
private data class DependencyDto(val organisation: String, val project: String, val version: String, val scope: String, val optional: Boolean)
private data class DescriptorDto(val extension: String, val dependencies: List<DependencyDto>)

class StorageUploader(private val storage: Storage){

    private val gson = Gson()
    private val configPath = "repo-config.json"

    private fun dir(c: Coordinates) = "${c.organisation}/${c.project}/${c.version}"
    private fun descriptorPath(c: Coordinates) = "${dir(c)}/descriptor.json"
    private fun filePath(c: Coordinates, ext: String) = "${dir(c)}/${c.project}-${c.version}.$ext"

    private fun writeJson(path: String, value: Any) =
        storage.write(path, ByteArrayInputStream(gson.toJson(value).toByteArray(Charsets.UTF_8)))

    private fun <T> readJson(path: String, type: Class<T>): T =
        storage.read(path).use { gson.fromJson(InputStreamReader(it, Charsets.UTF_8), type) }

    private fun loadConfig(): RepositoryConfig{
        if(!storage.exists(configPath))
            throw RepositoryException("Repozitorijum nije konfigurisan (nedostaje $configPath).")
        val dto = readJson(configPath, ConfigDto::class.java)
        return RepositoryConfig(
            dto.allowedExtensions.toSet(),
            dto.scopes.map { ScopeDefinition(it.name, it.transitive) }
        )
    }

    fun initRepository(){
        if(storage.list("").isNotEmpty())
            throw RepositoryAlreadyExistsException("Repozitorijum vec postoji na ovoj lokaciji")
        storage.createDirectory("")
    }

    fun configure(config: RepositoryConfig){
        writeJson(
            configPath,
            ConfigDto(
                config.allowedExtensions.toList(),
                config.scopes.map { ScopeDto(it.name, it.transitive) }
            )
        )
    }

    fun uploadComponent(file: File, descriptor: ComponentDescriptor){
        val config = loadConfig()
        val c = descriptor.coordinates

        if(descriptor.extension !in config.allowedExtensions)
            throw ExtensionNotAllowedException("Ekstenzija ${descriptor.extension} nije dozvoljena.")

        if(storage.exists(descriptorPath(c)))
            throw ComponentAlreadyExistsException("Komponenta ${descriptorPath(c)} vec postoji.")

        val coords = descriptor.dependencies.map { it.coordinates }
        if(coords.size != coords.toSet().size)
            throw InvalidDependencyException("Zavisnosti sadrze duplikate")

        val knownScopes = config.scopes.map { it.name }.toSet()
        for(dep in descriptor.dependencies) {
            if(dep.scope !in knownScopes)
                throw InvalidDependencyException("Scope {${dep.scope} nije podrzan u repozitorijumu")
            if(!storage.exists(descriptorPath(dep.coordinates)))
                throw MissingDependencyException("Zavisnost {$dep.coordinates} nije u repozitorijumu")
        }

        file.inputStream().use { storage.write(filePath(c, descriptor.extension), it) }
        writeJson(
            descriptorPath(c),
            DescriptorDto(
                descriptor.extension,
                descriptor.dependencies.map {
                    DependencyDto(
                        it.coordinates.organisation, it.coordinates.project,
                        it.coordinates.version.toString(), it.scope, it.optional
                    )
                }
            )
        )
    }

    fun listOrganisations() : List<String> = storage.list("").filter { it != configPath }

    fun listProjects(organisation: String) : List<String> = storage.list(organisation)

    fun listVersions(organisation: String, project: String) : List<String> =
        storage.list("$organisation/$project").sortedBy { Version(it) }

    fun getDependecies(coordinates: Coordinates) : List<Dependency>{
        if(!storage.exists(descriptorPath(coordinates)))
            throw ComponentNotFoundException("Komponenta $coordinates ne postoji")
        val dto = readJson(descriptorPath(coordinates), DescriptorDto::class.java)
        return dto.dependencies.map {
            Dependency(
                Coordinates(it.organisation, it.project, Version(it.version)), it.scope, it.optional
            )
        }
    }
}