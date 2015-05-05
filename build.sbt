name := """PlayReactiveMongoPolymer"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.11.6"

libraryDependencies ++= Seq(
  "org.reactivemongo" %% "play2-reactivemongo" % "0.10.5.0.akka23",
  "org.webjars" %% "webjars-play" % "2.3.0-2",
  "org.webjars.bower" % "polymer-polymer" % "0.8.0-rc.7"
)


//fork in run := true
