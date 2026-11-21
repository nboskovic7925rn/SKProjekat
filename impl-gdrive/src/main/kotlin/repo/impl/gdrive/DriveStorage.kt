package repo.impl.gdrive

import com.google.api.client.http.InputStreamContent
import com.google.api.services.drive.Drive
import com.google.api.services.drive.model.File as DriveFile
import repo.engine.Storage
import java.io.FileNotFoundException
import java.io.InputStream

class DriveStorage(private val drive: Drive, private val rootId: String) : Storage {

    private val folderMime = "application/vnd.google-apps.folder"
    private val idCache = mutableMapOf<String, String>()

    private fun split(path: String) = path.split("/").filter { it.isNotEmpty() }
    private fun escape(name: String) = name.replace("\\", "\\\\").replace("'", "\\'")

    private fun findChild(parentId: String, name: String): DriveFile? {
        var token: String? = null
        do {
            val page = drive.files().list()
                .setQ("'$parentId' in parents and name = '${escape(name)}' and trashed = false")
                .setFields("nextPageToken, files(id)")
                .setPageToken(token)
                .execute()
            page.files.firstOrNull()?.let { return it }
            token = page.nextPageToken
        } while (token != null)
        return null
    }

    private fun resolveId(path: String): String? {
        var id = rootId
        var current = ""
        for (part in split(path)) {
            current = if (current.isEmpty()) part else "$current/$part"
            id = idCache[current]
                ?: findChild(id, part)?.id?.also { idCache[current] = it }
                        ?: return null
        }
        return id
    }

    private fun createFolder(parentId: String, name: String): String =
        drive.files().create(
            DriveFile().setName(name).setMimeType(folderMime).setParents(listOf(parentId))
        ).setFields("id").execute().id

    override fun exists(path: String) = resolveId(path) != null

    override fun createDirectory(path: String) {
        var id = rootId
        var current = ""
        for (part in split(path)) {
            current = if (current.isEmpty()) part else "$current/$part"
            id = idCache[current]
                ?: (findChild(id, part)?.id ?: createFolder(id, part)).also { idCache[current] = it }
        }
    }

    override fun read(path: String): InputStream {
        val id = resolveId(path) ?: throw FileNotFoundException(path)
        return drive.files().get(id).executeMediaAsInputStream()
    }

    override fun write(path: String, data: InputStream) {
        val parts = split(path)
        require(parts.isNotEmpty()) { "Putanja ne sme biti prazna" }
        val parentPath = parts.dropLast(1).joinToString("/")
        createDirectory(parentPath)
        val parentId = resolveId(parentPath)!!

        val content = InputStreamContent("application/octet-stream", data)
        val existing = findChild(parentId, parts.last())
        val saved = if (existing != null) {
            drive.files().update(existing.id, DriveFile(), content).setFields("id").execute()
        } else {
            drive.files().create(
                DriveFile().setName(parts.last()).setParents(listOf(parentId)), content
            ).setFields("id").execute()
        }
        idCache[parts.joinToString("/")] = saved.id
    }

    override fun list(path: String): List<String> {
        val id = resolveId(path) ?: return emptyList()
        val names = mutableListOf<String>()
        var token: String? = null
        do {
            val page = drive.files().list()
                .setQ("'$id' in parents and trashed = false")
                .setFields("nextPageToken, files(name)")
                .setPageSize(1000)
                .setPageToken(token)
                .execute()
            names += page.files.map { it.name }
            token = page.nextPageToken
        } while (token != null)
        return names
    }
}