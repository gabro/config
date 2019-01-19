import io.estatico.newtype.macros.newtype
import io.chrisdavenport.fuuid.FUUID
import io.chrisdavenport.fuuid.http4s.FUUIDVar
import io.circe.Encoder
import io.circe.Decoder
import io.chrisdavenport.fuuid.circe._

package object config {
  @newtype case class Id[A](value: FUUID)
  object Id {
    def unapply[A](str: String): Option[Id[A]] = FUUIDVar.unapply(str).map(Id[A](_))
    implicit def jsonEncoder[A]: Encoder[Id[A]] = deriving
    implicit def jsonDecoder[A]: Decoder[Id[A]] = deriving
  }
  object ConfigId {
    def unapply(str: String): Option[Id[Config]] = Id.unapply(str)
  }

}
