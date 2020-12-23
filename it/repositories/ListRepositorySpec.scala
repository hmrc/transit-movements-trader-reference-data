package repositories

import models.{CountryCodesFullList, CustomsOfficesList, ReferenceDataList}
import org.scalacheck.Gen
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

  ".insert" - {

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
  }

  ".many" - {

    "must get all records that match a given selector" in {

      val app = new GuiceApplicationBuilder().build()

      running(app) {
        val repo = app.injector.instanceOf[ListRepository]

        val data = Seq(Json.obj("code" -> "GB"), Json.obj("code" -> "FR"))


        repo.insert(CountryCodesFullList, ImportId(1), data).futureValue

        val results =
          repo
            .many(CountryCodesFullList, Selector.All())
            .futureValue
            .map(jsObject => jsObject - "_id")

        val expectedResults = data.map(jsObject => jsObject ++ Json.obj("importId" -> 1))

        results must contain theSameElementsAs expectedResults
      }
    }
  }

  ".one" - {

    "must get a record if one exists that matches the selector" in {

      val app = new GuiceApplicationBuilder().build()

      running(app) {
        val repo = app.injector.instanceOf[ListRepository]

        val data = Seq(Json.obj("id" -> "GB000060"), Json.obj("id" -> "IT010101"))

        repo.insert(CustomsOfficesList, ImportId(1), data).futureValue

        val result =
          repo
            .one(CustomsOfficesList, Selector.ById("GB000060"))
            .futureValue
            .map(jsObject => jsObject - "_id")
            .value

        result mustEqual Json.obj("id" -> "GB000060", "importId" -> 1)
      }
    }

    "must return None if no record that matches the selector exists" in {

      val app = new GuiceApplicationBuilder().build()

      running(app) {
        val repo = app.injector.instanceOf[ListRepository]

        val data = Seq(Json.obj("id" -> "GB000060", "id" -> "IT010101"))

        repo.insert(CustomsOfficesList, ImportId(1), data).futureValue

        val result =
          repo
            .one(CustomsOfficesList, Selector.ById("FR202020"))
            .futureValue
            .map(jsObject => jsObject - "_id")

        result must not be defined
      }
    }
  }

  ".deleteOldImports" - {

    "must delete records with an importId less than the id specified" in {

      val app = new GuiceApplicationBuilder().build()

      val list = Gen.oneOf(ReferenceDataList.values.toList).sample.value

      running(app) {
        val repo = app.injector.instanceOf[ListRepository]

        val record = Json.obj("id" -> 1)
        val data = Seq(record)

        repo.insert(list, ImportId(1), data).futureValue
        repo.insert(list, ImportId(2), data).futureValue
        repo.insert(list, ImportId(3), data).futureValue
        repo.insert(list, ImportId(4), data).futureValue

        val result = repo.deleteOldImports(list, ImportId(3)).futureValue

        result mustEqual true

        val import1Records = repo.many(list, Selector.All().forImport(ImportId(1))).futureValue
        val import2Records = repo.many(list, Selector.All().forImport(ImportId(2))).futureValue
        val import3Records = repo.many(list, Selector.All().forImport(ImportId(3))).futureValue
        val import4Records = repo.many(list, Selector.All().forImport(ImportId(4))).futureValue

        import1Records mustBe empty
        import2Records mustBe empty
        import3Records.map(_ - "_id") must contain theSameElementsAs Seq(record ++ Json.obj("importId" -> 3))
        import4Records.map(_ - "_id") must contain theSameElementsAs Seq(record ++ Json.obj("importId" -> 4))
      }
    }
  }
}
