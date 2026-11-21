plugins {
    `java-library`
}

dependencies {
    implementation(project(":engine"))
    implementation(project(":spec-upload"))
    implementation(project(":spec-download"))
    implementation("com.hierynomus:sshj:0.38.0")

}