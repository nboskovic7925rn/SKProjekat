package repo.impl.local

import repo.engine.Storage
import java.io.File
import java.io.InputStream

class LocalStorage(
    private val root: File
) : Storage {
    override fun exists(path: String): Boolean {
        val adresa = File(root,path)
        return adresa.exists()
    }

    override fun read(path: String): InputStream {
        val adresa = File(root,path)
        return adresa.inputStream()
    }

    override fun write(path: String, data: InputStream) {
        val adresa = File(root, path)
        adresa.parentFile.mkdirs()

        adresa.outputStream().use { izlaz -> data.copyTo(izlaz) }
    }

    override fun list(path: String): List<String> {
        val adresa = File(root, path)
        val sadrzaj = adresa.listFiles().orEmpty()
        val imena = sadrzaj.map { it.name }
        return imena

    }

    override fun createDirectory(path: String) {
        val adresa = File(root,path)
        adresa.mkdirs()
    }

}