package repo.core.model

/**
 * Konfiguracija koja vazi na nivou celog repozitorijuma
 *
 * @property allowedExtensions Definise koje ekstenzije fajlova su podrzane na nivou repozitorijuma (npr. jar, zip...). Nenavedene ekstenzije ce biti odbijene prilikom inicijalizacije
 * @property scopes Definise koji su opsezi prepoznati na nivou repozitorijuma klasom [ScopeDefinition]
 */

data class RepositoryConfig(
    val allowedExtensions : Set<String>,
    val scopes : List<ScopeDefinition>
)