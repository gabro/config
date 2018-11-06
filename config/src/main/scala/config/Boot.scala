package config

import cats._
import cats.data.EitherT
import cats.implicits._
import cats.mtl._
// import cats.mtl.implicits._
import cats.effect._
import cats.effect.implicits._
import org.http4s.server.blaze._
import org.http4s.server.Router
import org.http4s.implicits._
import org.http4s.syntax._
import io.chrisdavenport.log4cats.slf4j.Slf4jLogger
import io.chrisdavenport.log4cats.Logger
import scala.concurrent.ExecutionContext.Implicits.global

object Boot extends IOApp {
  type Stack[A] = IO[A]

  implicit val logger: Logger[Stack] = Slf4jLogger.unsafeCreate[Stack]

  implicit val ioApplicativeHandle: ApplicativeHandle[IO, Throwable] =
    new DefaultApplicativeHandle[IO, Throwable] {
      override val functor: Functor[IO] = Functor[IO]
      override val applicative: Applicative[IO] = Applicative[IO]
      override def raise[A](e: Throwable): IO[A] = IO.raiseError(e)
      override def handleWith[A](fa: IO[A])(f: Throwable => IO[A]): IO[A] = fa.handleErrorWith(f)
    }

  val configRepo = new ConfigRepoImpl[Stack]()

  def run(args: List[String]): IO[ExitCode] = {
    BlazeServerBuilder[Stack]
      .bindHttp(8080, "localhost")
      .withHttpApp(HttpRoutesBuilder.build(configRepo).orNotFound)
      .serve
      .compile
      .drain
      .as(ExitCode.Success)
  }

}
