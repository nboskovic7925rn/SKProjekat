package repo.core.exception

open class RepositoryException(message: String) : Exception(message)

class ComponentAlreadyExistsException(message: String) : RepositoryException(message)
class MissingDependencyException(message: String) : RepositoryException(message)
class InvalidDependencyException(message: String) : RepositoryException(message)
class ExtensionNotAllowedException(message: String) : RepositoryException(message)
