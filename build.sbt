name := "scalavision"

version := "1.0"

scalaVersion := "2.11.8"

/*libraryDependencies ++= List(
  "com.typesafe.akka" %% "akka-actor" % "2.4.6",
  "com.typesafe.akka" %% "akka-http-core" % "2.4.6",
  "com.typesafe.akka" %% "akka-http-experimental" % "2.4.6"
)*/

javaOptions in run += "-Djava.library.path=/home/n.werneck/share/OpenCV/java"