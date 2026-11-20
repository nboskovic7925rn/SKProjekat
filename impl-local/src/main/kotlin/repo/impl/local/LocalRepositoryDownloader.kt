package repo.impl.local
import repo.engine.Storage
import repo.engine.StorageBackedDownloader
import java.io.File


class LocalRepositoryDownloader : StorageBackedDownloader() {
    override fun createStorage(params: Map<String, String>): Storage {
        val putanja = params["path"] ?: throw IllegalArgumentException("Nedostaje parametar path (putanja do foldera repozitorijuma)")
        val adresa = File(putanja)
        return LocalStorage(adresa)
    }

    override val implName = "LOCAL"
}