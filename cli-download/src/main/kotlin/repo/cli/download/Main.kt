package repo.cli.download

import repo.core.exception.RepositoryException
import repo.core.model.ConflictStrategy
import repo.spec.download.RepositoryDownloader
import java.io.File
import java.util.ServiceLoader

fun main(args: Array<String>) {
    if (args.size < 2) {
        println("Upotreba: IMPLEMENTACIJA komanda [opcije]. Komande: download")
        return
    }

    val downloader = try {
        implementacijaPoImenu(args[0])
    } catch (e: IllegalArgumentException) {
        println(e.message)
        return
    }

    val (opcije, argumenti) = try {
        parsirajArgumente(args)
    } catch (e: IllegalArgumentException) {
        println(e.message)
        return
    }

    try {
        downloader.connect(opcije)
        when (args[1]) {
            "download" -> {
                if (argumenti.size < 2) {
                    throw IllegalArgumentException(
                        "Upotreba: download <spisak.txt> <ciljni folder> [--strategy newest|nearest]"
                    )
                }
                val spisak = File(argumenti[0])
                val ciljani = File(argumenti[1])
                if (!spisak.exists()) {
                    throw IllegalArgumentException("Fajl $spisak ne postoji")
                }

                val strategija = pretvoriStrategiju(opcije["strategy"])
                val rezultat = downloader.resolveFromFile(spisak, ciljani, strategija)

                println("Strategija: $strategija")
                println("Preuzete komponente:")
                for (komponenta in rezultat.components) {
                    println("  ${komponenta.coordinates} -> ${komponenta.file}")
                }
                if (rezultat.conflicts.isNotEmpty()) {
                    println("Razreseni konflikti verzija:")
                    for (konflikt in rezultat.conflicts) {
                        println("  $konflikt")
                    }
                }
            }
            else -> println("Nepoznata komanda ${args[1]}")
        }
    } catch (e: RepositoryException) {
        println(e.message)
    } catch (e: IllegalArgumentException) {
        println(e.message)
    }
}

fun pretvoriStrategiju(tekst: String?): ConflictStrategy {
    if (tekst == null) {
        return ConflictStrategy.NEAREST
    }
    return try {
        ConflictStrategy.valueOf(tekst.uppercase())
    } catch (e: IllegalArgumentException) {
        throw IllegalArgumentException("Nepoznata strategija '$tekst', dozvoljeno je newest ili nearest")
    }
}

fun implementacijaPoImenu(ime: String): RepositoryDownloader {
    val impls = ServiceLoader.load(RepositoryDownloader::class.java).toList()
    val implN = impls.firstOrNull { it.implName.equals(ime, ignoreCase = true) }
        ?: throw IllegalArgumentException("Nepoznata implementacija $ime. Dostupne: ${impls.map { it.implName }}")
    return implN
}

fun parsirajArgumente(args: Array<String>): Pair<Map<String, String>, List<String>> {
    val ostatak = args.drop(2)
    var brojac = 0
    val mapa = mutableMapOf<String, String>()
    val lista = mutableListOf<String>()

    while (brojac < ostatak.size) {
        if (ostatak[brojac].startsWith("--")) {
            if (brojac + 1 < ostatak.size) {
                mapa[ostatak[brojac].removePrefix("--")] = ostatak[brojac + 1]
                brojac += 2
            } else {
                throw IllegalArgumentException("Opciji ${ostatak[brojac]} fali vrednost")
            }
        } else {
            lista.add(ostatak[brojac])
            brojac += 1
        }
    }
    return Pair(mapa, lista)
}