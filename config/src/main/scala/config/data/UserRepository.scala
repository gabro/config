package config

import cats._
import cats.implicits._
import cats.effect._
import io.chrisdavenport.fuuid.FUUID
import io.chrisdavenport.log4cats.Logger

trait UserRepository[F[_]] {
  def read(id: Id[User]): F[Option[User]]
  def update(id: Id[User], value: UpdateUser): F[Unit]
  def create(value: NewUser): F[Id[User]]
  def delete(id: Id[User]): F[Unit]
}

class InMemoryUserRepository[F[_]](implicit F: Sync[F], logger: Logger[F])
    extends UserRepository[F] {

  private val storage = new java.util.concurrent.ConcurrentHashMap[Id[User], User]
  private val id = Id[User](FUUID.fromString("a78be6e5-980b-486e-adaa-1a3d82eef8af").right.get)
  storage.put(id, User(id, "testuser"))

  override def read(id: Id[User]): F[Option[User]] =
    for {
      _ <- logger.info(s"Reading id: $id")
    } yield Option(storage.get(id))

  override def update(id: Id[User], user: UpdateUser): F[Unit] = {
    for {
      _ <- logger.info(s"Updating id: $id")
      result = storage.replace(id, User(id, user.name))
      res <- if (result == null) {
        F.raiseError(RepoError.NotFound)
      } else ().pure[F]
    } yield res
  }

  override def create(user: NewUser): F[Id[User]] = {
    for {
      uuid <- FUUID.randomFUUID[F]
      id = Id[User](uuid)
      _ <- logger.info(s"Creating with id: $id")
      _ = storage.put(id, User(id, user.name))
    } yield id
  }

  override def delete(id: Id[User]): F[Unit] = {
    for {
      _ <- logger.info(s"Deleting with id: $id")
    } yield {
      storage.remove(id)
      ()
    }
  }

}
