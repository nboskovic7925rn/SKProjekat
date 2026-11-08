package repo.core.model

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


