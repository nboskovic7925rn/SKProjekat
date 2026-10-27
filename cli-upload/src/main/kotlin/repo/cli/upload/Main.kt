package repo.cli.upload

import repo.spec.upload.RepositoryUploader
import java.util.ServiceLoader

fun main() {
    val impls = ServiceLoader.load(RepositoryUploader::class.java).toList()
    println("Pronadjene implementacije: " + impls.map { it.implName })
}