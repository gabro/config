import io.estatico.newtype.macros.newtype
import io.chrisdavenport.fuuid.FUUID
import io.chrisdavenport.fuuid.http4s.FUUIDVar
import cats.data.EitherT
import cats.effect.IO

package object config {
  type Stack[F] = EitherT[IO, String, F]

  @newtype case class ConfigId(value: FUUID)
  object ConfigId {
    def unapply(str: String): Option[ConfigId] = FUUIDVar.unapply(str).map(ConfigId(_))
  }
}
