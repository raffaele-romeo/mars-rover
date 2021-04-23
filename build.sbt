import Dependencies.Libraries

name := "mars-rover"
version := "0.1"
scalaVersion := "2.13.5"

libraryDependencies ++= Seq(
  Libraries.cats,
  Libraries.catsEffects,
  Libraries.scalaTest
)