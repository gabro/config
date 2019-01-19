package config

import io.circe.generic.JsonCodec

@JsonCodec case class User(id: Id[User], name: String)
@JsonCodec case class NewUser(name: String)
@JsonCodec case class UpdateUser(name: String)
