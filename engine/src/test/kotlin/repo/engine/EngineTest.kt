package repo.engine

import repo.core.exception.*
import repo.core.model.*
import repo.spec.download.ResolutionResult
import java.io.File
import java.nio.file.Files
import kotlin.test.*

class EngineTest {

    private val config = RepositoryConfig(
        allowedExtensions = setOf("jar"),
        scopes = listOf(ScopeDefinition("compile", true), ScopeDefinition("test", false))
    )

    private lateinit var uploader: StorageUploader
    private lateinit var downloader: StorageDownloader
    private lateinit var tmp: File

    @BeforeTest
    fun setUp() {
        val storage = InMemoryStorage()
        uploader = StorageUploader(storage)
        downloader = StorageDownloader(storage)
        uploader.initRepository()
        uploader.configure(config)
        tmp = Files.createTempDirectory("engine-test").toFile()
    }

    private fun dep(coords: String, scope: String = "compile", optional: Boolean = false) =
        Dependency(Coordinates.parse(coords), scope, optional)

    private fun upload(coords: String, vararg deps: Dependency, ext: String = "jar") {
        val file = File(tmp, "src.$ext").apply { writeText("sadržaj $coords") }
        uploader.uploadComponent(file, ComponentDescriptor(Coordinates.parse(coords), ext, deps.toList()))
    }

    private fun resolve(strategy: ConflictStrategy, vararg roots: String): ResolutionResult =
        downloader.resolve(roots.map { Coordinates.parse(it) }, File(tmp, "out-$strategy"), strategy)

    private fun names(r: ResolutionResult) = r.components.map { it.coordinates.toString() }.toSet()

    //upload

    @Test
    fun duplicateComponentIsRejected() {
        upload("rs:A:1.0")
        assertFailsWith<ComponentAlreadyExistsException> { upload("rs:A:1.0") }
    }

    @Test
    fun extensionNotAllowedIsRejected() {
        assertFailsWith<ExtensionNotAllowedException> { upload("rs:A:1.0", ext = "exe") }
    }

    @Test
    fun missingDependencyIsRejected() {
        assertFailsWith<MissingDependencyException> { upload("rs:A:1.0", dep("rs:Nema:1.0")) }
    }

    @Test
    fun unsupportedScopeIsRejected() {
        upload("rs:B:1.0")
        assertFailsWith<InvalidDependencyException> { upload("rs:A:1.0", dep("rs:B:1.0", "banana")) }
    }

    //download

    @Test
    fun nonTransitiveAndOptionalDependenciesAreSkipped() {
        upload("rs:T:1.0"); upload("rs:O:1.0"); upload("rs:C:1.0")
        upload("rs:A:1.0", dep("rs:T:1.0", "test"), dep("rs:O:1.0", optional = true), dep("rs:C:1.0"))

        val r = resolve(ConflictStrategy.NEAREST, "rs:A:1.0")

        assertEquals(setOf("rs:A:1.0", "rs:C:1.0"), names(r))
        assertTrue(r.components.all { it.file.exists() })
    }

    @Test
    fun conflictStrategiesPickDifferentVersions() {
        upload("rs:D:1.0"); upload("rs:D:2.0")
        upload("rs:A:1.0", dep("rs:D:1.0"))
        upload("rs:Y:1.0", dep("rs:D:2.0"))
        upload("rs:X:1.0", dep("rs:Y:1.0"))

        val nearest = resolve(ConflictStrategy.NEAREST, "rs:A:1.0", "rs:X:1.0")
        val newest = resolve(ConflictStrategy.NEWEST, "rs:A:1.0", "rs:X:1.0")

        assertTrue("rs:D:1.0" in names(nearest) && "rs:D:2.0" !in names(nearest))
        assertTrue("rs:D:2.0" in names(newest) && "rs:D:1.0" !in names(newest))
    }
}