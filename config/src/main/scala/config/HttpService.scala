package config

import cats._
import cats.implicits._
import cats.effect._
import cats.mtl._
import cats.mtl.implicits._
import org.http4s._
import org.http4s.dsl._
import scala.concurrent.ExecutionContext

object HttpRoutesBuilder {
  def build[E, F[_]: Effect: ApplicativeHandle[?[_], E]](configService: ConfigRepo[F])(
      implicit ec: ExecutionContext): HttpRoutes[F] = {
    val dslF = Http4sDsl[F]
    import dslF._
    HttpRoutes.of[F] {
      case GET -> Root / "config" / ConfigId(id) =>
        for {
          value <- configService.read(id)
          res <- value.fold(NotFound())(Ok(_))
        } yield res

      case POST -> Root / "config" / value =>
        for {
          id <- configService.create(value)
          res <- Ok(id.toString)
        } yield res

      case PUT -> Root / "config" / ConfigId(id) / value =>
        for {
          value <- configService.update(id, value).handle[E](x => NotFound())
          res <- Ok()
        } yield res

      case DELETE -> Root / "config" / ConfigId(id) =>
        for {
          _ <- configService.delete(id)
          res <- Ok()
        } yield res
    }
  }
}
