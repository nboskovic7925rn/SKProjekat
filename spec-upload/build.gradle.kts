plugins {
    `java-library`
    id("org.jetbrains.dokka")
}

dependencies {
    api(project(":spec-core"))
}