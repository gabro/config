package config

import io.circe.generic.JsonCodec

@JsonCodec case class Config(id: Id[Config], value: String, author: User)
@JsonCodec case class NewConfig(value: String, author: User)
@JsonCodec case class UpdateConfig(value: String)
