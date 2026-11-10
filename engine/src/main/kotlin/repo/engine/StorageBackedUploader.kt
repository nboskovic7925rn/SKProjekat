package repo.engine

import repo.core.model.ComponentDescriptor
import repo.core.model.Coordinates
import repo.core.model.Dependency
import repo.core.model.RepositoryConfig
import repo.spec.upload.RepositoryUploader
import java.io.File
import java.io.FileDescriptor

abstract class StorageBackedUploader : RepositoryUploader {

    private var delegate : StorageUploader? = null

    private fun uploader(): StorageUploader = delegate ?: throw IllegalStateException("Prvo pozovi connect(params)")

    protected abstract fun createStorage(params:Map<String,String>): Storage

    override fun connect(params:Map<String,String>) {
        delegate = StorageUploader(createStorage(params))
    }

    override fun initRepository() = uploader().initRepository()

    override fun configure(configuration: RepositoryConfig) {
        uploader().configure(configuration)
    }
    override fun uploadComponent(file : File, descriptor: ComponentDescriptor) =
        uploader().uploadComponent(file, descriptor)

    override fun listOrganisations(): List<String> = uploader().listOrganisations()

    override fun listProjects(organisation: String): List<String> = uploader().listProjects(organisation)

    override fun listVersions(organisation: String, project: String): List<String> = uploader().listVersions(organisation, project)

    override fun getDependencies(coordinates: Coordinates): List<Dependency> = uploader().getDependecies(coordinates)

}