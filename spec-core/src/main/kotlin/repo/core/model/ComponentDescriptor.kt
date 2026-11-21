package repo.core.model

/**
 * Metapodaci jedne softverske komponente skladistene u repozitorijumu
 *
 * Sadrzi jedinstvenu identifikacionu klasu Coordinates, ekstenziju i listu njenih zavisnosti
 *
 * @property coordinates Jedinstvene koordinate komponente predstavljene klasom [Coordinates]
 * @property extension Ekstenzija fajla komponente bez tacke (npr. jar, zip)
 * @property dependencies Lista direktnih zavisnosti predstavljene objektima klase [Dependency] koje komponenta
 * zahteva, podrazumevano prazna lista
 */

data class ComponentDescriptor(
    val coordinates : Coordinates,
    val extension : String,
    val dependencies : List<Dependency> = emptyList()
)