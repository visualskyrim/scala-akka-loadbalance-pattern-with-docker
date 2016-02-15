import com.typesafe.sbt.SbtNativePackager._
import NativePackagerKeys._

name := "hello-akka"

version := "1.0"

scalaVersion := "2.11.6"

packageArchetype.java_server

resolvers += Resolver.url("scoverage-bintray", url("https://dl.bintray.com/sksamuel/sbt-plugins/"))(Resolver.ivyStylePatterns)

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor" % "2.3.11",
  "com.typesafe.akka" %% "akka-remote" % "2.3.11",
  "com.typesafe.akka" %% "akka-testkit" % "2.3.11",
  "org.scalatest" % "scalatest_2.11" % "2.2.4" % "test",
  "com.novocode" % "junit-interface" % "0.11" % "test"
)
