package repo.impl.local

import repo.core.model.ComponentDescriptor
import repo.core.model.Coordinates
import repo.core.model.Dependency
import repo.core.model.RepositoryConfig
import repo.spec.upload.RepositoryUploader
import java.io.File

class LocalRepositoryUploader : RepositoryUploader {
    override val implName = "LOCAL"
    override fun connect(params: Map<String, String>) {
        TODO("Not yet implemented")
    }

    override fun initRepository() {
        TODO("Not yet implemented")
    }

    override fun configure(configuration: RepositoryConfig) {
        TODO("Not yet implemented")
    }

    override fun uploadComponent(file: File, descriptor: ComponentDescriptor) {
        TODO("Not yet implemented")
    }

    override fun listOrganisations(): List<String> {
        TODO("Not yet implemented")
    }

    override fun listProjects(organisation: String): List<String> {
        TODO("Not yet implemented")
    }

    override fun listVersions(organisation: String, project: String): List<String> {
        TODO("Not yet implemented")
    }

    override fun getDependencies(coordinates: Coordinates): List<Dependency> {
        TODO("Not yet implemented")
    }
}