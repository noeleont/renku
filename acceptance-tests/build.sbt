/*
 * Copyright 2018 Swiss Data Science Center (SDSC)
 * A partnership between École Polytechnique Fédérale de Lausanne (EPFL) and
 * Eidgenössische Technische Hochschule Zürich (ETHZ).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

organization := "ch.renku"
name := "renku-acceptance-tests"
version := "0.1.0"
scalaVersion := "2.13.8"

Test / parallelExecution := false

enablePlugins(AutomateHeaderPlugin)

publish / skip := true
publishTo := Some(Resolver.file("Unused transient repository", file("target/unusedrepo")))

val circeVersion  = "0.13.0"
val http4sVersion = "0.21.23"

libraryDependencies += "ch.qos.logback"          % "logback-classic"     % "1.2.3"
libraryDependencies += "com.github.pureconfig"  %% "pureconfig"          % "0.15.0"      % Test
libraryDependencies += "eu.timepit"             %% "refined"             % "0.9.25"      % Test
libraryDependencies += "io.circe"               %% "circe-core"          % circeVersion  % Test
libraryDependencies += "io.circe"               %% "circe-literal"       % circeVersion  % Test
libraryDependencies += "io.circe"               %% "circe-parser"        % circeVersion  % Test
libraryDependencies += "io.circe"               %% "circe-optics"        % circeVersion  % Test
libraryDependencies += "org.http4s"             %% "http4s-blaze-client" % http4sVersion % Test
libraryDependencies += "org.http4s"             %% "http4s-circe"        % http4sVersion % Test
libraryDependencies += "org.scalacheck"         %% "scalacheck"          % "1.15.4"      % Test
libraryDependencies += "org.scalatest"          %% "scalatest"           % "3.2.9"       % Test
libraryDependencies += "org.scalatestplus"      %% "selenium-3-141"      % "3.2.9.0"     % Test
libraryDependencies += "org.seleniumhq.selenium" % "selenium-java"       % "3.141.59"    % Test
libraryDependencies += "org.slf4j"               % "slf4j-log4j12"       % "1.7.30"      % Test
libraryDependencies += "org.typelevel"          %% "cats-effect"         % "2.5.1"       % Test

scalacOptions += "-feature"
scalacOptions += "-unchecked"
scalacOptions += "-deprecation"
scalacOptions += "-Xfatal-warnings"
scalacOptions += "-language:postfixOps"

organizationName := "Swiss Data Science Center (SDSC)"
startYear := Some(java.time.LocalDate.now().getYear)
licenses += ("Apache-2.0", new URL("https://www.apache.org/licenses/LICENSE-2.0.txt"))
headerLicense := Some(
  HeaderLicense.Custom(
    s"""Copyright ${java.time.LocalDate.now().getYear} Swiss Data Science Center (SDSC)
       |A partnership between École Polytechnique Fédérale de Lausanne (EPFL) and
       |Eidgenössische Technische Hochschule Zürich (ETHZ).
       |
       |Licensed under the Apache License, Version 2.0 (the "License");
       |you may not use this file except in compliance with the License.
       |You may obtain a copy of the License at
       |
       |    http://www.apache.org/licenses/LICENSE-2.0
       |
       |Unless required by applicable law or agreed to in writing, software
       |distributed under the License is distributed on an "AS IS" BASIS,
       |WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
       |See the License for the specific language governing permissions and
       |limitations under the License.""".stripMargin
  )
)
