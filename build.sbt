
ThisBuild / version := "1.0.0"
ThisBuild / organization := "io.github.truerss"
sonatypeRepository := "https://s01.oss.sonatype.org/service/local"
sonatypeCredentialHost := "s01.oss.sonatype.org"
ThisBuild / publishTo := {
  val nexus = "https://s01.oss.sonatype.org/"
  if (isSnapshot.value)
    Some("snapshots" at nexus + "content/repositories/snapshots/")
  else
    Some("releases" at nexus + "service/local/staging/deploy/maven2")
}

ThisBuild / homepage := Some(url("https://github.com/truerss/truerss"))
ThisBuild / scmInfo := Some(ScmInfo(url("https://github.com/truerss/plugins"), "git@github.com:truerss/plugins.git"))
ThisBuild / developers := List(Developer("mike", "mike", "mike.fch1@gmail.com", url("https://github.com/fntz")))
ThisBuild / licenses += ("MIT", url("http://opensource.org/licenses/MIT"))

val basePluginSetting = Seq(
  scalacOptions ++= Seq(
    "-Xlog-free-terms",
    "-deprecation",
    "-feature"
  )
)

val config = "com.typesafe" % "config" % "1.4.1"
val jsoup = "org.jsoup" % "jsoup" % "1.8.3"

val basePlugin = Project(
  id = "base",
  base = file("base")
).settings(basePluginSetting : _*).settings(
  name := "base",
  scalaVersion := "2.12.4",
  crossScalaVersions := Seq("2.12.4"),
  Test / publishArtifact := false,
  libraryDependencies ++= Seq(config)
).disablePlugins(sbtassembly.AssemblyPlugin)

val pluginSettings = Seq(
  assembly / assemblyOption := (assembly / assemblyOption).value.copy(includeScala = false),
  organization := "io.github.truerss",
  version := "0.0.3",
  scalaVersion := "2.12.4",
  licenses += ("MIT", url("http://opensource.org/licenses/MIT"))
)

def plugin(pluginName: String): Project = Project(
  id = pluginName,
  base = file(pluginName)
).settings(
  pluginSettings ++ Seq(
    name := pluginName,
    assembly / assemblyJarName := s"$pluginName.jar"
  )
).dependsOn(basePlugin)

lazy val redditImageViewerPlugin =
  plugin("truerss-reddit-imageviewer-plugin")
  .settings(
    libraryDependencies ++= Seq(
      config,
      jsoup,
      "org.scalaj" %% "scalaj-http" % "2.4.2"
    )
  )

lazy val stackoverflowPlugin =
  plugin("truerss-stackoverflow-plugin")
  .settings(
    libraryDependencies ++= Seq(config, jsoup)
  )

lazy val tumblrPlugin = plugin("truerss-tumblr-plugin")
  .settings(
    libraryDependencies ++= Seq(
      config,
      "com.tumblr" % "jumblr" % "0.0.11"
    )
  )

lazy val youtubePlugin = plugin("truerss-youtube-plugin")
  .settings(
    libraryDependencies ++= Seq(config)
  )

lazy val publishPlugin = plugin("truerss-publish-plugin")
  .settings(
    libraryDependencies ++= Seq(
      config,
      "com.dorkbox" % "Notify" % "3.7"
    )
  )

lazy val mainPlugin = Project(
  id = "plugins",
  base = file(".")
).disablePlugins(sbtassembly.AssemblyPlugin).aggregate(
  basePlugin,
  redditImageViewerPlugin.dependsOn(basePlugin),
  stackoverflowPlugin.dependsOn(basePlugin),
  tumblrPlugin.dependsOn(basePlugin),
  youtubePlugin.dependsOn(basePlugin)
)
