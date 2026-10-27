package repo.cli.download

import repo.spec.download.RepositoryDownloader
import java.util.ServiceLoader

fun main(){
    val impls = ServiceLoader.load(RepositoryDownloader::class.java).toList()
    println("Pronadjene implementacije: " + impls.map{it.implName})
}