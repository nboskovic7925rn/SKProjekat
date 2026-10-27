plugins {
    application
}

dependencies {
    implementation(project(":spec-upload"))
    runtimeOnly(project(":impl-local"))
}

application {
    mainClass.set("repo.cli.upload.MainKt")
}