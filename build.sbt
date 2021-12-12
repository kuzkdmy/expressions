ThisBuild / scalaVersion := "2.13.6"
ThisBuild / organization := "io.kdg.expressions"
ThisBuild / organizationName := "kdg"

Global / concurrentRestrictions += Tags.limit(Tags.Test, 1)
ThisBuild / parallelExecution := false

lazy val root = (project in file("."))
  .enablePlugins(JavaAppPackaging, DockerPlugin)
  .settings(
    name := "expressions",
    addCompilerPlugin("org.typelevel" % "kind-projector"     % "0.13.0" cross CrossVersion.full),
    addCompilerPlugin("com.olegpy"   %% "better-monadic-for" % "0.3.1"),
    libraryDependencies ++= Seq(
      "ch.qos.logback"                 % "logback-classic"               % "1.3.0-alpha10",
      "com.beachape"                  %% "enumeratum-circe"              % "1.7.0",
      "com.github.pureconfig"         %% "pureconfig"                    % "0.17.1",
      "com.softwaremill.sttp.client3" %% "async-http-client-backend-zio" % "3.3.17",
      "com.softwaremill.sttp.tapir"   %% "tapir-core"                    % "0.19.0-M9",
      "com.softwaremill.sttp.tapir"   %% "tapir-derevo"                  % "0.19.0-M9",
      "com.softwaremill.sttp.tapir"   %% "tapir-enumeratum"              % "0.19.0-M9",
      "com.softwaremill.sttp.tapir"   %% "tapir-json-circe"              % "0.19.0-M9",
      "com.softwaremill.sttp.tapir"   %% "tapir-newtype"                 % "0.19.0-M9",
      "com.softwaremill.sttp.tapir"   %% "tapir-openapi-circe-yaml"      % "0.19.0-M9",
      "com.softwaremill.sttp.tapir"   %% "tapir-openapi-docs"            % "0.19.0-M9",
      "com.softwaremill.sttp.tapir"   %% "tapir-prometheus-metrics"      % "0.19.0-M9",
      "com.softwaremill.sttp.tapir"   %% "tapir-swagger-ui-zio-http"     % "0.19.0-M4",
      "com.softwaremill.sttp.tapir"   %% "tapir-zio-http"                % "0.19.0-M9",
      "dev.zio"                       %% "zio"                           % "1.0.12",
      "dev.zio"                       %% "zio"                           % "1.0.12",
      "dev.zio"                       %% "zio-interop-cats"              % "3.2.9.0",
      "dev.zio"                       %% "zio-metrics-prometheus"        % "1.0.12",
      "dev.zio"                       %% "zio-test"                      % "1.0.12" % "test",
      "dev.zio"                       %% "zio-test"                      % "1.0.12" % "test",
      "dev.zio"                       %% "zio-test-sbt"                  % "1.0.12" % "test",
      "io.circe"                      %% "circe-generic-extras"          % "0.14.1",
      "io.circe"                      %% "circe-generic-extras"          % "0.14.1",
      "io.circe"                      %% "circe-parser"                  % "0.14.1",
      "io.d11"                        %% "zhttp"                         % "1.0.0.0-RC17",
      "net.logstash.logback"           % "logstash-logback-encoder"      % "7.0.1",
      "tf.tofu"                       %% "derevo-cats"                   % "0.12.8",
      "tf.tofu"                       %% "derevo-circe"                  % "0.12.8"
    ),
    semanticdbEnabled := true,
    semanticdbVersion := scalafixSemanticdb.revision,
    testFrameworks += new TestFramework("zio.test.sbt.ZTestFramework"),
    scalacOptions ++= Seq(
      "-deprecation", // Emit warning and location for usages of deprecated APIs.
      "-encoding",
      "utf-8", // Specify character encoding used by source files.
      "-explaintypes", // Explain type errors in more detail.
      "-feature", // Emit warning and location for usages of features that should be imported explicitly.
      "-language:existentials", // Existential types (besides wildcard types) can be written and inferred
      "-language:experimental.macros", // Allow macro definition (besides implementation and application)
      "-language:higherKinds", // Allow higher-kinded types
      "-language:implicitConversions", // Allow definition of implicit functions called views
      "-unchecked", // Enable additional warnings where generated code depends on assumptions.
      "-Xcheckinit", // Wrap field accessors to throw an exception on uninitialized access.
//      "-Xfatal-warnings", // Fail the compilation if there are any warnings.
      "-Xlint:adapted-args", // Warn if an argument list is modified to match the receiver.
      "-Xlint:constant", // Evaluation of a constant arithmetic expression results in an error.
      "-Xlint:delayedinit-select", // Selecting member of DelayedInit.
      "-Xlint:doc-detached", // A Scaladoc comment appears to be detached from its element.
      "-Xlint:inaccessible", // Warn about inaccessible types in method signatures.
      "-Xlint:missing-interpolator", // A string literal appears to be missing an interpolator id.
      "-Xlint:option-implicit", // Option.apply used implicit view.
      "-Xlint:package-object-classes", // Class or object defined in package object.
      "-Xlint:poly-implicit-overload", // Parameterized overloaded implicit methods are not visible as view bounds.
      "-Xlint:private-shadow", // A private field (or class parameter) shadows a superclass field.
      "-Xlint:stars-align", // Pattern sequence wildcard must align with sequence component.
      "-Xlint:type-parameter-shadow", // A local type parameter shadows a type already in scope.
      "-Ywarn-dead-code", // Warn when dead code is identified.
      "-Ywarn-extra-implicit", // Warn when more than one implicit parameter section is defined.
      "-Ywarn-numeric-widen", // Warn when numerics are widened.
      "-Ywarn-unused:implicits", // Warn if an implicit parameter is unused.
      "-Ywarn-unused:imports", // Warn if an import selector is not referenced.
      "-Ywarn-unused:locals", // Warn if a local definition is unused.
      "-Ywarn-unused:params", // Warn if a value parameter is unused.
      "-Ywarn-unused:patvars", // Warn if a variable bound in a pattern is unused.
      "-Ywarn-unused:privates", // Warn if a private member is unused.
      "-Ywarn-value-discard", // Warn when non-Unit expression results are unused.
      "-Ymacro-annotations"
    )
  )
