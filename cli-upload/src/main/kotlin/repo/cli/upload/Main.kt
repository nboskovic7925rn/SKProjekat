package repo.cli.upload

import com.google.gson.Gson
import com.google.gson.JsonParseException
import repo.core.exception.RepositoryException
import repo.core.model.ComponentDescriptor
import repo.core.model.Coordinates
import repo.core.model.Dependency
import repo.core.model.RepositoryConfig
import repo.spec.upload.RepositoryUploader
import java.io.File
import java.util.ServiceLoader

fun main(args: Array<String>) {
    if (args.size < 2) {
        println("Upotreba: IMPLEMENTACIJA komanda [opcije]. Komande: init configure upload list deps")
        return
    }

    val uploader = try {
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
        uploader.connect(opcije)
        when (args[1]) {
            "init" -> {
                uploader.initRepository()
                println("Repozitorijum je napravljen")
            }
            "configure" -> {
                if (argumenti.isEmpty()) {
                    throw IllegalArgumentException("Fali putanja do JSON fajla sa pravilima")
                }
                val fajl = File(argumenti[0])
                if (!fajl.exists()) {
                    throw IllegalArgumentException("Fajl $fajl ne postoji")
                }
                val tekst = fajl.readText()
                val config = Gson().fromJson(tekst, RepositoryConfig::class.java)
                uploader.configure(config)
                println("Konfiguracija je upisana")
            }
            "upload" -> {
                if (argumenti.size < 2) {
                    throw IllegalArgumentException("Upotreba: upload <fajl> organizacija:projekat:verzija [--deps zavisnosti]")
                }
                val fajl = File(argumenti[0])
                if (!fajl.exists()) {
                    throw IllegalArgumentException("Fajl $fajl ne postoji")
                }
                val koordinate = Coordinates.parse(argumenti[1])
                val ekstenzija = fajl.extension
                val zavisnosti = opcije["deps"]?.let { parsirajZavisnosti(it) } ?: emptyList()
                val deskriptor = ComponentDescriptor(koordinate, ekstenzija, zavisnosti)
                uploader.uploadComponent(fajl, deskriptor)
                println("Komponenta ${argumenti[1]} je ubacena")
            }
            "list" -> {
                val rezultat = when (argumenti.size) {
                    0 -> uploader.listOrganisations()
                    1 -> uploader.listProjects(argumenti[0])
                    2 -> uploader.listVersions(argumenti[0], argumenti[1])
                    else -> throw IllegalArgumentException(
                        "Upotreba: list [organizacija [projekat]]"
                    )
                }
                ispisiListu(rezultat)
            }
            "deps" -> {
                if (argumenti.isEmpty()) {
                    throw IllegalArgumentException("Upotreba: deps organizacija:projekat:verzija")
                }
                val koordinate = Coordinates.parse(argumenti[0])
                val zavisnosti = uploader.getDependencies(koordinate)
                if (zavisnosti.isEmpty()) {
                    println("Nema rezultata")
                }
                for (zavisnost in zavisnosti) {
                    println(opisZavisnosti(zavisnost))
                }
            }
            else -> println("Nepoznata komanda ${args[1]}")
        }
    } catch (e: RepositoryException) {
        println(e.message)
    } catch (e: IllegalArgumentException) {
        println(e.message)
    } catch (e: JsonParseException) {
        println("Neispravan JSON: ${e.message}")
    }
}

fun ispisiListu(lista: List<String>) {
    if (lista.isEmpty()) {
        println("Nema rezultata")
    }
    for (element in lista) {
        println(element)
    }
}

fun opisZavisnosti(zavisnost: Dependency): String {
    val oznake = if (zavisnost.optional) "${zavisnost.scope}, optional" else zavisnost.scope
    return "${zavisnost.coordinates} ($oznake)"
}

fun parsirajZavisnost(tekst: String): Dependency {
    val delovi = tekst.trim().split(":")
    if (delovi.size !in 4..5) {
        throw IllegalArgumentException(
            "Nevalidan format zavisnosti '$tekst', ispravan format je organizacija:projekat:verzija:scope[:optional]"
        )
    }
    val koordinate = Coordinates.parse(delovi.take(3).joinToString(":"))
    val scope = delovi[3]
    if (scope.isBlank()) {
        throw IllegalArgumentException("Zavisnosti '$tekst' fali scope")
    }
    val peti = delovi.getOrNull(4)
    if (peti != null && peti != "optional") {
        throw IllegalArgumentException("Poslednji deo zavisnosti '$tekst' mora biti 'optional'")
    }
    return Dependency(koordinate, scope, peti != null)
}

fun parsirajZavisnosti(tekst: String): List<Dependency> =
    tekst.split(",").map { parsirajZavisnost(it) }

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

fun implementacijaPoImenu(ime: String): RepositoryUploader {
    val impls = ServiceLoader.load(RepositoryUploader::class.java).toList()
    val implN = impls.firstOrNull { it.implName.equals(ime, ignoreCase = true) }
        ?: throw IllegalArgumentException("Nepoznata implementacija $ime. Dostupne: ${impls.map { it.implName }}")
    return implN
}