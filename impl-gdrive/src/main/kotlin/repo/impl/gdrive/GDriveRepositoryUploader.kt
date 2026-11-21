package repo.impl.gdrive

import repo.engine.Storage
import repo.engine.StorageBackedUploader

class GDriveRepositoryUploader : StorageBackedUploader() {
    override val implName = "GDRIVE"
    override fun createStorage(params: Map<String, String>): Storage = createDriveStorage(params)
}