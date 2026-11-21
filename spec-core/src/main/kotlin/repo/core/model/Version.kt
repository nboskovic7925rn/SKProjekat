package repo.core.model

/**
 *
 * Numericka verzija komponente (npr. 1.0.0, 2.1.4) koja omogucava njihovo medjusobno poredjenje
 *
 * Verzije se dele po tackama na nenegativne cele brojeve i porede po komponentama
 * Krace verzije se dopunjavaju nulama sa desne strane (1.2 = 1.2.0)
 *
 * npr - 1.2.1 > 1.2
 *
 * @property text Izvorni stringovni zapis verzije
 * @throws IllegalArgumentException Ukoliko string sadrzi nenumericke delove ili negativne brojeve
 */

data class Version(
    val text: String
): Comparable<Version>{
    companion object{
        private fun parse(text: String): List<Int> {
            return text.trim().split(".").map { txt ->
                val broj = requireNotNull(txt.toIntOrNull()) { "Nevalidan unos verzije: $text" }
                require(broj >= 0) { "Verzija ne sme imati negativne brojeve: $text" }
                broj
            }
        }
    }
    private val lista: List<Int> = parse(text)

    /**
     * Poredi ovu verziju sa drugom verzijom numericki po segmentima.
     *
     * @param other Druga verzija sa kojom se vrsi poređenje.
     * @return Negativan broj ako je ova verzija starija, `0` ako su jednake, ili pozitivan broj ako je novija od [other].
     */
    override fun compareTo(other: Version): Int {
    val kraj = maxOf(this.lista.size,other.lista.size)
        for(i in 0 until kraj){
            val a = this.lista.getOrElse(i){0}
            val b = other.lista.getOrElse(i){0}
            val rezultat = a.compareTo(b)
            if(rezultat != 0){
                return rezultat
            }
        }
        return 0
    }

    override fun toString(): String {
        return text
    }

}


