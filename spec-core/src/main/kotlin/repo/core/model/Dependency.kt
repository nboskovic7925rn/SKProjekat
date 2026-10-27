package repo.core.model

data class Dependency (
    val coordinates : Coordinates,
    val scope : String,
    val optional : Boolean = false
)