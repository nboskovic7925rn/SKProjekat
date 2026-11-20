package repo.core.exception

/**
 * Osnovni izuzetak za sve greske koje mogu da nastanu prilikom rada sa repozitorijumom
 * Svi specificniji izuzeci nasledjuju ovu klasu kako bi klijentski kod mogao jedinstveno da uhvati konkretnu gresku nastalu prilikom rada sa repozitorijumom
 *
 * @param message Opisuje gresku koja je nastala
 */

open class RepositoryException(message: String) : Exception(message)

/**
 * Nastaje prilikom pokusaja postaljanje komponente koja vec postoji u repozitorijumu (njene jedinstvene koordinate vec postoje u repozitorijumu)
 *
 * @param message Opis konflikta sa postojecom komponentom
 */
class ComponentAlreadyExistsException(message: String) : RepositoryException(message)

/**
 * Nastaje prilikom pokusaja postavljanje komponente pre nego sto je njega zavisna komponenta postavljena u repozitorijum
 *
 * @param message Opis zavisnosti koja nedostaje
 */
class MissingDependencyException(message: String) : RepositoryException(message)

/**
 * Nastaje ukoliko su zavisnosti komponente neispravno definisane (npr. sadrzi duplikate ili
 * sadrzi nevalidne opsege zavisnosti (opsege koji nisu prepoznati na nivout repozitorijuma)
 *
 * @property message Opis nepravilnosti
 */
class InvalidDependencyException(message: String) : RepositoryException(message)

/**
 * Nastaje prilikom pokusaja postavljanja komponente sa nedozvoljenom ekstenzijom
 * (ekstenzija nije dozvoljena po konfiguraciji na nivou repozitorijuma)
 *
 * @param message Opis nedozvoljene ekstenzije fajla
 */
class ExtensionNotAllowedException(message: String) : RepositoryException(message)

/**
 * Nastaje kada komponenta nije pronadjena na nivou repozitorijuma (npr. prilikom preuzimanja ili pregleda zavisnosti)
 *
 * @param message Opis lokacije na kojoj nije pronadjena komponenta
 */
class ComponentNotFoundException(message: String) : RepositoryException(message)

/**
 * Nastaje prilikom pokusaja inicijalizacije repozitorijuma na mestu na kom je vec inicijalizovan
 *
 * @param message Opis lokacije na kojoj repozitorijum vec postoji
 */
class RepositoryAlreadyExistsException(message: String) : RepositoryException(message)
