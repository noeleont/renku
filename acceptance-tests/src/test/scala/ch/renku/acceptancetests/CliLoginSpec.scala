/*
 * Copyright 2021 Swiss Data Science Center (SDSC)
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

package ch.renku.acceptancetests

import ch.renku.acceptancetests.tooling.console.{CommandOps, rootWorkDirectory}
import ch.renku.acceptancetests.tooling.{AcceptanceSpec, console}
import ch.renku.acceptancetests.workflows._

import java.nio.file.Path

/** Login to Renku from CLI.
  */
class CliLoginSpec extends AcceptanceSpec with Login {

  Scenario("User can log in from CLI") {

    // TODO Make sure that a browser exists in the image -> probably true or otherwise ChromeDriver won't run
    // TODO Define a 'BROWSER' env var with possible chrome values so that webbrowser module picks up the right one:
    // export BROWSER=google-chrome:google-chrome-stable:chrome:chromium:chromium-browser

//    `setup renku CLI`  // FIXME uncomment this when running on CI

    `log in to Renku from CLI`

    When("the reading renku token from the global config file")
    val token = `read renku token`
    Then("token has a value")



//    `log out of Renku` // FIXME there is no logout button on the CLI Token Page. Is it a problem if we don't log out?

    // TODO Verify that token was saved to ~/.renku/renku.ini
  }

  private def `read renku token`: String = {
    implicit val workFolder: Path = rootWorkDirectory

    console %%> c"renku config show http.mohammad.dev.renku.ch"
  }
}
