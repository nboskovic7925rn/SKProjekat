plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.8.0"
}
rootProject.name = "SKProjekat1"

include(
    "spec-core",
    "spec-upload",
    "spec-download",
    "impl-local",
    "cli-upload",
    "cli-download"
)

