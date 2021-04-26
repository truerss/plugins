
val basePluginSetting = Seq(
  scalacOptions ++= Seq(
    "-Xlog-free-terms",
    "-deprecation",
    "-feature"
  )
)

val basePlugin = Project(
  id = "base",
  base = file("base")
).settings(basePluginSetting : _*).settings(
  organization := "io.github.truerss",
  name := "base",
  version := "0.0.6",
  scalaVersion := "2.12.4",
  crossScalaVersions := Seq("2.12.4"),
  licenses += ("MIT", url("http://opensource.org/licenses/MIT")),
  publishMavenStyle := true,
  Test / publishArtifact := false,
  libraryDependencies ++= Seq("com.typesafe" % "config" % "1.3.0")
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
      "com.typesafe" % "config" % "1.3.0",
      "org.jsoup" % "jsoup" % "1.8.3",
      "org.scalaj" %% "scalaj-http" % "2.3.0"
    )
  )

lazy val stackoverflowPlugin =
  plugin("truerss-stackoverflow-plugin")
  .settings(
    libraryDependencies ++= Seq(
      "com.typesafe" % "config" % "1.3.0",
      "org.jsoup" % "jsoup" % "1.8.3" % "provided"
    )
  )

lazy val tumblrPlugin = plugin("truerss-tumblr-plugin")
  .settings(
    libraryDependencies ++= Seq(
      "com.typesafe" % "config" % "1.3.0" % "provided",
      "com.tumblr" % "jumblr" % "0.0.11"
    )
  )

lazy val youtubePlugin = plugin("truerss-youtube-plugin")
  .settings(
    libraryDependencies ++= Seq(
      "com.typesafe" % "config" % "1.3.0"
    )
  )

lazy val publishPlugin = plugin("truerss-publish-plugin")
  .settings(
    libraryDependencies ++= Seq()
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
