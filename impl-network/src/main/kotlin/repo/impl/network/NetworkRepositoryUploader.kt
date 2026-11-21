package repo.impl.network

import repo.engine.Storage
import repo.engine.StorageBackedUploader

class NetworkRepositoryUploader: StorageBackedUploader() {
    override val implName = "NETWORK"
    override fun createStorage(params: Map<String, String>): Storage {
        return SftpConnection.createStorage(params)
    }
}