/*
 * Copyright 2022 Swiss Data Science Center (SDSC)
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

package ch.renku.acceptancetests.workflows

import ch.renku.acceptancetests.pages.DatasetsPage
import ch.renku.acceptancetests.pages.DatasetsPage.DatasetSearchOrdering
import ch.renku.acceptancetests.pages.DatasetsPage.DatasetSearchOrdering.ProjectsCount
import ch.renku.acceptancetests.tooling.AcceptanceSpec

import scala.concurrent.duration._

trait DatasetsSearch {
  self: AcceptanceSpec =>

  def `search for dataset with phrase`(phrase: String, orderBy: DatasetSearchOrdering = ProjectsCount): Unit = {
    `try few times before giving up` { _ =>
      go to DatasetsPage sleep (2 seconds)
      verify browserAt DatasetsPage
    }

    When(s"the user types in the '$phrase' in the search field")
    DatasetsPage.searchBox enterValue phrase
    And("opens the order by dropdown menu")
    click on DatasetsPage.orderByDropdownMenu() sleep (1 second)
    And(s"changes the ordering to ${orderBy.value}")
    click on DatasetsPage.orderByDropdownItem(orderBy) sleep (1 second)
    And("clicks the search button")
    click on DatasetsPage.searchButton sleep (1 second)

    DatasetsPage.waitIfBouncing
  }
}
