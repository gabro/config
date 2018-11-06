package config

import cats._
import cats.implicits._
import cats.mtl._
import cats.effect._
import cats.mtl.syntax.all._
import io.chrisdavenport.fuuid.FUUID
import io.chrisdavenport.log4cats.Logger

trait ConfigRepo[F[_]] {
  def read(id: ConfigId): F[Option[String]]
  def update(id: ConfigId, value: String): F[Unit]
  def create(value: String): F[ConfigId]
  def delete(id: ConfigId): F[Unit]
}

class ConfigRepoImpl[F[_]: FunctorRaise[?[_], Throwable]: Sync: Logger] extends ConfigRepo[F] {

  private val storage = new java.util.concurrent.ConcurrentHashMap[ConfigId, String]
  private val logger = Logger[F]

  override def read(id: ConfigId): F[Option[String]] =
    for {
      _ <- logger.info(s"Reading id: $id")
    } yield Option(storage.get(id))

  override def update(id: ConfigId, value: String): F[Unit] = {
    for {
      _ <- logger.info(s"Updating id: $id")
    } yield {
      val result = storage.replace(id, value)
      if (result == null) RepoError.notFound.raise
      else ()
    }
  }

  override def create(value: String): F[ConfigId] = {
    for {
      uuid <- FUUID.randomFUUID[F]
      id = ConfigId(uuid)
      _ <- logger.info(s"Creating with id: $id")
      _ = storage.put(id, value)
    } yield id
  }

  override def delete(id: ConfigId): F[Unit] = {
    for {
      _ <- logger.info(s"Deleting with id: $id")
    } yield {
      storage.remove(id)
      ()
    }
  }

}
