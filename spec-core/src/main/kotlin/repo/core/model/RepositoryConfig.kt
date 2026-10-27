package repo.core.model

data class RepositoryConfig(
    val AllowedExtensions : Set<String>,
    val scopes : List<ScopeDefinition>
)