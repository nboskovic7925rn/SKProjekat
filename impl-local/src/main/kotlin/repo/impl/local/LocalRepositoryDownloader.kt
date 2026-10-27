package repo.impl.local
import repo.core.exception.RepositoryException
import repo.spec.download.RepositoryDownloader

class LocalRepositoryDownloader : RepositoryDownloader {
    override val implName = "LOCAL"
}