import play.Play.autoImport._

resolvers += "Websudos releases" at "http://maven.websudos.co.uk/ext-release-local"

val buildSettings = Seq(
  name := "badger",
  organization := "com.blinkbox.books.games.badge",
  version := scala.util.Try(scala.io.Source.fromFile("VERSION").mkString.trim).getOrElse("0.0.0"),
  scalaVersion := "2.11.2",
  scalacOptions := Seq("-unchecked", "-deprecation", "-feature", "-encoding", "utf8", "-target:jvm-1.7"),
  unmanagedResourceDirectories in Compile += file("./var")
)

val dependencySettings = Seq(
  libraryDependencies ++= Seq(
      "com.blinkbox.books.hermes" %% "rabbitmq-ha" % "7.1.0",
      "eu.inn" %% "binders-cassandra" % "0.2.5",
      "org.postgresql" % "postgresql" % "9.3-1102-jdbc41",
      jdbc,
      anorm,
      ws
  )
)

val root = (project in file(".")).
  enablePlugins(PlayScala).
  settings(buildSettings: _*).
  settings(dependencySettings: _*)


