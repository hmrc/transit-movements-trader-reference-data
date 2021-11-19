package repositories

import models.CustomsOfficesList
import org.scalatest.concurrent.IntegrationPatience
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers
import org.scalatest.BeforeAndAfterEach
import org.scalatest.OptionValues
import play.api.Application
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.libs.json.JsObject
import play.api.libs.json.Json
import repositories.Selector.ByCountry
import repositories.Selector.ById
import repositories.Selector.OptionallyByRole

class CustomsOfficeListRepositorySpec
    extends AnyFreeSpec
    with Matchers
    with MongoSuite
    with ScalaFutures
    with IntegrationPatience
    with OptionValues
    with BeforeAndAfterEach
    with FailOnUnindexedQueries {

  override def beforeEach(): Unit = {
    dropDatabase()
    super.beforeEach()
  }

  class Setup {

    private val appBuilder: Application =
      new GuiceApplicationBuilder().build()

    val repo: ListRepository = appBuilder.injector.instanceOf[ListRepository]

    def genRole(role: String): JsObject = Json.obj(
      "seasonStartDate"                 -> "20180101",
      "role"                            -> role,
      "seasonCode"                      -> 1,
      "openingHoursTimeFirstPeriodFrom" -> "0800",
      "dayInTheWeekBeginDay"            -> 1,
      "openingHoursTimeFirstPeriodTo"   -> "2000",
      "seasonName"                      -> "Well",
      "trafficType"                     -> "N/A",
      "dayInTheWeekEndDay"              -> 5,
      "seasonEndDate"                   -> "21001212"
    )

    val officeOne = Json.obj(
      "name"        -> "AB0001",
      "phoneNumber" -> "12345",
      "id"          -> "AB00001",
      "countryId"   -> "AB",
      "importId"    -> 12345,
      "roles"       -> Json.arr(genRole("TRA"), genRole("DEP"), genRole("DES"))
    )

    val officeTwo = Json.obj(
      "phoneNumber" -> "12345",
      "importId"    -> 12345,
      "name"        -> "AB0002",
      "roles"       -> Json.arr(genRole("TRA")),
      "id"          -> "AB00002",
      "countryId"   -> "AB"
    )

    val officeThree = Json.obj(
      "phoneNumber" -> "12345",
      "importId"    -> 12345,
      "name"        -> "AB0003",
      "roles"       -> Json.arr(genRole("TRA"), genRole("DEP"), genRole("DES")),
      "id"          -> "AB00003",
      "countryId"   -> "AC"
    )

    val officeFour = Json.obj(
      "phoneNumber" -> "12345",
      "importId"    -> 12345,
      "name"        -> "AB0004",
      "roles"       -> Json.arr(genRole("DEP"), genRole("DES"), genRole("NPM")),
      "id"          -> "AB00004",
      "countryId"   -> "AB"
    )

    val customsOffices = Seq(
      officeOne,
      officeTwo,
      officeThree,
      officeFour
    )

    repo.insert(CustomsOfficesList, ImportId(12345), customsOffices).futureValue
  }

  "customsOfficeListRepository" - {
    "fetch all offices when roles is empty" in new Setup {
      val returnedOffices: Seq[JsObject] = repo
        .many(CustomsOfficesList, OptionallyByRole(Nil))
        .futureValue
        .map(_ - "_id")

      returnedOffices mustEqual customsOffices
    }

    "fetch offices with specific roles if roles is not empty" in new Setup {
      val returnedOffices: Seq[JsObject] = repo.many(CustomsOfficesList, OptionallyByRole(Seq("TRA"))).futureValue.map(_ - "_id")

      returnedOffices mustEqual Seq(
        officeOne,
        officeTwo,
        officeThree
      )
    }

    "fetch offices with specific roles if roles has multiple roles" in new Setup {
      val returnedOffices: Seq[JsObject] = repo.many(CustomsOfficesList, OptionallyByRole(Seq("TRA", "DEP"))).futureValue.map(_ - "_id")

      returnedOffices mustEqual Seq(
        officeOne,
        officeThree
      )
    }

    "fetch all offices by country when roles is empty" in new Setup {
      val returnedOffices: Seq[JsObject] = repo
        .many(CustomsOfficesList, ByCountry("AB") and OptionallyByRole(Nil))
        .futureValue
        .map(_ - "_id")

      returnedOffices mustEqual Seq(
        officeOne,
        officeTwo,
        officeFour
      )
    }

    "fetch offices by country with specific roles if roles is not empty" in new Setup {
      val returnedOffices: Seq[JsObject] = repo.many(CustomsOfficesList, OptionallyByRole(Seq("NPM"))).futureValue.map(_ - "_id")

      returnedOffices mustEqual Seq(
        officeFour
      )
    }

    "fetch office by id" in new Setup {
      val returnedOffices: JsObject = repo.one(CustomsOfficesList, ById("AB00002")).futureValue.map(_ - "_id").value

      returnedOffices mustEqual officeTwo
    }
  }
}
