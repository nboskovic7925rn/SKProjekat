plugins {
    application
}

dependencies {
    implementation(project(":spec-download"))
    runtimeOnly(project(":impl-local"))
    runtimeOnly(project(":impl-gdrive"))
}

application {
    mainClass.set("repo.cli.download.MainKt")
}