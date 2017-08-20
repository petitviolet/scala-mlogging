import sbt.Keys._

val libVersion = "1.0"

val scala = "2.12.2"

// https://github.com/scalameta/sbt-macro-example/blob/master/build.sbt
lazy val metaMacroSettings: Seq[Def.Setting[_]] = Seq(
  resolvers += Resolver.sonatypeRepo("releases"),
//  resolvers += Resolver.url(
//    "bintray-sbt-plugin-releases",
//    url("http://dl.bintray.com/content/sbt/sbt-plugin-releases"))(
//    Resolver.ivyStylePatterns),
  resolvers += Resolver.bintrayIvyRepo("scalameta", "maven"),
  addCompilerPlugin("org.scalameta" % "paradise" % "3.0.0-M9" cross CrossVersion.full),
  // for old-style macro
  libraryDependencies ++= Seq(
    "org.scala-lang" % "scala-reflect" % scalaVersion.value,
    "org.scala-lang" % "scala-compiler" % scalaVersion.value % "provided"
  ),
  scalacOptions ++= Seq(
    "-Xplugin-require:macroparadise",
    "-Ymacro-debug-lite"
  )
)

def commonSettings(moduleName: String, _scalaVersion: String = scala) = Seq(
  name := moduleName,
  scalaVersion := _scalaVersion,
  version := "1.0"
)

lazy val loggingRoot = (project in file("."))
  .aggregate(logging, sample)

lazy val logging = (project in file("modules/logging"))
  .settings(commonSettings("logging"))
  .settings(metaMacroSettings)
  .settings(
    libraryDependencies += "org.scalameta" %% "scalameta" % "1.8.0",
    crossScalaVersions := Seq("2.11.11", "2.12.2")
  )

lazy val sample = (project in file("modules/sample"))
  .settings(commonSettings("sample"))
  .settings(metaMacroSettings)
  .dependsOn(logging)

import com.typesafe.sbt.SbtScalariform.ScalariformKeys
import scalariform.formatter.preferences._

scalariformSettings(autoformat = true)
ScalariformKeys.preferences := ScalariformKeys.preferences.value
  .setPreference(AlignParameters, true)
  .setPreference(FormatXml, true)
  .setPreference(DanglingCloseParenthesis, Preserve)
  .setPreference(SpaceInsideBrackets, false)
  .setPreference(IndentWithTabs, false)
  .setPreference(SpaceInsideParentheses, false)
  .setPreference(MultilineScaladocCommentsStartOnFirstLine, false)
  .setPreference(AlignSingleLineCaseStatements, true)
  .setPreference(CompactStringConcatenation, false)
  .setPreference(PlaceScaladocAsterisksBeneathSecondAsterisk, false)
  .setPreference(IndentPackageBlocks, false)
  .setPreference(CompactControlReadability, false)
  .setPreference(SpacesWithinPatternBinders, true)
  .setPreference(AlignSingleLineCaseStatements.MaxArrowIndent, 40)
  .setPreference(DoubleIndentConstructorArguments, false)
  .setPreference(PreserveSpaceBeforeArguments, false)
  .setPreference(SpaceBeforeColon, false)
  .setPreference(RewriteArrowSymbols, false)
  .setPreference(IndentLocalDefs, false)
  .setPreference(IndentSpaces, 2)
