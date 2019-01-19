package config

import cats._
import cats.implicits._
import cats.effect._
import io.chrisdavenport.fuuid.FUUID
import io.chrisdavenport.log4cats.Logger

trait ConfigRepository[F[_]] {
  def read(id: Id[Config]): F[Option[Config]]
  def update(id: Id[Config], value: UpdateConfig): F[Unit]
  def create(value: NewConfig): F[Id[Config]]
  def delete(id: Id[Config]): F[Unit]
}

class InMemoryConfigRepository[F[_]](implicit F: Sync[F], logger: Logger[F])
    extends ConfigRepository[F] {

  private val storage = new java.util.concurrent.ConcurrentHashMap[Id[Config], Config]

  override def read(id: Id[Config]): F[Option[Config]] =
    for {
      _ <- logger.info(s"Reading id: $id")
    } yield Option(storage.get(id))

  override def update(id: Id[Config], config: UpdateConfig): F[Unit] = {
    for {
      _ <- logger.info(s"Updating id: $id")
      result = storage.computeIfPresent(id, (_, oldConfig) => oldConfig.copy(value = config.value))
      res <- if (result == null) {
        F.raiseError(RepoError.NotFound)
      } else ().pure[F]
    } yield res
  }

  override def create(config: NewConfig): F[Id[Config]] = {
    for {
      uuid <- FUUID.randomFUUID[F]
      id = Id[Config](uuid)
      _ <- logger.info(s"Creating with id: $id")
      _ = storage.put(id, Config(id, config.value, config.author))
    } yield id
  }

  override def delete(id: Id[Config]): F[Unit] = {
    for {
      _ <- logger.info(s"Deleting with id: $id")
    } yield {
      storage.remove(id)
      ()
    }
  }

}
