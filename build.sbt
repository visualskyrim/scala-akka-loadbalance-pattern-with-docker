
name := "scala-akka-loadbalance-pattern-with-docker"

version := "1.0"

scalaVersion := "2.11.6"


resolvers += Resolver.url("scoverage-bintray", url("https://dl.bintray.com/sksamuel/sbt-plugins/"))(Resolver.ivyStylePatterns)

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor" % "2.3.11",
  "com.typesafe.akka" %% "akka-remote" % "2.3.11",
  "com.typesafe.akka" %% "akka-testkit" % "2.3.11",
  "org.scalatest" % "scalatest_2.11" % "2.2.4" % "test",
  "com.novocode" % "junit-interface" % "0.11" % "test"
)
