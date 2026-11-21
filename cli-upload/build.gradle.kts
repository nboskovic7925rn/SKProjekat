plugins {
    application
}

dependencies {
    implementation(project(":spec-upload"))
    runtimeOnly(project(":impl-local"))
    runtimeOnly(project(":impl-gdrive"))
    runtimeOnly(project(":impl-network"))
}

application {
    mainClass.set("repo.cli.upload.MainKt")
}