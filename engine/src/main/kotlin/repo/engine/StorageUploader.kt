package repo.engine

import repo.core.exception.*
import repo.core.model.*
import java.io.File


class StorageUploader(private val storage: Storage){



    private fun loadConfig(): RepositoryConfig{
        if(!storage.exists(Layout.CONFIG))
            throw RepositoryException("Repozitorijum nije konfigurisan (nedostaje ${Layout.CONFIG}).")
        val dto = storage.readJson(Layout.CONFIG, ConfigDto::class.java)
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
        storage.writeJson(
            Layout.CONFIG,
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

        if(storage.exists(Layout.descriptor(c)))
            throw ComponentAlreadyExistsException("Komponenta ${Layout.descriptor(c)} vec postoji.")

        val coords = descriptor.dependencies.map { it.coordinates }
        if(coords.size != coords.toSet().size)
            throw InvalidDependencyException("Zavisnosti sadrze duplikate")

        val knownScopes = config.scopes.map { it.name }.toSet()
        for(dep in descriptor.dependencies) {
            if(dep.scope !in knownScopes)
                throw InvalidDependencyException("Scope {${dep.scope} nije podrzan u repozitorijumu")
            if(!storage.exists(Layout.descriptor(dep.coordinates)))
                throw MissingDependencyException("Zavisnost {$dep.coordinates} nije u repozitorijumu")
        }

        file.inputStream().use { storage.write(Layout.file(c, descriptor.extension), it) }
        storage.writeJson(
            Layout.descriptor(c),
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

    fun listOrganisations() : List<String> = storage.list("").filter { it != Layout.CONFIG }

    fun listProjects(organisation: String) : List<String> = storage.list(organisation)

    fun listVersions(organisation: String, project: String) : List<String> =
        storage.list("$organisation/$project").sortedBy { Version(it) }

    fun getDependecies(coordinates: Coordinates) : List<Dependency>{
        if(!storage.exists(Layout.descriptor(coordinates)))
            throw ComponentNotFoundException("Komponenta $coordinates ne postoji")
        val dto = storage.readJson(Layout.descriptor(coordinates), DescriptorDto::class.java)
        return dto.dependencies.map {
            Dependency(
                Coordinates(it.organization, it.project, Version(it.version)), it.scope, it.optional
            )
        }
    }
}