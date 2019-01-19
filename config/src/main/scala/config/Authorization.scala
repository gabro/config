package config

import cats._
import cats.data._
import cats.implicits._
import cats.effect._
import org.http4s._
import org.http4s.parser._
import org.http4s.server._
import org.http4s.headers.{Authorization => AuthorizationH}
import org.http4s.dsl.Http4sDsl
import org.http4s.util.CaseInsensitiveString
import io.chrisdavenport.log4cats.Logger

class Authorization[F[_]: Functor](authService: AuthRepository[F], userService: UserRepository[F])(
    implicit F: Effect[F],
    logger: Logger[F])
    extends Http4sDsl[F] {

  private val authUser: Kleisli[OptionT[F, ?], Request[F], User] =
    Kleisli({ request =>
      for {
        header <- OptionT.fromOption[F](request.headers.get(CaseInsensitiveString("Authorization")))
        token <- OptionT.fromOption[F](
          HttpHeaderParser.AUTHORIZATION(header.value).toOption.map(_.credentials).collect {
            case Credentials.AuthParams(_, NonEmptyList(("token", token), _)) => token
          })
        userId <- OptionT(authService.read(token))
        user <- OptionT(userService.read(userId))
      } yield user
    })

  val middleware: AuthMiddleware[F, User] =
    AuthMiddleware(authUser)

}
