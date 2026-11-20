package repo.core.model

/**
 * Jedinstveni identifikator komponente u repozitorijumu
 *
 * Svaka komponenta je jednoznacno predstavljena kombinacijom naziva organizacije, naziva projekta
 * i verzije (po uzoru na Maven)
 *
 * @property organisation Naziv organizacije koja kreira komponentu
 * @property project Naziv konkretnog projekta ili biblioteke
 * @property version Verzija komponente ili biblioteke predstavljena klasom [Version]
 */

data class Coordinates(
    val organisation : String,
    val project : String,
    val version : Version

){
    override fun toString() = "$organisation:$project:$version"

    companion object{
        /**
         * Parsira string formata organisation:project:version u objekat [Coordinates]
         *
         * @param text ulazni String sadrzi tri dela razdvojenih dvotackom ':'
         * @return parsirani objekat [Coordinates]
         * @throws IllegalArgumentException ukoliko text nije pravilno razdvojen dvotackom ili ako parsirana klasa
         * verzije nije validna
         */
        fun parse(text: String) : Coordinates{
            val parts = text.trim().split(":")
            require(parts.size == 3 && parts.none{it.isBlank()}){
                "Ocekivan format - organizacija:projekat:verzija, dobijeno - $text"
            }
            return Coordinates(parts[0], parts[1], Version(parts[2]))
        }
    }
}