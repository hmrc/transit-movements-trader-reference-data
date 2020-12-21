/*
 * Copyright 2020 HM Revenue & Customs
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

package data.services

import java.time.Clock
import java.time.Instant
import java.time.ZoneId

import models.ReferenceDataList
import org.mockito.ArgumentMatchers.{eq => eqTo}
import org.mockito.Mockito.reset
import org.mockito.Mockito.when
import org.scalacheck.Gen
import org.scalatest.BeforeAndAfterEach
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers
import org.scalatestplus.mockito.MockitoSugar
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.libs.json.Json
import play.api.test.Helpers.running
import repositories.DataImport
import repositories.DataImportRepository
import repositories.ImportId
import repositories.ImportIdRepository
import repositories.ImportStatus
import repositories.ListCollectionIndexManager
import repositories.ListRepository

import scala.concurrent.Future

class DataImportServiceSpec extends AnyFreeSpec with Matchers with MockitoSugar with ScalaFutures with BeforeAndAfterEach with ScalaCheckPropertyChecks {

  private val instant: Instant = Instant.now

  private val stubClock: Clock               = Clock.fixed(instant, ZoneId.systemDefault)
  private val mockImportIdRepo               = mock[ImportIdRepository]
  private val mockDataImportRepo             = mock[DataImportRepository]
  private val mockListRepo                   = mock[ListRepository]
  private val mockListCollectionIndexManager = mock[ListCollectionIndexManager]

  private val appBuilder: GuiceApplicationBuilder =
    new GuiceApplicationBuilder()
      .overrides(
        bind[ImportIdRepository].toInstance(mockImportIdRepo),
        bind[DataImportRepository].toInstance(mockDataImportRepo),
        bind[ListRepository].toInstance(mockListRepo),
        bind[Clock].toInstance(stubClock),
        bind[ListCollectionIndexManager].toInstance(mockListCollectionIndexManager)
      )

  override def beforeEach(): Unit = {
    reset(mockImportIdRepo)
    reset(mockDataImportRepo)
    reset(mockListRepo)
    super.beforeEach()
  }

  ".importData" - {

    "must load data into the correct collection and record the result of the import" in {

      forAll(Gen.oneOf(ReferenceDataList.values.toList)) {
        list =>
          val importId          = ImportId(1)
          val referenceData     = Seq(Json.obj("id" -> 1))
          val initialDataImport = DataImport(importId, list, 1, ImportStatus.Started, Instant.now(stubClock))
          val finalDataImport   = DataImport(importId, list, 1, ImportStatus.Complete, Instant.now(stubClock), Some(Instant.now(stubClock)))

          when(mockImportIdRepo.nextId) thenReturn Future.successful(importId)
          when(mockDataImportRepo.insert(eqTo(initialDataImport))) thenReturn Future.successful(true)
          when(mockDataImportRepo.markFinished(eqTo(importId), eqTo(ImportStatus.Complete))) thenReturn Future.successful(finalDataImport)
          when(mockListRepo.insert(eqTo(list), eqTo(importId), eqTo(referenceData))) thenReturn Future.successful(true)

          val app = appBuilder.build()

          running(app) {

            val service = app.injector.instanceOf[DataImportService]

            val result = service.importData(list, referenceData).futureValue

            result mustEqual finalDataImport
          }
      }
    }

    "must mark an import as failed if there is a problem inserting the data" in {

      forAll(Gen.oneOf(ReferenceDataList.values.toList)) {
        list =>
          val importId          = ImportId(1)
          val referenceData     = Seq(Json.obj("id" -> 1))
          val initialDataImport = DataImport(importId, list, 1, ImportStatus.Started, Instant.now(stubClock))
          val finalDataImport   = DataImport(importId, list, 1, ImportStatus.Failed, Instant.now(stubClock), Some(Instant.now(stubClock)))

          when(mockImportIdRepo.nextId) thenReturn Future.successful(importId)
          when(mockDataImportRepo.insert(eqTo(initialDataImport))) thenReturn Future.successful(true)
          when(mockDataImportRepo.markFinished(eqTo(importId), eqTo(ImportStatus.Failed))) thenReturn Future.successful(finalDataImport)
          when(mockListRepo.insert(eqTo(list), eqTo(importId), eqTo(referenceData))) thenReturn Future.failed(new Exception("foo"))

          val app = appBuilder.build()

          running(app) {

            val service = app.injector.instanceOf[DataImportService]

            val result = service.importData(list, referenceData).futureValue

            result mustEqual finalDataImport
          }
      }
    }
  }
}
