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

package repositories

import org.scalatest.OptionValues
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers
import uk.gov.hmrc.mongo.test.DefaultPlayMongoRepositorySupport

import scala.concurrent.ExecutionContext.Implicits.global

class ImportIdRepositorySpec extends AnyFreeSpec with Matchers with ScalaFutures with OptionValues with DefaultPlayMongoRepositorySupport[ImportId] {

  override protected def repository: ImportIdRepository = new ImportIdRepository(mongoComponent)

  "Import Id Repository" - {

    "must return sequential ids starting at 1" in {
      val first  = repository.nextId.futureValue
      val second = repository.nextId.futureValue

      first mustEqual ImportId(1)
      second mustEqual ImportId(2)
    }

    "must not fail if the collection already has a document on startup" in {
      repository.collection.insertOne(ImportId(123)).toFuture().futureValue

      repository.nextId.futureValue mustEqual ImportId(124)
    }
  }
}
