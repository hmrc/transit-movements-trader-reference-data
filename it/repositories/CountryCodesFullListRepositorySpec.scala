/*
 * Copyright 2023 HM Revenue & Customs
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

package repositories

import models.CountryCodesFullList
import org.mongodb.scala.model.Projections
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers
import org.scalatest.BeforeAndAfterEach
import org.scalatest.OptionValues
import play.api.libs.json.JsObject
import play.api.libs.json.Json
import repositories.ListRepository.ListRepositoryProvider
import uk.gov.hmrc.mongo.test.DefaultPlayMongoRepositorySupport

import scala.concurrent.ExecutionContext.Implicits.global

class CountryCodesFullListRepositorySpec
    extends AnyFreeSpec
    with Matchers
    with BeforeAndAfterEach
    with ScalaFutures
    with OptionValues
    with DefaultPlayMongoRepositorySupport[JsObject] {

  override protected def repository: ListRepository = new ListRepositoryProvider(mongoComponent).apply(CountryCodesFullList)

  ".insert" - {

    "must insert records, adding the import id to them" in {

      val importId = ImportId(1)
      val data     = Seq(Json.obj("code" -> "GB"), Json.obj("code" -> "FR"))

      val result = repository.insert(importId, data).futureValue

      result mustEqual true

      val databaseRecords = repository.collection
        .find()
        .projection(Projections.excludeId())
        .toFuture()
        .futureValue

      val expectedRecords = List(
        Json.obj("code" -> "GB", "importId" -> 1),
        Json.obj("code" -> "FR", "importId" -> 1)
      )

      databaseRecords must contain theSameElementsAs expectedRecords
    }
  }

  ".many" - {

    "must get all records that match a given selector" in {

      val data = Seq(Json.obj("code" -> "GB"), Json.obj("code" -> "FR"))

      repository.insert(ImportId(1), data).futureValue

      val results = repository
        .many(Selector.All())
        .futureValue
        .map(
          jsObject => jsObject - "_id"
        )

      val expectedResults = data.map(
        jsObject => jsObject ++ Json.obj("importId" -> 1)
      )

      results must contain theSameElementsAs expectedResults
    }
  }

  ".one" - {

    "must get a record if one exists that matches the selector" in {

      val data = Seq(Json.obj("code" -> "GB"), Json.obj("code" -> "FR"))

      repository.insert(ImportId(1), data).futureValue

      val result = repository
        .one(Selector.ByCode("GB"))
        .futureValue
        .map(
          jsObject => jsObject - "_id"
        )
        .value

      result mustEqual Json.obj("code" -> "GB", "importId" -> 1)
    }

    "must return None if no record that matches the selector exists" in {

      val data = Seq(Json.obj("code" -> "GB"), Json.obj("code" -> "FR"))

      repository.insert(ImportId(1), data).futureValue

      val result = repository
        .one(Selector.ByCode("IT"))
        .futureValue

      result must not be defined
    }
  }

  ".deleteOldImports" - {

    "must delete records with an importId less than the id specified" in {

      val record = Json.obj("id" -> 1)
      val data   = Seq(record)

      repository.insert(ImportId(1), data).futureValue
      repository.insert(ImportId(2), data).futureValue
      repository.insert(ImportId(3), data).futureValue
      repository.insert(ImportId(4), data).futureValue

      val result = repository.deleteOldImports(ImportId(3)).futureValue

      result mustEqual true

      val import1Records = repository.many(Selector.All().forImport(ImportId(1))).futureValue
      val import2Records = repository.many(Selector.All().forImport(ImportId(2))).futureValue
      val import3Records = repository.many(Selector.All().forImport(ImportId(3))).futureValue
      val import4Records = repository.many(Selector.All().forImport(ImportId(4))).futureValue

      import1Records mustBe empty
      import2Records mustBe empty
      import3Records.map(_ - "_id") must contain theSameElementsAs Seq(record ++ Json.obj("importId" -> 3))
      import4Records.map(_ - "_id") must contain theSameElementsAs Seq(record ++ Json.obj("importId" -> 4))
    }
  }
}
