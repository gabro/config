package config

import org.http4s._
import org.http4s.dsl.Http4sDsl
import cats._
import cats.data._
import cats.implicits._

object RoutesHttpErrorHandler {
  def apply[F[_], E <: Throwable](routes: HttpRoutes[F])(handler: E => F[Response[F]])(
      implicit ev: ApplicativeError[F, E]): HttpRoutes[F] =
    Kleisli { req: Request[F] =>
      OptionT {
        routes.run(req).value.handleErrorWith { e =>
          handler(e).map(Option(_))
        }
      }
    }
}

class RepoHttpErrorHandler[F[_]: ApplicativeError[?[_], RepoError]]
    extends HttpErrorHandler[F, RepoError]
    with Http4sDsl[F] {

  private val handler: RepoError => F[Response[F]] = {
    case RepoError.NotFound => NotFound()
  }

  override def handle(routes: HttpRoutes[F]): HttpRoutes[F] =
    RoutesHttpErrorHandler(routes)(handler)
}
