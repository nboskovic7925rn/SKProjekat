package repo.impl.network

import repo.engine.Storage
import repo.engine.StorageBackedDownloader

class NetworkRepositoryDownloader: StorageBackedDownloader() {
    override val implName = "NETWORK"
    override fun createStorage(params: Map<String, String>): Storage {
        return SftpConnection.createStorage(params)
    }
}