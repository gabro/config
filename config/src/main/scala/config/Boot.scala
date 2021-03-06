package config

import cats._
import cats.data.EitherT
import cats.implicits._
import cats.effect._
import cats.effect.implicits._
import org.http4s.server.blaze._
import org.http4s.server.Router
import org.http4s.implicits._
import org.http4s.syntax._
import io.chrisdavenport.log4cats.slf4j.Slf4jLogger
import io.chrisdavenport.log4cats.Logger
import com.olegpy.meow.hierarchy._

object Boot extends IOApp {
  type Stack[A] = IO[A]

  implicit val logger: Logger[Stack] = Slf4jLogger.unsafeCreate[Stack]
  implicit val repoHttpErrorHandler: HttpErrorHandler[IO, RepoError] = new RepoHttpErrorHandler[IO]

  val configRepository = new InMemoryConfigRepository[Stack]()
  val authRepository = new InMemoryAuthRepository[Stack]()
  val userRepository = new InMemoryUserRepository[Stack]()

  val auth = new Authorization(authRepository, userRepository)

  val service = Router(
    "/config" -> new ConfigRoutes(configRepository, auth.middleware).routes
  ).orNotFound

  def run(args: List[String]): IO[ExitCode] = {
    BlazeServerBuilder[Stack]
      .bindHttp(8080, "localhost")
      .withHttpApp(service)
      .serve
      .compile
      .drain
      .as(ExitCode.Success)
  }

}
