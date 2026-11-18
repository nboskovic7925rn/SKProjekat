package repo.impl.gdrive

import com.google.api.services.drive.model.File as DriveFile
import repo.engine.Storage
import java.io.File
import com.google.api.services.drive.Drive as Drive

private const val FOLDER_MIME = "application/vnd.google-apps.folder"

internal fun openRootFolder(drive: Drive, name: String): String{

    val safe = name.replace("\\", "\\\\").replace("'", "\\'")
    var token: String? = null
    do {
        val page = drive.files().list()
            .setQ("name = '$safe' and mimeType = '$FOLDER_MIME' and 'root' in parents and trashed = false")
            .setOrderBy("createdTime")
            .setPageSize(1)
            .setFields("nextPageToken,files(id)")
            .setPageToken(token)
            .execute()
        page.files.firstOrNull()?.let { return it.id }
        token = page.nextPageToken
    } while (token != null)

    return drive.files().create(DriveFile().setName(name).setMimeType(FOLDER_MIME))
        .setFields("id").execute().id
}

internal fun createDriveStorage(params: Map<String, String>): Storage {
    val credentials = params["credentials"] ?: System.getenv("GOOGLE_DRIVE_CREDENTIALS")
    ?: throw IllegalArgumentException("Nedostaje parametar 'credentials' (putanja do OAuth JSON-a).")
    val folder = params["folder"]?.takeIf { it.isNotBlank() }
        ?: throw IllegalArgumentException("Nedostaje parametar 'folder' (ime foldera repozitorijuma).")
    val tokens = params["tokens"] ?: System.getenv("GOOGLE_DRIVE_TOKENS") ?: "tokens"

    val drive = GoogleDriveAuth.connect(File(credentials), File(tokens))
    return DriveStorage(drive, openRootFolder(drive, folder))
}