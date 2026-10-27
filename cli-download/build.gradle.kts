plugins {
    application
}

dependencies {
    implementation(project(":spec-download"))
    runtimeOnly(project(":impl-local"))
}

application {
    mainClass.set("repo.cli.download.MainKt")
}