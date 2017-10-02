lazy val commonSettings = Seq(

version := "0.0.1",
scalaVersion := "2.11.8",
EclipseKeys.withSource := true,
parallelExecution in test := false,
test in assembly := {},
assemblyMergeStrategy in assembly := {
 case "META-INF/services/org.apache.lucene.codecs.Codec" =>  MergeStrategy.first
 case "META-INF/services/org.apache.lucene.codecs.PostingsFormat" => MergeStrategy.first
 case "META-INF/services/org.apache.lucene.codecs.DocValuesFormat" => MergeStrategy.first
 case PathList("META-INF", xs @ _*) => MergeStrategy.discard
 case x => MergeStrategy.first
}
) ++ packAutoSettings

lazy val project = Project(
id = "geosearch",
base = file(".")).settings(commonSettings).settings(
name := "geosearch",

resolvers ++= Seq(
    "geo" at "http://download.osgeo.org/webdav/geotools"
),

libraryDependencies ++= Seq(
  "org.json4s" %% "json4s-jackson" % "3.2.11",
"com.github.scala-incubator.io" %% "scala-io-file" % "0.4.3",
 "org.apache.lucene" % "lucene-codecs" % "4.10.4" % "test",
"org.apache.lucene" % "lucene-analyzers-common" % "4.10.4",
"org.apache.lucene" % "lucene-core" % "4.10.4",
"org.apache.lucene" % "lucene-spatial" % "4.10.4",
 "org.apache.lucene" % "lucene-queries" % "4.10.4",
"com.vividsolutions" % "jts" % "1.13")

)
