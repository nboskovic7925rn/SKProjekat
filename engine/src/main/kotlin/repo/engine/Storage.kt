package repo.engine
import java.io.InputStream

interface Storage{
    fun exists(path: String) : Boolean
    fun read(path: String) : InputStream
    fun write(path: String, data: java.io.InputStream)
    fun list(path: String) : List<String>
    fun createDirectory(path: String)
}