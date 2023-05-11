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

import models.ReferenceDataList
import org.scalacheck.Gen
import org.scalatest.OptionValues
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import uk.gov.hmrc.mongo.test.DefaultPlayMongoRepositorySupport

import java.time.temporal.ChronoUnit
import java.time.Clock
import java.time.Instant
import java.time.ZoneId
import scala.concurrent.ExecutionContext.Implicits.global

class DataImportRepositorySpec
    extends AnyFreeSpec
    with Matchers
    with ScalaFutures
    with OptionValues
    with GuiceOneAppPerSuite
    with DefaultPlayMongoRepositorySupport[DataImport] {

  private val stubClock: Clock = Clock.fixed(Instant.now, ZoneId.systemDefault)

  override protected val repository: DataImportRepository = new DataImportRepository(mongoComponent, stubClock)

  private val now = Instant.now(stubClock).truncatedTo(ChronoUnit.MILLIS)

  "Data Import Repository" - {

    "must insert a new data import and retrieve it" in {
      val list = Gen.oneOf(ReferenceDataList.values.toList).sample.value

      val dataImport = DataImport(ImportId(1), list, 1, ImportStatus.Started, now, None)

      val insertResult = repository.insert(dataImport).futureValue
      val getResult    = repository.get(dataImport.importId).futureValue

      insertResult mustEqual true
      getResult.value mustEqual dataImport
    }

    "must mark a record as finished" in {
      val list = Gen.oneOf(ReferenceDataList.values.toList).sample.value

      val dataImport = DataImport(ImportId(1), list, 1, ImportStatus.Started, now, None)

      repository.insert(dataImport).futureValue
      val updateResult = repository.markFinished(ImportId(1), ImportStatus.Complete).futureValue

      updateResult mustEqual DataImport(ImportId(1), list, 1, ImportStatus.Complete, now, Some(now))
    }
  }

  ".currentImportId" - {

    "must return the highest import Id that's in a Complete status" in {
      val list = Gen.oneOf(ReferenceDataList.values.toList).sample.value

      val import1 = DataImport(ImportId(1), list, 1, ImportStatus.Complete, now, Some(now))
      val import2 = DataImport(ImportId(2), list, 1, ImportStatus.Complete, now, Some(now))
      val import3 = DataImport(ImportId(3), list, 1, ImportStatus.Failed, now, Some(now))
      val import4 = DataImport(ImportId(4), list, 1, ImportStatus.Started, now, Some(now))

      repository.insert(import1).futureValue
      repository.insert(import2).futureValue
      repository.insert(import3).futureValue
      repository.insert(import4).futureValue

      val result = repository.currentImportId(list).futureValue

      result.value mustEqual ImportId(2)
    }
  }
}
