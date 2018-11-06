package config

sealed trait RepoError extends Throwable
object RepoError {
  case object NotFound extends RepoError

  val notFound: Throwable = NotFound
}
