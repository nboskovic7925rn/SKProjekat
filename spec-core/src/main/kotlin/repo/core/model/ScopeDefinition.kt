package repo.core.model

/**
 * Definicija jednog opsega (scope-a) zavisnosti u okviru konfiguracije repozitorijuma
 *
 * @property name Konkretno ime opsega (npr. compile, test...)
 * @property transitive Oznacava da li se zavisnosti ovog opsega automatski povlace dalje kao tranzitivne zavisnosti (true), ili vaze samo direktno (false)
 */

data class ScopeDefinition(
    val name : String,
    val transitive : Boolean
)