package repo.engine

import repo.core.model.ConflictStrategy
import repo.core.model.Coordinates
import repo.spec.download.RepositoryDownloader
import repo.spec.download.ResolutionResult
import java.io.File

abstract class StorageBackedDownloader : RepositoryDownloader{

    private var delegate : StorageDownloader ?= null

    private fun downloader() : StorageDownloader =
        delegate ?: throw IllegalStateException("Prvo pozovi connect(params)")

    protected abstract fun createStorage(params: Map<String, String>) : Storage

    override fun connect(params: Map<String, String>) {
        delegate = StorageDownloader(createStorage(params))
    }

    override fun resolve(roots: List<Coordinates>, targetDir: File, strategy: ConflictStrategy): ResolutionResult {
        downloader().resolve(roots, targetDir, strategy)
    }

    override fun resolveFromFile(listFile: File, targetDir: File, strategy: ConflictStrategy): ResolutionResult {
        downloader().resolveFromFile(listFile, targetDir, strategy)
    }

}