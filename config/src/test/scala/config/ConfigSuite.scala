package config
package test

import cats.data.NonEmptyList
import cats.effect._
import io.chrisdavenport.log4cats.slf4j.Slf4jLogger
import io.chrisdavenport.log4cats.Logger
import org.http4s._
import org.http4s.util.CaseInsensitiveString
import org.http4s.server.Router
import org.http4s.implicits._
import com.olegpy.meow.hierarchy._
import minitest._

object ConfigSuite extends SimpleTestSuite {
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

  test("should create config") {
    val response = service
      .run(
        Request(
          method = Method.POST,
          uri = Uri.uri("/config/testvalue"),
          headers = Headers(headers.Authorization(Credentials
            .AuthParams(CaseInsensitiveString("Token"), NonEmptyList.one("token" -> "testtoken"))))
        ))
      .unsafeRunSync
    assertEquals(response.status, Status.Created)
  }

}
