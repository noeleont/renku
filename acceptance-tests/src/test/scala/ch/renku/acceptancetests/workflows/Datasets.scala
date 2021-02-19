package ch.renku.acceptancetests.workflows

import ch.renku.acceptancetests.model.datasets
import ch.renku.acceptancetests.model.datasets.{DatasetName, DatasetURL}
import ch.renku.acceptancetests.model.projects.ProjectDetails
import ch.renku.acceptancetests.pages.{DatasetPage, ProjectPage}
import ch.renku.acceptancetests.tooling.AcceptanceSpec
import eu.timepit.refined.auto._
import org.openqa.selenium.{WebDriver, WebElement}
import org.scalatestplus.selenium.WebBrowser.{cssSelector, findAll}

import scala.concurrent.duration._
import scala.language.postfixOps

trait Datasets {
  self: AcceptanceSpec =>

  def verifyDatasetCreated(datasetName: DatasetName)(implicit projectDetails: ProjectDetails): Unit = {
    implicit val projectPage = ProjectPage()
    val datasetPage          = DatasetPage(datasetName)
    When("the user navigates to the Datasets tab")
    click on projectPage.Datasets.tab

    And("the events are processed")
    val datasetPageTitle = projectPage.Datasets.title(_: WebDriver)
    reload whenUserCannotSee datasetPageTitle

    Then(s"the user should see a link to the '$datasetName' dataset")
    val datasetLink = projectPage.Datasets.DatasetsList.link(to = datasetPage)(_: WebDriver)
    reload whenUserCannotSee datasetLink
  }

  def `create a dataset`(datasetName: DatasetName)(implicit projectPage: ProjectPage): DatasetPage = {
    import Modification._
    val newDatasetName = datasets.DatasetName("new")
    val newDatasetPage = DatasetPage(newDatasetName)
    Given("the user is on the Datasets tab")
    click on projectPage.Datasets.tab

    When("the user clicks on the Add Dataset button")
    click on projectPage.Datasets.addADatasetButton
    verify that newDatasetPage.DatasetModificationForm.formTitle contains "Add Dataset"

    And(s"the user add the title '$datasetName' to the new dataset")
    `changing its title`(to = datasetName.toString).modifying(newDatasetPage)

    And("the user saves the modification")
    click on newDatasetPage.DatasetModificationForm.datasetSubmitButton sleep (10 seconds)

    val datasetPage = DatasetPage(datasetName)
    Then("the user should see its newly created dataset and the project it belongs to")
    verify browserAt datasetPage
    verify that datasetPage.datasetTitle contains datasetName.value
    val projectLink = datasetPage.ProjectsList.link(projectPage)(_: WebDriver)
    reload whenUserCannotSee projectLink

    datasetPage
  }

  def `import a dataset`(datasetURL: DatasetURL, datasetName: DatasetName)(implicit projectPage: ProjectPage, projectDetails: ProjectDetails): DatasetPage = {
    import Import._
    val generatedDatasetName = datasets.DatasetName.generate
    val newDatasetPage = DatasetPage(generatedDatasetName)
    Given("the user is on the Datasets tab")
    click on projectPage.Datasets.tab

    When("the user clicks on the Add Dataset button")
    click on projectPage.Datasets.addADatasetButton
    verify that newDatasetPage.DatasetModificationForm.formTitle contains "Add Dataset"

    When("the user clicks on the Import Dataset button")
    click on projectPage.Datasets.importDatasetButton
    verify that newDatasetPage.ImportForm.datasetSubmitButton contains "Import Dataset"

    And(s"the user imports a dataset from the datasetURL '$datasetURL'")
    `setting its import url`(to = datasetURL.toString).importing(newDatasetPage)

    And("the user imports the dataset")
    click on newDatasetPage.ImportForm.datasetSubmitButton sleep (40 seconds)

    val datasetPage = DatasetPage(datasetName)
    When("the user navigates to the Datasets tab")
    click on projectPage.Datasets.tab

    Then("the user should see its newly imported dataset")
    val datasetTitle = projectPage.Datasets.datasetTitle(datasetName)(_: WebDriver)
    reload whenUserCannotSee datasetTitle

    datasetPage
  }

  def `navigate to dataset`(datasetPage: DatasetPage)(implicit projectPage: ProjectPage): Unit = {

    Given("the user is on the Datasets tab")
    click on projectPage.Datasets.tab

    When(s"the user clicks on the dataset name")
    click on projectPage.Datasets.DatasetsList.link(to = datasetPage)

    Then(s"the user should see the dataset details")
    verify browserAt datasetPage
  }

  def `modify the dataset`(datasetPage: DatasetPage, by: Modification, and: Modification*)(implicit
                                                                                           projectPage:                      ProjectPage
  ): DatasetPage = {
    Given(s"the user is on the page of the dataset")
    `navigate to dataset`(datasetPage)

    When(s"the user clicks on the modify button")
    click on datasetPage.modifyButton
    verify userCanSee datasetPage.DatasetModificationForm.formTitle

    And(s"the user modifies the dataset by ${by.name}")
    by.modifying(datasetPage)
    and.toList.foreach { by =>
      And(s"by ${by.name}")
      by.modifying(datasetPage)
    }

    And("the user saves the modification")
    click on datasetPage.DatasetModificationForm.datasetSubmitButton

    Then("the user should see its dataset and to which project it belongs")
    verify browserAt datasetPage
    val projectLink = datasetPage.ProjectsList.link(projectPage)(_: WebDriver)
    reload whenUserCannotSee projectLink

    datasetPage
  }

  case class Modification private (name: String, field: DatasetPage => WebElement, newValue: String) {
    def modifying(datasetPage: DatasetPage): Unit = field(datasetPage).enterValue(newValue)
  }

  object Modification {

    def `changing its title`(to: String): Modification =
      Modification("changing its title", datasetPage => datasetPage.DatasetModificationForm.datasetTitleField, to)

    def `changing its description`(to: String): Modification =
      Modification("changing its description", datasetPage => datasetPage.DatasetModificationForm.datasetDescriptionField, to)

  }

  case class Import private (name: String, field: DatasetPage => WebElement, newValue: String) {
    def importing(datasetPage: DatasetPage): Unit = field(datasetPage).enterValue(newValue)
  }

  object Import {

    def `setting its import url`(to: String): Import =
      Import("importing dataset with URL", datasetPage => datasetPage.ImportForm.datasetURLField, to)
  }
}
