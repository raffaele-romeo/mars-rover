import sbt._

object Dependencies {

  object Versions {
    val cats = "2.6.0"
    val catsEffect = "3.1.0"
    val log4cats = "2.0.1"
    val logback = "1.2.3"
    val slf4j = "1.7.30"

    val munitCatsEffect = "1.0.2"
    val scalaTest = "3.2.7"
  }

  object Libraries {
    val cats = "org.typelevel" %% "cats-core" % Versions.cats
    val catsEffects = "org.typelevel" %% "cats-effect" % Versions.catsEffect

    val log4catsSlf4j = "org.typelevel" %% "log4cats-slf4j" % Versions.log4cats
    val log4catsCore = "org.typelevel" %% "log4cats-core" % Versions.log4cats
    val log4catsNoOp = "org.typelevel" %% "log4cats-noop" % Versions.log4cats

    // Runtime
    val logback = "ch.qos.logback" % "logback-classic" % Versions.logback

    val scalaTest = "org.scalatest" %% "scalatest" % Versions.scalaTest % Test
    val munitCatsEffect = "org.typelevel" %% "munit-cats-effect-3" % Versions.munitCatsEffect % Test
  }
}
