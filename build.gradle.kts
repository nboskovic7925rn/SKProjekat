plugins {
    kotlin("jvm") version "2.1.0" apply false
    id("org.jetbrains.dokka") version "2.0.0"
}

dependencies {
    dokka(project(":spec-core"))
    dokka(project(":spec-upload"))
    dokka(project(":spec-download"))
}

allprojects {
    group = "rs.raf"
    version = "1.0-SNAPSHOT"

    repositories {
        mavenCentral()
    }
}

subprojects {
    apply(plugin = "org.jetbrains.kotlin.jvm")

    extensions.configure<org.jetbrains.kotlin.gradle.dsl.KotlinJvmProjectExtension> {
        jvmToolchain(23)
    }

    dependencies {
        "testImplementation"(kotlin("test"))
    }

    tasks.withType<Test> {
        useJUnitPlatform()
    }
}
tasks.register("packageAll") {
    dependsOn(":cli-upload:installDist", ":cli-download:installDist", "dokkaGenerate")
}