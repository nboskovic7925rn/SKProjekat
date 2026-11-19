package repo.impl.local

import repo.engine.Storage
import repo.engine.StorageBackedUploader
import java.io.File


class LocalRepositoryUploader : StorageBackedUploader() {
    override val implName = "LOCAL"
    override fun createStorage(params: Map<String, String>): Storage {
        val putanja = params["path"] ?: throw IllegalArgumentException("Nedostaje parametar path (putanja do foldera repozitorijuma)")
        val adresa = File(putanja)
        return LocalStorage(adresa)
    }

}