package repo.impl.gdrive

import kotlin.test.*

class DriveStorageManualTest {
    @Ignore("Rucni test: trazi prijavu u Google nalog")
    @Test
    fun fiveOperations() {
        val storage = createDriveStorage(
            mapOf(
                "credentials" to "../config/credentials.json",
                "tokens" to "../tokens",
                "folder" to "SKRepoTest"
            )
        )
        val path = "rs.raf/lib/1.0/pozdrav.txt"
        storage.write(path, "zdravo".byteInputStream())
        assertTrue(storage.exists(path))
        assertEquals(listOf("pozdrav.txt"), storage.list("rs.raf/lib/1.0"))
        assertEquals("zdravo", storage.read(path).bufferedReader().readText())

        storage.write(path, "novo".byteInputStream())   // prepisivanje
        assertEquals("novo", storage.read(path).bufferedReader().readText())
        assertFalse(storage.exists("rs.raf/nema"))
    }
}