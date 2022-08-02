/*
 * Copyright 2022 HM Revenue & Customs
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

package services

import models.ReferenceDataList
import org.mongodb.scala.MongoClient
import org.scalacheck.Gen
import org.scalactic.Uniformity
import org.scalatest.concurrent.IntegrationPatience
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers
import org.scalatest.BeforeAndAfterEach
import org.scalatest.OptionValues
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.libs.json.JsObject
import play.api.libs.json.Json
import play.api.test.Helpers.running
import repositories.ListRepository.ListRepositoryProvider
import repositories._

import java.time.Instant
import scala.concurrent.ExecutionContext.Implicits.global

class ReferenceDataServiceSpec extends AnyFreeSpec with Matchers with BeforeAndAfterEach with ScalaFutures with IntegrationPatience with OptionValues {

  private def dropDatabases(): Unit = {
    val client = MongoClient()

    def dropDatabase(name: String): Unit =
      client
        .getDatabase(name)
        .drop()
        .toFuture()
        .map(
          _ => ()
        )
        .recover {
          case _: Throwable => ()
        }
        .futureValue

    client.listDatabaseNames().map(dropDatabase).toFuture().futureValue
  }

  override def beforeEach(): Unit = {
    dropDatabases()
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

            val import1     = DataImport(ImportId(1), list, 1, ImportStatus.Complete, Instant.now, Some(Instant.now))
            val import2     = DataImport(ImportId(2), list, 1, ImportStatus.Complete, Instant.now, Some(Instant.now))
            val import1Data = Seq(Json.obj("id" -> "1", "value" -> "import 1 value"))
            val import2Data = Seq(Json.obj("id" -> "1", "value" -> "import 2 value"))

            val app = new GuiceApplicationBuilder().build()

            running(app) {

              val dataImportRepo   = app.injector.instanceOf[DataImportRepository]
              val listRepoProvider = app.injector.instanceOf[ListRepositoryProvider]
              val listRepo         = listRepoProvider.apply(list)
              val service          = app.injector.instanceOf[ReferenceDataService]

              dataImportRepo.insert(import1).futureValue
              dataImportRepo.insert(import2).futureValue
              listRepo.insert(ImportId(1), import1Data).futureValue
              listRepo.insert(ImportId(2), import2Data).futureValue

              val result = service.one(list, Selector.ById("1")).futureValue

              val expectedResult = import2Data.head ++ Json.obj("importId" -> 2)

              result.value must equal(expectedResult)(after being madeMongoIdAgnostic)
            }
          }
        }

        "but not from the current import" - {

          "must return None" in {

            val list = Gen.oneOf(ReferenceDataList.values.toList).sample.value

            val import1     = DataImport(ImportId(1), list, 1, ImportStatus.Complete, Instant.now, Some(Instant.now))
            val import2     = DataImport(ImportId(2), list, 1, ImportStatus.Complete, Instant.now, Some(Instant.now))
            val import1Data = Seq(Json.obj("id" -> "1", "value" -> "import 1 value"))

            val app = new GuiceApplicationBuilder().build()

            running(app) {

              val dataImportRepo   = app.injector.instanceOf[DataImportRepository]
              val listRepoProvider = app.injector.instanceOf[ListRepositoryProvider]
              val listRepo         = listRepoProvider.apply(list)
              val service          = app.injector.instanceOf[ReferenceDataService]

              dataImportRepo.insert(import1).futureValue
              dataImportRepo.insert(import2).futureValue
              listRepo.insert(ImportId(1), import1Data).futureValue

              val result = service.one(list, Selector.ById("1")).futureValue

              result must not be defined
            }
          }
        }
      }

      "and no records match the selector used" - {

        "must return None" in {

          val list = Gen.oneOf(ReferenceDataList.values.toList).sample.value

          val import1     = DataImport(ImportId(1), list, 1, ImportStatus.Complete, Instant.now, Some(Instant.now))
          val import1Data = Seq(Json.obj("id" -> "1", "value" -> "import 1 value"))

          val app = new GuiceApplicationBuilder().build()

          running(app) {

            val dataImportRepo   = app.injector.instanceOf[DataImportRepository]
            val listRepoProvider = app.injector.instanceOf[ListRepositoryProvider]
            val listRepo         = listRepoProvider.apply(list)
            val service          = app.injector.instanceOf[ReferenceDataService]

            dataImportRepo.insert(import1).futureValue
            listRepo.insert(ImportId(1), import1Data).futureValue

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

          val service = app.injector.instanceOf[ReferenceDataService]

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

            val import1     = DataImport(ImportId(1), list, 1, ImportStatus.Complete, Instant.now, Some(Instant.now))
            val import2     = DataImport(ImportId(2), list, 1, ImportStatus.Complete, Instant.now, Some(Instant.now))
            val import1Data = Seq(Json.obj("id" -> "1", "value" -> "import 1 value"), Json.obj("id" -> "2", "value" -> "import 1 value"))
            val import2Data = Seq(Json.obj("id" -> "1", "value" -> "import 2 value"), Json.obj("id" -> "2", "value" -> "import 1 value"))

            val app = new GuiceApplicationBuilder().build()

            running(app) {

              val dataImportRepo   = app.injector.instanceOf[DataImportRepository]
              val listRepoProvider = app.injector.instanceOf[ListRepositoryProvider]
              val listRepo         = listRepoProvider.apply(list)
              val service          = app.injector.instanceOf[ReferenceDataService]

              dataImportRepo.insert(import1).futureValue
              dataImportRepo.insert(import2).futureValue
              listRepo.insert(ImportId(1), import1Data).futureValue
              listRepo.insert(ImportId(2), import2Data).futureValue

              val result = service.many(list, Selector.All()).futureValue

              val expectedResult = import2Data.map(
                json => json ++ Json.obj("importId" -> 2)
              )

              result.map(_ - "_id") must equal(expectedResult)
            }
          }
        }

        "but not from the current import" - {

          "must return None" in {

            val list = Gen.oneOf(ReferenceDataList.values.toList).sample.value

            val import1     = DataImport(ImportId(1), list, 1, ImportStatus.Complete, Instant.now, Some(Instant.now))
            val import2     = DataImport(ImportId(2), list, 1, ImportStatus.Complete, Instant.now, Some(Instant.now))
            val import1Data = Seq(Json.obj("id" -> "1", "value" -> "import 1 value"), Json.obj("id" -> "2", "value" -> "import 1 value"))

            val app = new GuiceApplicationBuilder().build()

            running(app) {

              val dataImportRepo   = app.injector.instanceOf[DataImportRepository]
              val listRepoProvider = app.injector.instanceOf[ListRepositoryProvider]
              val listRepo         = listRepoProvider.apply(list)
              val service          = app.injector.instanceOf[ReferenceDataService]

              dataImportRepo.insert(import1).futureValue
              dataImportRepo.insert(import2).futureValue
              listRepo.insert(ImportId(1), import1Data).futureValue

              val result = service.many(list, Selector.ById("1")).futureValue

              result mustBe empty
            }
          }
        }
      }

      "and no records match the selector used" - {

        "must return None" in {

          val list = Gen.oneOf(ReferenceDataList.values.toList).sample.value

          val import1     = DataImport(ImportId(1), list, 1, ImportStatus.Complete, Instant.now, Some(Instant.now))
          val import1Data = Seq(Json.obj("id" -> "1", "value" -> "import 1 value"), Json.obj("id" -> "2", "value" -> "import 1 value"))

          val app = new GuiceApplicationBuilder().build()

          running(app) {

            val dataImportRepo   = app.injector.instanceOf[DataImportRepository]
            val listRepoProvider = app.injector.instanceOf[ListRepositoryProvider]
            val listRepo         = listRepoProvider.apply(list)
            val service          = app.injector.instanceOf[ReferenceDataService]

            dataImportRepo.insert(import1).futureValue
            listRepo.insert(ImportId(1), import1Data).futureValue

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

          val service = app.injector.instanceOf[ReferenceDataService]

          val result = service.many(list, Selector.All()).futureValue

          result mustBe empty
        }
      }
    }
  }
}
