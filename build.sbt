import sbt.Keys._

val libVersion = "0.3.1"

val scala = "2.12.4"

val groupId = "net.petitviolet"

val projectName = "mlogging"

// https://github.com/scalameta/sbt-macro-example/blob/master/build.sbt
lazy val metaMacroSettings: Seq[Def.Setting[_]] = Seq(
  resolvers += Resolver.sonatypeRepo("releases"),
//  resolvers += Resolver.url(
//    "bintray-sbt-plugin-releases",
//    url("http://dl.bintray.com/content/sbt/sbt-plugin-releases"))(
//    Resolver.ivyStylePatterns),
  resolvers += Resolver.bintrayIvyRepo("scalameta", "maven"),
  addCompilerPlugin("org.scalameta" % "paradise" % "3.0.0-M10" cross CrossVersion.full),
  scalacOptions ++= Seq(
    "-Xplugin-require:macroparadise",
    "-Ymacro-debug-lite"
  )
)

def commonSettings(moduleName: String, _scalaVersion: String = scala) = Seq(
  organization := groupId,
  name := moduleName,
  scalaVersion := _scalaVersion,
  version := libVersion
)

lazy val mloggingRoot = (project in file("."))
  .aggregate(mlogging, sample)

lazy val mlogging = (project in file("mlogging"))
  .settings(commonSettings(projectName, scala): _*)
  .settings(metaMacroSettings)
  .settings(
    crossScalaVersions := Seq("2.11.11", "2.12.4"),
    libraryDependencies += "org.scalameta" %% "scalameta" % "1.8.0"
  )

lazy val sample = (project in file("sample"))
  .settings(commonSettings("sample"): _*)
  .settings(metaMacroSettings)
//  .settings(libraryDependencies += groupId %% projectName % libVersion)
  .dependsOn(mlogging)

