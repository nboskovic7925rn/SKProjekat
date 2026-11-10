plugins {
    `java-library`
}

dependencies {
    implementation(project(":engine"))
    implementation(project(":spec-upload"))
    implementation(project(":spec-download"))
}