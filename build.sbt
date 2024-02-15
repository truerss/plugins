ThisBuild / scalaVersion := "2.13.12"

ThisBuild / version := "1.1.0"
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
ThisBuild / scmInfo := Some(
  ScmInfo(url("https://github.com/truerss/plugins"), "git@github.com:truerss/plugins.git")
)
ThisBuild / developers := List(
  Developer("mike", "mike", "mike.fch1@gmail.com", url("https://github.com/fntz"))
)
ThisBuild / licenses += ("MIT", url("http://opensource.org/licenses/MIT"))

val basePluginSetting = Seq(scalacOptions ++= Seq("-Xlog-free-terms", "-deprecation", "-feature"))

val config = "com.typesafe" % "config" % "1.4.3"
val jsoup = "org.jsoup" % "jsoup" % "1.17.2"
val enumeratum = "com.beachape" %% "enumeratum" % "1.7.3"
val enumeratumPlayJson = "com.beachape" %% "enumeratum-play-json" % "1.8.0"
val utest = "com.lihaoyi" %% "utest" % "0.8.2"

ThisBuild / libraryDependencies += utest % Test
ThisBuild / testFrameworks += new TestFramework("utest.runner.Framework")

val basePlugin = Project(id = "base", base = file("base"))
  .settings(basePluginSetting: _*)
  .settings(
    name := "base",
    Test / publishArtifact := false,
    libraryDependencies ++= Seq(config, enumeratum, enumeratumPlayJson, utest % Test),
    testFrameworks += new TestFramework("utest.runner.Framework")
  )
  .disablePlugins(sbtassembly.AssemblyPlugin)

val pluginSettings = Seq(
  assembly / assemblyOption := (assembly / assemblyOption).value
    .withIncludeScala(false),
  assembly / assemblyMergeStrategy := {
    case x if x.endsWith("module-info.class") => MergeStrategy.discard
    case x => MergeStrategy.defaultMergeStrategy(x)
  },
  organization := "io.github.truerss",
  licenses += ("MIT", url("http://opensource.org/licenses/MIT"))
)

def plugin(pluginName: String): Project = Project(id = pluginName, base = file(pluginName))
  .settings(
    assembly / target := file("."),
    pluginSettings ++ Seq(
      name := pluginName,
      version := (ThisBuild / version).value,
      assembly / assemblyJarName := s"$pluginName.jar")
  )
  .dependsOn(basePlugin % "provided")

lazy val redditImageViewerPlugin =
  plugin("truerss-reddit-imageviewer-plugin")
    .settings(
      libraryDependencies ++= Seq(config, jsoup, "org.scalaj" %% "scalaj-http" % "2.4.2")
    )

lazy val stackoverflowPlugin =
  plugin("truerss-stackoverflow-plugin")
    .settings(
      libraryDependencies ++= Seq(config, jsoup))

lazy val tumblrPlugin = plugin("truerss-tumblr-plugin")
  .settings(
    libraryDependencies ++= Seq(
      config,
      "com.tumblr" % "jumblr" % "0.0.13"
    )
  )

lazy val youtubePlugin = plugin("truerss-youtube-plugin")
  .settings(
    libraryDependencies ++= Seq(config))

lazy val notifyPlugin = plugin("truerss-notify-plugin")
  .settings(
    libraryDependencies ++= Seq(
      config,
      "com.dorkbox" % "Notify" % "4.5"
    )
  )

lazy val mainPlugin = Project(id = "plugins", base = file("."))
  .disablePlugins(sbtassembly.AssemblyPlugin)
  .aggregate(
    basePlugin,
    redditImageViewerPlugin,
    stackoverflowPlugin,
    tumblrPlugin,
    youtubePlugin,
    notifyPlugin
  )
