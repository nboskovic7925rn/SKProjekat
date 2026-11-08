package repo.core.model

data class RepositoryConfig(
    val allowedExtensions : Set<String>,
    val scopes : List<ScopeDefinition>
)