plugins {
    application
}

dependencies {
    implementation(project(":spec-download"))
    runtimeOnly(project(":impl-local"))
    runtimeOnly(project(":impl-gdrive"))
    runtimeOnly(project(":impl-network"))
}

application {
    mainClass.set("repo.cli.download.MainKt")
}