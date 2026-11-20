package repo.engine

import java.io.ByteArrayInputStream
import java.io.FileNotFoundException
import java.io.InputStream

class InMemoryStorage : Storage {
    private val files = mutableMapOf<String, ByteArray>()
    private val dirs = mutableSetOf<String>()

    private fun norm(p: String) = p.trim('/')

    override fun exists(path: String): Boolean {
        val p = norm(path)
        return p.isEmpty() || p in files || p in dirs
    }

    override fun read(path: String): InputStream =
        ByteArrayInputStream(files[norm(path)] ?: throw FileNotFoundException(path))

    override fun write(path: String, data: InputStream) {
        val p = norm(path)
        createDirectory(p.substringBeforeLast('/', ""))
        files[p] = data.readBytes()
    }

    override fun createDirectory(path: String) {
        var current = ""
        for (part in norm(path).split("/").filter { it.isNotEmpty() }) {
            current = if (current.isEmpty()) part else "$current/$part"
            dirs += current
        }
    }

    override fun list(path: String): List<String> {
        val p = norm(path)
        val prefix = if (p.isEmpty()) "" else "$p/"
        return (files.keys + dirs)
            .filter { it.startsWith(prefix) && it.length > prefix.length }
            .map { it.substring(prefix.length) }
            .filter { !it.contains('/') }
    }
}