name := "scala-futures-promises"

version := "1.0"

organization := "tdauth"

scalaVersion := "2.12.7"

scalacOptions := Seq("-unchecked", "-deprecation", "-feature")

// disable JIT compilation at runtime which could affect the performance time
javaOptions += "-Djava.compiler=NONE"

// set the main class for 'sbt run'
mainClass in (Compile, run) := Some("tdauth.futuresandpromises.benchmarks.Benchmarks")
// set the main class for packaging the main jar
mainClass in (Compile, packageBin) := Some("tdauth.futuresandpromises.benchmarks.Benchmarks")

resolvers += "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots"

libraryDependencies += "com.twitter" %% "util-collection" % "18.9.1"

libraryDependencies += "org.scala-stm" %% "scala-stm" % "0.8"

libraryDependencies += "org.scala-lang.modules" %% "scala-async" % "0.9.6"

libraryDependencies += "org.scalactic" %% "scalactic" % "3.0.5"

libraryDependencies += "org.scalatest" %% "scalatest" % "3.0.5" % "test"