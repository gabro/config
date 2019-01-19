package config

import cats._
import cats.implicits._
import cats.effect._
import io.chrisdavenport.fuuid.FUUID
import io.chrisdavenport.log4cats.Logger

trait AuthRepository[F[_]] {
  def read(token: String): F[Option[Id[User]]]
}

class InMemoryAuthRepository[F[_]](implicit F: Sync[F], logger: Logger[F])
    extends AuthRepository[F] {

  protected val storage = new java.util.concurrent.ConcurrentHashMap[String, Id[User]]
  private val id = Id[User](FUUID.fromString("a78be6e5-980b-486e-adaa-1a3d82eef8af").right.get)
  storage.put("testtoken", id)

  override def read(token: String): F[Option[Id[User]]] =
    for {
      _ <- logger.info(s"Reading token: $token")
    } yield Option(storage.get(token))

}
