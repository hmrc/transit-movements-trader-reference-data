package api.consumption

import models.CustomsOfficesList
import play.api.libs.json.JsArray
import play.api.libs.json.JsObject
import play.api.libs.json.Json
import repositories._
import java.time.Instant
import scala.concurrent.ExecutionContext.Implicits.global
import base.SpecBaseWithAppPerSuite
import play.api.test.Helpers._
import play.api.test._

class RetrieveCustomsOfficesISpec extends SpecBaseWithAppPerSuite with MongoSuite {

  lazy val port: Int = Helpers.testServerPort

  override def beforeEachBlocks: Seq[() => Unit] = super.beforeEachBlocks ++ Seq(
    () => database.flatMap(_.drop).futureValue
  )

  class Setup {

    val repo: ListRepository                 = app.injector.instanceOf[ListRepository]
    val importRepo: ImportIdRepository       = app.injector.instanceOf[ImportIdRepository]
    val dataImportRepo: DataImportRepository = app.injector.instanceOf[DataImportRepository]

    val nextId: ImportId = importRepo.nextId.futureValue

    dataImportRepo
      .insert(DataImport(nextId, CustomsOfficesList, 4, ImportStatus.Complete, Instant.now(), Some(Instant.now())))
      .futureValue

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
      "importId"    -> nextId.value,
      "roles"       -> Json.arr(genRole("TRA"), genRole("DEP"), genRole("DES"))
    )

    val officeTwo = Json.obj(
      "phoneNumber" -> "12345",
      "importId"    -> nextId.value,
      "name"        -> "AB0002",
      "roles"       -> Json.arr(genRole("TRA")),
      "id"          -> "AB00002",
      "countryId"   -> "AB"
    )

    val officeThree = Json.obj(
      "phoneNumber" -> "12345",
      "importId"    -> nextId.value,
      "name"        -> "AB0003",
      "roles"       -> Json.arr(genRole("TRA"), genRole("DEP"), genRole("DES")),
      "id"          -> "AB00003",
      "countryId"   -> "AC"
    )

    val officeFour = Json.obj(
      "phoneNumber" -> "12345",
      "importId"    -> nextId.value,
      "name"        -> "AB0002",
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

    repo.insert(CustomsOfficesList, nextId, customsOffices).futureValue
  }

  "GET /customs-offices" - {
    "return all records if call has no query parameters" in new Setup {
      val request = FakeRequest(
        GET,
        controllers.consumption.routes.CustomsOfficeController.customsOffices().url
      )
      val result = Helpers.route(app, request).value

      status(result) mustEqual 200
      contentAsJson(result).as[JsArray] mustEqual Json.toJson(customsOffices.map(_ - "roles"))
    }

    "return records that match role for a single role" in new Setup {
      val request = FakeRequest(
        GET,
        controllers.consumption.routes.CustomsOfficeController.customsOffices(Seq("TRA")).url
      )
      val result = Helpers.route(app, request).value

      status(result) mustEqual 200
      contentAsJson(result).as[JsArray] mustEqual Json.toJson(
        Seq(
          officeOne,
          officeTwo,
          officeThree
        ).map(_ - "roles")
      )
    }

    "return records that match all roles" in new Setup {
      val request = FakeRequest(
        GET,
        controllers.consumption.routes.CustomsOfficeController.customsOffices(Seq("TRA", "DEP")).url
      )
      val result = Helpers.route(app, request).value

      status(result) mustEqual 200
      contentAsJson(result).as[JsArray] mustEqual Json.toJson(
        Seq(
          officeOne,
          officeThree
        ).map(_ - "roles")
      )
    }

    "return 404 if roles don't match" in new Setup {
      val request = FakeRequest(
        GET,
        controllers.consumption.routes.CustomsOfficeController.customsOffices(Seq("NOP")).url
      )
      val result = Helpers.route(app, request).value

      status(result) mustEqual 404
    }
  }

  "GET /customs-offices/:code" - {
    "return all records if call has no query parameters" in new Setup {
      val request = FakeRequest(
        GET,
        controllers.consumption.routes.CustomsOfficeController.customsOfficesOfTheCountry("AB").url
      )
      val result = Helpers.route(app, request).value

      status(result) mustEqual 200
      contentAsJson(result).as[JsArray] mustEqual Json.toJson(
        Seq(
          officeOne,
          officeTwo,
          officeFour
        ).map(_ - "roles")
      )
    }

    "return records that match role for a single role" in new Setup {
      val request = FakeRequest(
        GET,
        controllers.consumption.routes.CustomsOfficeController.customsOfficesOfTheCountry("AB", Seq("NPM")).url
      )
      val result = Helpers.route(app, request).value

      status(result) mustEqual 200
      contentAsJson(result).as[JsArray] mustEqual Json.toJson(
        Seq(
          officeFour
        ).map(_ - "roles")
      )
    }
  }

  "GET /customs-office/id" - {
    "return the customs office" in new Setup {
      val request = FakeRequest(
        GET,
        controllers.consumption.routes.CustomsOfficeController.getCustomsOffice("AB00002").url
      )
      val result = Helpers.route(app, request).value

      status(result) mustEqual 200
      contentAsJson(result) mustEqual officeTwo - "roles"
    }
  }
}
