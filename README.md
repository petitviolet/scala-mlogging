# @logging annotation for tracking method input and output

[![Maven Central](https://maven-badges.herokuapp.com/maven-central/net.petitviolet/logging_2.12/badge.svg)](https://maven-badges.herokuapp.com/maven-central/net.petitviolet/logging_2.12)

This repository provides a `@logging` annotation.

## @logging annotation

This annotation inserts logging before/after invoking annotated method.

```scala
val logger = new {
    def info(s: => String): Unit =
        println(s"${LocalDateTime.now()}: $s")
}

// provide your own logger(just a `String => Unit` function)
@logging(logger.info)
def add(i: Int, j: Int): Int = {
    i + j
}

println(add(1, 2))
```

Outputs are below.

> 2017-08-19T23:05:26.279: [start]add(1, 2)  
> 2017-08-19T23:05:26.308: [end]add(1, 2) => 3  
> 3

Also see [samples](https://github.com/petitviolet/scala-logging/blob/master/sample/src/main/scala/net/petitviolet/logging/meta/loggingApp.scala).

## @timeLogging annotation

This annotation inserts logging before invoking annotated method with elapsed milli seconds.

```scala
val logger = new {
  def info(s: => String): Unit =
    println(s"${LocalDateTime.now()}: $s")
}

@timeLogging(logger.info)
def add(i: Int, j: Int): Int = {
  i + j
}
println(add(1, 2))

@timeLogging(logger.info)
def add2(i: Int)(j: Int)(): Int = {
  Thread.sleep(100)
  i + j
}

println(add2(2)(3))
```

> 2017-09-07T09:36:26.335: [start]add(1, 2)  
> 2017-09-07T09:36:26.400: [end][0 ms]add(1, 2) => 3  
> 3  
> 2017-09-07T09:36:26.406: [start]add2(2)(3)()  
> 2017-09-07T09:36:26.515: [end][107 ms]add2(2)(3)() => 5  
> 5  

Also see [samples](https://github.com/petitviolet/scala-logging/blob/master/sample/src/main/scala/net/petitviolet/logging/meta_sample/timeLoggingApp.scala).

# setup

in build.sbt

```scala
lazy val metaMacroSettings: Seq[Def.Setting[_]] = Seq(
  resolvers += Resolver.sonatypeRepo("releases"),
  resolvers += Resolver.bintrayIvyRepo("scalameta", "maven"),
  addCompilerPlugin("org.scalameta" % "paradise" % "3.0.0-M9" cross CrossVersion.full),
  scalacOptions ++= Seq(
    "-Xplugin-require:macroparadise",
    // "-Ymacro-debug-lite" // for debug
  )
)

project.settings(
  metaMacroSettings,
  libraryDependencies += "net.petitviolet" %% "logging" % "<latest-version>"
)
```

# License

[MIT License](https://petitviolet.mit-license.org/)
