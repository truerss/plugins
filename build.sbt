

val basePluginSetting = Seq(
  scalacOptions ++= Seq(
    "-Xlog-free-terms",
    "-deprecation",
    "-feature")
)

val basePlugin = Project(
  id = "base",
  base = file("base")
)
  .settings(basePluginSetting : _*).settings(
  organization := "com.github.truerss",
  name := "base",
  version := "0.0.6",
  scalaVersion := "2.12.2",
  crossScalaVersions := Seq("2.12.2"),
  licenses += ("MIT", url("http://opensource.org/licenses/MIT")),
  publishMavenStyle := true,
  bintrayOrganization := Some("truerss"),
  bintrayVcsUrl := Some("git@github.com:truerss/plugins.git"),
  pomExtra := pomXml,
  publishArtifact in Test := false,
  libraryDependencies ++= Seq("com.typesafe" % "config" % "1.3.0")
)

def pomXml =
  (<url>https://github.com/truerss/plugins</url>
    <scm>
      <url>git@github.com:truerss/plugins.git</url>
      <connection>scm:git:git@github.com:truerss/plugins.git</connection>
    </scm>
    <developers>
      <developer>
        <id>fntz</id>
        <name>mike</name>
        <url>https://github.com/fntz</url>
      </developer>
    </developers>)

val basePluginDeps =  "com.github.truerss" %% "base" % "0.0.6"  % "provided"

val pluginSettings = Seq(
  assemblyOption in assembly := (assemblyOption in assembly).value.copy(includeScala = false),
  organization := "com.github.truerss",
  resolvers += Resolver.bintrayRepo("truerss", "maven"),
  version := "0.0.2",
  scalaVersion := "2.12.2",
  assemblyJarName in assembly := "truerss-reddit-imageviewer-plugin.jar",
  licenses += ("MIT", url("http://opensource.org/licenses/MIT"))
)


lazy val redditImageViewerPlugin = Project(
  id = "truerss-reddit-imageviewer-plugin",
  base = file("truerss-reddit-imageviewer-plugin"),
  settings = pluginSettings ++ Seq(
    name := "truerss-reddit-imageviewer-plugin",
    assemblyJarName in assembly := "truerss-reddit-imageviewer-plugin.jar",
    libraryDependencies ++= Seq(
      basePluginDeps,
      "com.typesafe" % "config" % "1.3.0",
      "org.jsoup" % "jsoup" % "1.8.3",
      "org.scalaj" %% "scalaj-http" % "2.3.0"
    )
  )
)

lazy val stackoverflowPlugin = Project(
  id = "truerss-stackoverflow-plugin",
  base = file("truerss-stackoverflow-plugin"),
  settings = pluginSettings ++ Seq(
    name := "truerss-stackoverflow-plugin",
    assemblyJarName in assembly := "truerss-stackoverflow-plugin.jar",
    libraryDependencies ++= Seq(
      basePluginDeps,
      "com.typesafe" % "config" % "1.3.0",
      "org.jsoup" % "jsoup" % "1.8.3" % "provided"
    )
  )
)

lazy val tumblrPlugin = Project(
  id = "truerss-tumblr-plugin",
  base = file("truerss-tumblr-plugin"),
  settings = pluginSettings ++ Seq(
    name := "truerss-tumblr-plugin",
    assemblyJarName in assembly := "truerss-tumblr-plugin.jar",
    libraryDependencies ++= Seq(
      basePluginDeps,
      "com.typesafe" % "config" % "1.3.0" % "provided",
      "com.tumblr" % "jumblr" % "0.0.11"
    )
  )
)

lazy val youtubePlugin = Project(
  id = "truerss-youtube-plugin",
  base = file("truerss-youtube-plugin"),
  settings = pluginSettings ++ Seq(
    name := "truerss-youtube-plugin",
    assemblyJarName in assembly := "truerss-youtube-plugin.jar",
    libraryDependencies ++= Seq(
      basePluginDeps,
      "com.typesafe" % "config" % "1.3.0"
    )
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
