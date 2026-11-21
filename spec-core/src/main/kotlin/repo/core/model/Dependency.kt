package repo.core.model

/**
 * Direktna zavisnost jedne komponente prema drugoj komponenti u istom repozitorijumu
 *
 * @property coordinates Koordinate komponente od koje postoji zavisnost
 * @property scope Opseg zavisnosti (npr. compile, test, runtime...), mora biti definisan u konfiguraciji repozitorijuma
 * @property optional Oznacava da li je zavisnost opciona ili obavezna. Ukoliko je opciona (true), ne povlaci se kao tranzitivna zavisnost prilikom preuzimanja
 */

data class Dependency (
    val coordinates : Coordinates,
    val scope : String,
    val optional : Boolean = false
)