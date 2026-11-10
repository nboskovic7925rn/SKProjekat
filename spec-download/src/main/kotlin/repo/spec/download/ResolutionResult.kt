package repo.spec.download

import repo.core.model.Coordinates
import java.io.File

data class ResolvedComponent(val coordinates: Coordinates, val file: File)

/**
 * Rezultat razrešavanja.
 * @property components sve preuzete komponente (zadate i tranzitivne)
 * @property conflicts opisi razrešenih konflikata verzija, korisno za ispis i demonstraciju
 */
class ResolutionResult(
    val components: List<ResolvedComponent>,
    val conflicts: List<String>
)