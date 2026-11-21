package repo.core.model

/**
 * Strategija razresavanja konflikta prilikom preuzimanja komponenti i njihovih tranzitivnih zavisnosti
 *
 * Koristi se kada vise komponenti u stablu zavisnosti zahteva razlicite verzije istog projekta (i iste organizacije, i istog projekta)
 */

enum class ConflictStrategy {
    /**
     * Bira najnoviju verziju komponente medju svim verzijama
     */
    NEWEST,

    /**
     * Bira verziju komponente najblizu korenu stabla zavisnosti (po uzoru na Maven posredovanje zavisnosti)
     */
    NEAREST
}