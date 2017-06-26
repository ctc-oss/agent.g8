
lazy val `$name;format="norm"$-agent` =
  project.in(file("."))
  .aggregate(
    `$name;format="norm"$-agent-api`,
    `$name;format="norm"$-agent-impl`,
    `$name;format="norm"$-agent-dsl`
  )
  .settings(commonSettings: _*)
  .enablePlugins(GitVersioning, NoPublish)

lazy val `$name;format="norm"$-agent-api` =
  project.in(file("$name;format="norm"$-agent-api"))
  .settings(commonSettings: _*)
  .settings(
    name := "$name;format="norm"$-agent-api",
    libraryDependencies ++= commonLibraries
  )
  .enablePlugins(JavaServerAppPackaging, DockerPlugin, GitVersioning, ArtifactoryPublish)
  .settings(dockerSettings: _*)

lazy val `$name;format="norm"$-agent-impl` =
  project.in(file("$name;format="norm"$-agent-impl"))
  .dependsOn(`$name;format="norm"$-agent-api`)
  .settings(commonSettings: _*)
  .settings(
    name := "$name;format="norm"$-agent",
    libraryDependencies ++= commonLibraries
  )
  .enablePlugins(JavaServerAppPackaging, DockerPlugin, GitVersioning, NoPublish)
  .settings(dockerSettings: _*)

lazy val `$name;format="norm"$-agent-dsl` =
  project.in(file("$name;format="norm"$-agent-dsl"))
  .dependsOn(`$name;format="norm"$-agent-api`)
  .settings(commonSettings: _*)
  .settings(
    name := "$name;format="norm"$-agent-dsl",
    publishArtifact in packageDoc := false
  )
  .settings(
    libraryDependencies
      ++= commonLibraries
      ++ antlrLibraries
  )
  .settings(antlrSettings: _*)
  .enablePlugins(JavaServerAppPackaging, DockerPlugin, GitVersioning, NoPublish)
  .settings(dockerSettings: _*)

/**
 *
 * Commons
 *
 */

lazy val commonSettings = Seq(
  organization := "$organization$",
  version := "$version$",
  scalaVersion := "2.12.2",
  parallelExecution := false,

  scalacOptions ++= Seq(
    "-encoding", "UTF-8",

    "-feature",
    "-unchecked",
    "-deprecation",

    "-language:postfixOps",
    "-language:implicitConversions",

    "-Ywarn-unused-import",
    "-Xfatal-warnings",
    "-Xlint:_"
  ),
  concurrentRestrictions in Global += Tags.limit(Tags.Test, 1)
)

lazy val akkaVersion = "2.5.2"
lazy val akkaHttpVersion = "10.0.7"
lazy val scalatestVersion = "3.0.3"
lazy val rxKafkaVersion = "0.16"

lazy val commonLibraries = {
  Seq(
    "com.ctc.reactivechat" %% "api" % "0.1",
    "com.ctc.reactivechat" %% "core" % "0.1",

    "com.iheart" %% "ficus" % "1.4.0",
    "io.spray" %% "spray-json" % "1.3.3",
    "org.julienrf" %% "play-json-derived-codecs" % "4.0.0-RC1",

    "com.typesafe.akka" %% "akka-actor" % akkaVersion,
    "com.typesafe.akka" %% "akka-stream" % akkaVersion,
    "com.typesafe.akka" %% "akka-stream-kafka" % rxKafkaVersion,
    "com.typesafe.akka" %% "akka-http" % akkaHttpVersion,
    "com.typesafe.akka" %% "akka-http-core" % akkaHttpVersion,
    "com.typesafe.akka" %% "akka-http-spray-json" % akkaHttpVersion,

    "com.typesafe.akka" %% "akka-slf4j" % akkaVersion,
    "ch.qos.logback" % "logback-classic" % "1.2.3",
    "com.typesafe.scala-logging" %% "scala-logging" % "3.5.0",

    "org.scalactic" %% "scalactic" % scalatestVersion % Test,
    "org.scalatest" %% "scalatest" % scalatestVersion % Test,
    "com.typesafe.akka" %% "akka-testkit" % akkaVersion % Test,
    "net.manub" %% "scalatest-embedded-kafka" % "0.13.1" % Test
  )
}

lazy val antlrLibraries = {
  Seq(
    "org.antlr" % "antlr4-runtime" % "4.6"
  )
}

lazy val dockerSettings = Seq(
  packageName := "$name;format="norm"$-agent",
  dockerExposedPorts := Seq(9000),
  dockerRepository := Some("docker.ctc.com/big"),
  dockerBaseImage := "davidcaste/debian-oracle-java:jdk8",
  version in Docker := version.value.replaceFirst("""-SNAPSHOT""", ""),
  dockerUpdateLatest := true
)

lazy val antlrSettings = antlr4Settings ++ Seq(
  antlr4PackageName in Antlr4 := Some("big"),
  javaSource in Antlr4 <<= (sourceManaged in Compile).apply(_ / "java")
)
