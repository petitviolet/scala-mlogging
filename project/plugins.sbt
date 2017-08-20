logLevel := Level.Warn

resolvers += Resolver.bintrayIvyRepo("scalameta", "maven")
resolvers += Resolver.typesafeRepo("releases")

addSbtPlugin("io.get-coursier" % "sbt-coursier" % "1.0.0-M15")

// Formatter plugins
addSbtPlugin("org.scalariform" % "sbt-scalariform" % "1.8.0")

addSbtPlugin("org.xerial.sbt" % "sbt-sonatype" % "1.1")

addSbtPlugin("com.jsuereth" % "sbt-pgp" % "1.0.0")
