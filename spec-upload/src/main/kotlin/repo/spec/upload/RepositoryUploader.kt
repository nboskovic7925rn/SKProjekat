package repo.spec.upload

import java.io.File
import repo.core.exception.*
import repo.core.model.*

interface RepositoryUploader {
    val implName: String

    /**
     * Povezuje implementaciju sa repozitorijumom na zadatoj lokaciji.
     * Mora se pozvati pre svih ostalih operacija.
     *
     * @param params podaci za povezivanje; za lokalni repozitorijum ključ "path" sa putanjom do foldera
     * @throws IllegalArgumentException ako u params nije prosleđen potreban ključ (npr. "path")
     */
    fun connect(params: Map<String, String>)

    /**
     * Pravi prazan koren repozitorijuma na lokaciji zadatoj u connect.
     * Mora se pozvati posle connect.
     *
     * @throws RepositoryAlreadyExistsException ako se pokuša inicijalizacija foldera u kojem repozitorijum već postoji
     */
    fun initRepository()

    /**
     * Upisuje pravila repozitorijuma (dozvoljene ekstenzije i scope-ove).
     * Mora se pozvati posle connect.
     *
     * @param configuration podaci o dozvoljenim ekstenzijama i scope-ovima
     */
    fun configure(configuration: RepositoryConfig)

    /**
     * Ubacuje komponentu u repozitorijum.
     * Mora se pozvati posle connect.
     *
     * @param file fajl komponente koji se kopira u repozitorijum
     * @param descriptor identifikacija komponente i njene zavisnosti
     * @throws ComponentAlreadyExistsException ako kombinacija organizacija, projekat i verzija već postoji
     * @throws MissingDependencyException ako neka navedena zavisnost nije u repozitorijumu
     * @throws InvalidDependencyException ako zavisnost ima scope koji nije definisan u konfiguraciji ili je navedena više puta
     * @throws ExtensionNotAllowedException ako repozitorijum ne podržava ekstenziju fajla
     */
    fun uploadComponent(file: File, descriptor: ComponentDescriptor)

    /**
     * Vraća spisak organizacija koje postoje u repozitorijumu.
     *
     * @return spisak imena organizacija; prazna lista ako ne postoji nijedna
     */
    fun listOrganisations(): List<String>

    /**
     * Vraća spisak projekata jedne organizacije.
     *
     * @param organisation naziv organizacije
     * @return spisak imena projekata; prazna lista ako organizacija nema projekata ili ne postoji
     */
    fun listProjects(organisation: String): List<String>

    /**
     * Vraća spisak verzija jednog projekta.
     *
     * @param organisation naziv organizacije
     * @param project naziv projekta
     * @return spisak verzija; prazna lista ako projekat nema verzija ili ne postoji
     */
    fun listVersions(organisation: String, project: String): List<String>

    /**
     * Vraća zavisnosti jedne komponente.
     *
     * @param coordinates koordinate komponente (organizacija, projekat, verzija)
     * @return spisak zavisnosti komponente
     * @throws ComponentNotFoundException ako komponenta ne postoji u repozitorijumu
     */
    fun getDependencies(coordinates: Coordinates): List<Dependency>
}