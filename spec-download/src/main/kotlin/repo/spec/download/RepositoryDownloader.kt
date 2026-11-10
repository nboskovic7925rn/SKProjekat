package repo.spec.download

import repo.core.exception.*
import repo.core.model.*
import java.io.File

interface RepositoryDownloader {
    val implName: String

    /**
     * Povezuje implementaciju sa repozitorijumom. Mora se pozvati pre svih ostalih operacija.
     *
     * @param params podaci za povezivanje; za lokalni repozitorijum ključ "path"
     * @throws IllegalArgumentException ako nedostaje potreban ključ
     */
    fun connect(params: Map<String, String>)

    /**
     * Preuzima zadate komponente i njihove zavisnosti u ciljni folder. Zavisnost se povlači
     * tranzitivno samo ako nije opciona i ako je njen scope u repozitorijumu označen kao tranzitivan.
     * Konflikti verzija se razrešavaju prema zadatoj strategiji.
     *
     * @param roots komponente koje se preuzimaju
     * @param targetDir folder u koji se preuzimaju (pravi se ako ne postoji)
     * @param strategy strategija razrešavanja konflikta verzija
     * @throws ComponentNotFoundException ako neka komponenta ne postoji u repozitorijumu
     */
    fun resolve(
        roots: List<Coordinates>,
        targetDir: File,
        strategy: ConflictStrategy = ConflictStrategy.NEAREST
    ): ResolutionResult

    /**
     * Isto kao resolve, ali se komponente čitaju iz tekstualnog fajla: jedna po liniji u
     * formatu organizacija:projekat:verzija; prazne linije i linije koje počinju sa # se preskaču.
     *
     * @throws IllegalArgumentException ako neka linija nije u ispravnom formatu
     */
    fun resolveFromFile(
        listFile: File,
        targetDir: File,
        strategy: ConflictStrategy = ConflictStrategy.NEAREST
    ): ResolutionResult
}