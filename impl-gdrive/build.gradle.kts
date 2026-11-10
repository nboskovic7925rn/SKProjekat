plugins {
    application
}

repositories { mavenCentral() }

dependencies {
    implementation(project(":engine"))
    implementation(project(":cli-upload"))
    implementation(project(":cli-download"))
    implementation("com.google.api-client:google-api-client:2.9.2")
    implementation("com.google.oauth-client:google-oauth-client-jetty:1.39.0")
    implementation("com.google.apis:google-api-services-drive:v3-rev20261011-2.0.0")
}

application {
    mainClass.set("repo.impl.gdrive.MainKt")
}