package repositories.services

import java.time.Instant

import models.ReferenceDataList
import org.scalacheck.Gen
import org.scalactic.Uniformity
import org.scalatest.{BeforeAndAfterEach, OptionValues}
import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.libs.json.{JsObject, Json}
import play.api.test.Helpers.running
import repositories.{DataImport, DataImportRepository, ImportId, ImportStatus, ListRepository, MongoSuite, Selector}

import scala.concurrent.ExecutionContext.Implicits.global

class CurrentReferenceDataServiceSpec
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

  private val madeMongoIdAgnostic: Uniformity[JsObject] = new Uniformity[JsObject] {
    override def normalizedOrSame(b: Any): Any =
      b match {
        case jsObject: JsObject => normalized(jsObject)
        case _                  => b
      }

    override def normalizedCanHandle(b: Any): Boolean =
      b.isInstanceOf[JsObject]

    override def normalized(a: JsObject): JsObject =
      a - "_id"
  }

  ".one" - {

    "when data has been imported" - {

      "and records match the selector used" - {

        "including one from the current import" - {

          "must return the record from the current import" in {

            val list = Gen.oneOf(ReferenceDataList.values.toList).sample.value

            val import1 = DataImport(ImportId(1), list, 1, ImportStatus.Complete, Instant.now, Some(Instant.now))
            val import2 = DataImport(ImportId(2), list, 1, ImportStatus.Complete, Instant.now, Some(Instant.now))
            val import1Data = Seq(Json.obj("id" -> "1", "value" -> "import 1 value"))
            val import2Data = Seq(Json.obj("id" -> "1", "value" -> "import 2 value"))

            val app = new GuiceApplicationBuilder().build()

            running(app) {

              val dataImportRepo = app.injector.instanceOf[DataImportRepository]
              val listRepo       = app.injector.instanceOf[ListRepository]
              val service        = app.injector.instanceOf[CurrentReferenceDataService]

              dataImportRepo.insert(import1).futureValue
              dataImportRepo.insert(import2).futureValue
              listRepo.insert(list, ImportId(1), import1Data).futureValue
              listRepo.insert(list, ImportId(2), import2Data).futureValue

              val result = service.one(list, Selector.ById("1")).futureValue

              val expectedResult = import2Data.head ++ Json.obj("importId" -> 2)

              result.value must equal(expectedResult) (after being madeMongoIdAgnostic)
            }
          }
        }

        "but not from the current import" - {

          "must return None" in {

            val list = Gen.oneOf(ReferenceDataList.values.toList).sample.value

            val import1 = DataImport(ImportId(1), list, 1, ImportStatus.Complete, Instant.now, Some(Instant.now))
            val import2 = DataImport(ImportId(2), list, 1, ImportStatus.Complete, Instant.now, Some(Instant.now))
            val import1Data = Seq(Json.obj("id" -> "1", "value" -> "import 1 value"))

            val app = new GuiceApplicationBuilder().build()

            running(app) {

              val dataImportRepo = app.injector.instanceOf[DataImportRepository]
              val listRepo       = app.injector.instanceOf[ListRepository]
              val service        = app.injector.instanceOf[CurrentReferenceDataService]

              dataImportRepo.insert(import1).futureValue
              dataImportRepo.insert(import2).futureValue
              listRepo.insert(list, ImportId(1), import1Data).futureValue

              val result = service.one(list, Selector.ById("1")).futureValue

              result must not be defined
            }
          }
        }
      }

      "and no records match the selector used" - {

        "must return None" in {

          val list = Gen.oneOf(ReferenceDataList.values.toList).sample.value

          val import1 = DataImport(ImportId(1), list, 1, ImportStatus.Complete, Instant.now, Some(Instant.now))
          val import1Data = Seq(Json.obj("id" -> "1", "value" -> "import 1 value"))

          val app = new GuiceApplicationBuilder().build()

          running(app) {

            val dataImportRepo = app.injector.instanceOf[DataImportRepository]
            val listRepo       = app.injector.instanceOf[ListRepository]
            val service        = app.injector.instanceOf[CurrentReferenceDataService]

            dataImportRepo.insert(import1).futureValue
            listRepo.insert(list, ImportId(1), import1Data).futureValue

            val result = service.one(list, Selector.ById("2")).futureValue

            result must not be defined
          }
        }
      }
    }

    "when no data has been imported" - {

      "must return None" in {

        val list = Gen.oneOf(ReferenceDataList.values.toList).sample.value

        val app = new GuiceApplicationBuilder().build()

        running(app) {

          val service = app.injector.instanceOf[CurrentReferenceDataService]

          val result = service.one(list, Selector.ById("1")).futureValue

          result must not be defined
        }
      }
    }
  }

  ".many" - {

    "when data has been imported" - {

      "and records match the selector used" - {

        "including some from the current import" - {

          "must return the records from the current import" in {

            val list = Gen.oneOf(ReferenceDataList.values.toList).sample.value

            val import1 = DataImport(ImportId(1), list, 1, ImportStatus.Complete, Instant.now, Some(Instant.now))
            val import2 = DataImport(ImportId(2), list, 1, ImportStatus.Complete, Instant.now, Some(Instant.now))
            val import1Data = Seq(Json.obj("id" -> "1", "value" -> "import 1 value"), Json.obj("id" -> "2", "value" -> "import 1 value"))
            val import2Data = Seq(Json.obj("id" -> "1", "value" -> "import 2 value"), Json.obj("id" -> "2", "value" -> "import 1 value"))

            val app = new GuiceApplicationBuilder().build()

            running(app) {

              val dataImportRepo = app.injector.instanceOf[DataImportRepository]
              val listRepo       = app.injector.instanceOf[ListRepository]
              val service        = app.injector.instanceOf[CurrentReferenceDataService]

              dataImportRepo.insert(import1).futureValue
              dataImportRepo.insert(import2).futureValue
              listRepo.insert(list, ImportId(1), import1Data).futureValue
              listRepo.insert(list, ImportId(2), import2Data).futureValue

              val result = service.many(list, Selector.All()).futureValue

              val expectedResult = import2Data.map(json => json ++ Json.obj("importId" -> 2))

              result.map(_ - "_id") must equal(expectedResult)
            }
          }
        }

        "but not from the current import" - {

          "must return None" in {

            val list = Gen.oneOf(ReferenceDataList.values.toList).sample.value

            val import1 = DataImport(ImportId(1), list, 1, ImportStatus.Complete, Instant.now, Some(Instant.now))
            val import2 = DataImport(ImportId(2), list, 1, ImportStatus.Complete, Instant.now, Some(Instant.now))
            val import1Data = Seq(Json.obj("id" -> "1", "value" -> "import 1 value"), Json.obj("id" -> "2", "value" -> "import 1 value"))

            val app = new GuiceApplicationBuilder().build()

            running(app) {

              val dataImportRepo = app.injector.instanceOf[DataImportRepository]
              val listRepo       = app.injector.instanceOf[ListRepository]
              val service        = app.injector.instanceOf[CurrentReferenceDataService]

              dataImportRepo.insert(import1).futureValue
              dataImportRepo.insert(import2).futureValue
              listRepo.insert(list, ImportId(1), import1Data).futureValue

              val result = service.many(list, Selector.ById("1")).futureValue

              result mustBe empty
            }
          }
        }
      }

      "and no records match the selector used" - {

        "must return None" in {

          val list = Gen.oneOf(ReferenceDataList.values.toList).sample.value

          val import1 = DataImport(ImportId(1), list, 1, ImportStatus.Complete, Instant.now, Some(Instant.now))
          val import1Data = Seq(Json.obj("id" -> "1", "value" -> "import 1 value"), Json.obj("id" -> "2", "value" -> "import 1 value"))

          val app = new GuiceApplicationBuilder().build()

          running(app) {

            val dataImportRepo = app.injector.instanceOf[DataImportRepository]
            val listRepo       = app.injector.instanceOf[ListRepository]
            val service        = app.injector.instanceOf[CurrentReferenceDataService]

            dataImportRepo.insert(import1).futureValue
            listRepo.insert(list, ImportId(1), import1Data).futureValue

            val result = service.many(list, Selector.ById("3")).futureValue

            result mustBe empty
          }
        }
      }
    }

    "when no data has been imported" - {

      "must return None" in {

        val list = Gen.oneOf(ReferenceDataList.values.toList).sample.value

        val app = new GuiceApplicationBuilder().build()

        running(app) {

          val service = app.injector.instanceOf[CurrentReferenceDataService]

          val result = service.many(list, Selector.All()).futureValue

          result mustBe empty
        }
      }
    }
  }
}
