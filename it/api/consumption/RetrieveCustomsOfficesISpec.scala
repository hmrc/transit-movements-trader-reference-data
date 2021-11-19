package api.consumption

import models.CustomsOfficesList
import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers
import org.scalatest.{BeforeAndAfterEach, OptionValues}
import org.scalatestplus.play.guice.GuiceOneServerPerSuite
import play.api.Application
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.libs.json.{JsArray, JsObject, JsValue, Json}
import play.api.libs.ws.{WSClient, WSResponse}
import repositories._

import java.time.Instant
import scala.concurrent.ExecutionContext.Implicits.global

class RetrieveCustomsOfficesISpec extends AnyFreeSpec
  with Matchers
  with MongoSuite
  with BeforeAndAfterEach
  with ScalaFutures
  with IntegrationPatience
  with OptionValues with GuiceOneServerPerSuite {

  override def beforeEach(): Unit = {
    dropDatabase()
    super.beforeEach()
  }

  class Setup {
    private val appBuilder: Application =
      new GuiceApplicationBuilder().build()

    val repo: ListRepository = appBuilder.injector.instanceOf[ListRepository]
    val importRepo: ImportIdRepository = appBuilder.injector.instanceOf[ImportIdRepository]
    val dataImportRepo: DataImportRepository = appBuilder.injector.instanceOf[DataImportRepository]



    val nextId: ImportId = importRepo.nextId.futureValue

    dataImportRepo
      .insert(DataImport(nextId, CustomsOfficesList, 4, ImportStatus.Complete, Instant.now(), Some(Instant.now())))
      .futureValue

    def genRole(role: String): JsObject = Json.obj(
      "seasonStartDate" -> "20180101",
      "role" -> role,
      "seasonCode" -> 1,
      "openingHoursTimeFirstPeriodFrom" -> "0800",
      "dayInTheWeekBeginDay" -> 1,
      "openingHoursTimeFirstPeriodTo" -> "2000",
      "seasonName" -> "Well",
      "trafficType" -> "N/A",
      "dayInTheWeekEndDay" -> 5,
      "seasonEndDate" -> "21001212"
    )

    val officeOne = Json.obj(
      "name" -> "AB0001",
      "phoneNumber" -> "12345",
      "id" -> "AB00001",
      "countryId" -> "AB",
      "importId" -> nextId.value,
      "roles" -> Json.arr(genRole("TRA"), genRole("DEP"), genRole("DES"))
    )

    val officeTwo = Json.obj(
      "phoneNumber" -> "12345",
      "importId" -> nextId.value,
      "name" -> "AB0002",
      "roles" -> Json.arr(genRole("TRA")),
      "id" -> "AB00002",
      "countryId" -> "AB"
    )

    val officeThree = Json.obj(
      "phoneNumber" -> "12345",
      "importId" -> nextId.value,
      "name" -> "AB0003",
      "roles" -> Json.arr(genRole("TRA"), genRole("DEP"), genRole("DES")),
      "id" -> "AB00003",
      "countryId" -> "AC"
    )

    val officeFour = Json.obj(
      "phoneNumber" -> "12345",
      "importId" -> nextId.value,
      "name" -> "AB0002",
      "roles" -> Json.arr(genRole("DEP"), genRole("DES"), genRole("NPM")),
      "id" -> "AB00004",
      "countryId" -> "AB"
    )

    val customsOffices = Seq(
      officeOne, officeTwo, officeThree, officeFour
    )

    repo.insert(CustomsOfficesList, nextId, customsOffices).futureValue

    val ws: WSClient = app.injector.instanceOf[WSClient]
  }



  "GET /customs-offices" - {
    "return all records if call has no query parameters" in new Setup {
      val result: WSResponse = ws
        .url(s"http://localhost:$port/transit-movements-trader-reference-data/customs-offices")
        .get()
        .futureValue

      result.status mustBe 200
      result.body[JsValue].asOpt[JsArray].value mustEqual Json.toJson(customsOffices.map(_ - "roles"))
    }

    "return records that match role for a single role" in new Setup {
      val result: WSResponse = ws
        .url(s"http://localhost:$port/transit-movements-trader-reference-data/customs-offices?role=TRA")
        .get()
        .futureValue

      result.status mustBe 200
      result.body[JsValue].asOpt[JsArray].value mustEqual Json.toJson(Seq(
        officeOne, officeTwo, officeThree
      ).map(_ - "roles"))
    }

    "return records that match all roles" in new Setup {
      val result: WSResponse = ws
        .url(s"http://localhost:$port/transit-movements-trader-reference-data/customs-offices?role=TRA&role=DEP")
        .get()
        .futureValue

      result.status mustBe 200
      result.body[JsValue].asOpt[JsArray].value mustEqual Json.toJson(Seq(
        officeOne, officeThree
      ).map(_ - "roles"))
    }

    "return 404 if roles don't match" in new Setup {
      val result: WSResponse = ws
        .url(s"http://localhost:$port/transit-movements-trader-reference-data/customs-offices?role=NOP")
        .get()
        .futureValue

      result.status mustBe 404
    }
  }

  "GET /customs-offices/:code" - {
    "return all records if call has no query parameters" in new Setup {
      val result: WSResponse = ws
        .url(s"http://localhost:$port/transit-movements-trader-reference-data/customs-offices/AB")
        .get()
        .futureValue

      result.status mustBe 200
      result.body[JsValue].asOpt[JsArray].value mustEqual Json.toJson(Seq(
        officeOne, officeTwo, officeFour
      ).map(_ - "roles"))
    }

    "return records that match role for a single role" in new Setup {
      val result: WSResponse = ws
        .url(s"http://localhost:$port/transit-movements-trader-reference-data/customs-offices/AB?role=NPM")
        .get()
        .futureValue

      result.status mustBe 200
      result.body[JsValue].asOpt[JsArray].value mustEqual Json.toJson(Seq(
        officeFour
      ).map(_ - "roles"))
    }
  }

  "GET /customs-office/id" - {
    "return the customs office" in new Setup {
      val result: WSResponse = ws
        .url(s"http://localhost:$port/transit-movements-trader-reference-data/customs-office/AB00002")
        .get()
        .futureValue

      result.status mustBe 200
      result.body[JsValue].asOpt[JsObject].value mustEqual officeTwo - "roles"
    }
  }
}
