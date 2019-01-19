package config

import cats._
import cats.implicits._
import cats.effect._
import org.http4s._
import org.http4s.server._
import org.http4s.dsl.Http4sDsl
import org.http4s.circe._
import scala.concurrent.ExecutionContext
import io.chrisdavenport.log4cats.Logger
import io.circe.syntax._

class ConfigRoutes[F[_]: Effect](configService: ConfigRepository[F], auth: AuthMiddleware[F, User])(
    implicit H: HttpErrorHandler[F, RepoError])
    extends Http4sDsl[F] {

  private val httpRoutes: HttpRoutes[F] = auth(AuthedService {
    case GET -> Root / ConfigId(id) as _ =>
      for {
        value <- configService.read(id)
        res <- value.fold(NotFound())(v => Ok(v.asJson))
      } yield res

    case POST -> Root / value as user =>
      for {
        id <- configService.create(NewConfig(value, user))
        res <- Created(id.toString)
      } yield res

    case PUT -> Root / ConfigId(id) / value as _ =>
      for {
        value <- configService.update(id, UpdateConfig(value))
        res <- Ok()
      } yield res

    case DELETE -> Root / ConfigId(id) as _ =>
      for {
        _ <- configService.delete(id)
        res <- Ok()
      } yield res
  })

  val routes: HttpRoutes[F] = H.handle(httpRoutes)
}
