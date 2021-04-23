import Dependencies.Libraries

name := "mars-rover"
version := "0.1"
scalaVersion := "2.13.5"

libraryDependencies ++= Seq(
  Libraries.cats,
  Libraries.catsEffects,
  Libraries.log4catsSlf4j,
  Libraries.log4catsCore,
  Libraries.logback,

  Libraries.munitCatsEffect,
  Libraries.scalaTest,
  Libraries.log4catsNoOp
)