package repo.core.model

data class Coordinates(
    val organisation : String,
    val project : String,
    val version : String

){
    override fun toString() = "$organisation:$project:$version"

    companion object{
        fun parse(text: String) : Coordinates{
            val parts = text.trim().split(":")
            require(parts.size == 3 && parts.none{it.isBlank()}){
                "Ocekivan format - organizacija:projekat:verzija, dobijeno - $text"
            }
            return Coordinates(parts[0], parts[1], parts[2])
        }
    }
}