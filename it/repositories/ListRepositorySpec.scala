package repositories

import models.{CountryCodesFullList, CustomsOfficesList}
import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers
import org.scalatest.{BeforeAndAfterEach, OptionValues}
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.libs.json.{JsObject, Json}
import play.api.test.Helpers.running
import reactivemongo.api.Cursor
import reactivemongo.play.json.collection.Helpers.idWrites
import reactivemongo.play.json.collection.JSONCollection

import scala.concurrent.ExecutionContext.Implicits.global

class ListRepositorySpec
  extends AnyFreeSpec
    with Matchers
    with MongoSuite
    with BeforeAndAfterEach
    with ScalaFutures
    with IntegrationPatience
    with OptionValues {

  override def beforeEach(): Unit = {
    database.flatMap(_.drop).futureValue
    super.beforeEach()
  }

  "insert" - {

    "must insert records, adding the import id to them" in {

      val app = new GuiceApplicationBuilder().build()

      running(app) {
        val repo = app.injector.instanceOf[ListRepository]

        val importId = ImportId(1)
        val data = Seq(Json.obj("code" -> "GB"), Json.obj("code" -> "FR"))

        val result = repo.insert(CountryCodesFullList, importId, data).futureValue

        result mustEqual true

        val databaseRecords = database.flatMap {
          _.collection[JSONCollection](CountryCodesFullList.listName)
            .find[JsObject, JsObject](Json.obj(), None)
            .cursor[JsObject]()
            .collect[List](-1, Cursor.FailOnError())
        }.futureValue.map(jsObject => jsObject - "_id")

        val expectedRecords = List(
          Json.obj("code" -> "GB", "importId" -> 1),
          Json.obj("code" -> "FR", "importId" -> 1)
        )

        databaseRecords must contain theSameElementsAs expectedRecords
      }
    }

    // TODO: Add a test for an attempt to insert duplicates

    "must get all records that match a given selector" in {

      val app = new GuiceApplicationBuilder().build()

      running(app) {
        val repo = app.injector.instanceOf[ListRepository]

        val import1Data = Seq(Json.obj("code" -> "GB"), Json.obj("code" -> "FR"))
        val import2Data = Seq(Json.obj("code" -> "GB"), Json.obj("code" -> "IT"))

        repo.insert(CountryCodesFullList, ImportId(1), import1Data).futureValue
        repo.insert(CountryCodesFullList, ImportId(2), import2Data).futureValue

        val results =
          repo
            .many(CountryCodesFullList, Selector.All(ImportId(2)))
            .futureValue
            .map(jsObject => jsObject - "_id")

        val expectedResults = import2Data.map(jsObject => jsObject ++ Json.obj("importId" -> 2))

        results must contain theSameElementsAs expectedResults
      }
    }

    "must get a record if one exists that matches the selector" in {

      val app = new GuiceApplicationBuilder().build()

      running(app) {
        val repo = app.injector.instanceOf[ListRepository]

        val import1Data = Seq(Json.obj("officeId" -> "GB000060"), Json.obj("officeId" -> "IT010101"))
        val import2Data = Seq(Json.obj("officeId" -> "GB000060"), Json.obj("officeId" -> "IT010101"))

        repo.insert(CustomsOfficesList, ImportId(1), import1Data).futureValue
        repo.insert(CustomsOfficesList, ImportId(2), import2Data).futureValue

        val result =
          repo
            .one(CustomsOfficesList, Selector.ByCustomsOfficeId(ImportId(1), "GB000060"))
            .futureValue
            .map(jsObject => jsObject - "_id")
            .value

        result mustEqual Json.obj("officeId" -> "GB000060", "importId" -> 1)
      }
    }

    "must return None if no record that matches the selector exists" in {

      val app = new GuiceApplicationBuilder().build()

      running(app) {
        val repo = app.injector.instanceOf[ListRepository]

        val import1Data = Seq(Json.obj("officeId" -> "GB000060", "officeId" -> "IT010101"))
        val import2Data = Seq(Json.obj("officeId" -> "GB000060", "officeId" -> "FR202020"))

        repo.insert(CustomsOfficesList, ImportId(1), import1Data).futureValue
        repo.insert(CustomsOfficesList, ImportId(2), import2Data).futureValue

        val result =
          repo
            .one(CustomsOfficesList, Selector.ByCustomsOfficeId(ImportId(1), "FR202020"))
            .futureValue
            .map(jsObject => jsObject - "_id")

        result must not be defined
      }
    }
  }
}
