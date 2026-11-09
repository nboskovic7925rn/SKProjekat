package repo.impl.gdrive

import com.google.api.client.http.FileContent
import com.google.api.services.drive.model.File as DriveFile
import java.io.File

// Pokrenite ovaj main bez argumenata. Working directory: koren projekta.
fun main() {
    val drive = GoogleDriveAuth.connect() // Pri prvoj prijavi otvara pregledač.

    // Pronađi postojeći folder u My Drive-u; preskoči foldere u korpi.
    var pageToken: String? = null
    var folder: DriveFile?
    do {
        val page = drive.files().list()
            .setQ("name = 'SKRepo' and mimeType = 'application/vnd.google-apps.folder' " +
                    "and 'root' in parents and trashed = false")
            .setOrderBy("createdTime") // Ako postoje duplikati, koristi najstariji.
            .setPageSize(1)
            .setFields("nextPageToken,files(id)")
            .setPageToken(pageToken)
            .execute()
        folder = page.files.firstOrNull()
        pageToken = page.nextPageToken
    } while (folder == null && pageToken != null)

    // Novi folder pravimo samo ako nijedan nije pronađen.
    val folderId = (folder ?: drive.files().create(
        DriveFile().setName("SKRepo")
            .setMimeType("application/vnd.google-apps.folder")
    ).setFields("id").execute()).id
    println("Folder, folderId=$folderId")

    // Upload: parents određuje folder u koji postavljamo fajl.
    val source = File("samples/pozdrav.txt")
    val uploaded = drive.files().create(
        DriveFile().setName(source.name).setParents(listOf(folderId)),
        FileContent("text/plain", source)
    ).setFields("id").execute()
    println("Postavljeno, fileId=${uploaded.id}")

    // Download: po tom ID-ju preuzimamo sadržaj u lokalni fajl.
    val destination = File("downloads/${uploaded.id}-${source.name}")
    destination.parentFile.mkdirs()
    destination.outputStream().use { output ->
        drive.files().get(uploaded.id).executeMediaAndDownloadTo(output)
    }
    println("Preuzeto: ${destination.absolutePath}")
}
