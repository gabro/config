val V = new {
  val scala = "2.12.7"
  val http4s = "0.20.0-M2"
  val cats = "1.2"
  val catsMtl = "0.4.0"
  val catsEffect = "1.0.0"
  val circe = "0.10.0"
  val fuuid = "0.2.0-M2"
}

lazy val config = project
  .settings(
    scalaVersion := V.scala,
    scalacOptions += "-Ypartial-unification",
    libraryDependencies ++= Seq(
      "org.http4s" %% "http4s-dsl" % V.http4s,
      "org.http4s" %% "http4s-blaze-server" % V.http4s,
      "org.http4s" %% "http4s-circe" % V.http4s,
      "org.typelevel" %% "cats-core" % V.cats,
      "org.typelevel" %% "cats-mtl-core" % V.catsMtl,
      "org.typelevel" %% "cats-effect" % V.catsEffect,
      "io.circe" %% "circe-core" % V.circe,
      "io.estatico" %% "newtype" % "0.4.2",
      "io.chrisdavenport" %% "fuuid" % V.fuuid,
      "io.chrisdavenport" %% "fuuid-http4s" % V.fuuid,
      "io.chrisdavenport" %% "log4cats-slf4j" % "0.2.0",
      "ch.qos.logback" % "logback-classic" % "1.2.3"
    ),
    addCompilerPlugin("org.spire-math" %% "kind-projector" % "0.9.6"),
    addCompilerPlugin("org.scalamacros" % "paradise" % "2.1.1" cross CrossVersion.full)
  )
