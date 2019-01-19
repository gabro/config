package config

sealed trait RepoError extends Exception
object RepoError {
  case object NotFound extends RepoError
}
