package repo.core.model

data class ComponentDescriptor(
    val coordinates : Coordinates,
    val extension : String,
    val dependencies : List<Dependency> = emptyList()
)