package repo.engine

import com.google.gson.Gson
import repo.core.model.Coordinates
import java.io.ByteArrayInputStream
import java.io.InputStreamReader

internal data class ScopeDto(val name: String, val transitive: Boolean)
internal data class ConfigDto(val allowedExtensions: List<String>, val scopes: List<ScopeDto>)
internal data class DependencyDto(
    val organization: String, val project: String, val version: String,
    val scope: String, val optional: Boolean
)
internal data class DescriptorDto(val extension: String, val dependencies: List<DependencyDto>)

internal object Layout {
    const val CONFIG = "repo-config.json"
    private fun dir(c: Coordinates) = "${c.organisation}/${c.project}/${c.version}"
    fun descriptor(c: Coordinates) = "${dir(c)}/descriptor.json"
    fun file(c: Coordinates, ext: String) = "${dir(c)}/${c.project}-${c.version}.$ext"
}

private val gson = Gson()

internal fun <T> Storage.readJson(path: String, type: Class<T>): T =
    read(path).use { gson.fromJson(InputStreamReader(it, Charsets.UTF_8), type) }

internal fun Storage.writeJson(path: String, value: Any) =
    write(path, ByteArrayInputStream(gson.toJson(value).toByteArray(Charsets.UTF_8)))