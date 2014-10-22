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
      //"com.typesafe.akka" %% "akka-actor" % "2.3.3",
      //"com.typesafe.akka" %% "akka-kernel" % "2.3.3",
      //"com.blinkbox.books" %% "common-config" % "1.4.1",
      "com.blinkbox.books.hermes" %% "rabbitmq-ha" % "7.1.0",
      //"com.blinkbox.books.hermes" %% "message-schemas" % "0.7.0",
      //"com.sksamuel.elastic4s" %% "elastic4s" % "1.3.2",
      "eu.inn" %% "binders-cassandra" % "0.2.5",
      "org.reactivemongo" %% "reactivemongo" % "0.10.5.0.akka23",
      jdbc,
      anorm,
      ws
  )
)

val root = (project in file(".")).
  enablePlugins(PlayScala).
  settings(buildSettings: _*).
  settings(dependencySettings: _*)


