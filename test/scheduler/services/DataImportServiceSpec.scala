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

package scheduler.services

import java.time.Clock
import java.time.Instant
import java.time.ZoneId

import data.DataRetrieval
import models._
import org.mockito.ArgumentMatchers.any
import org.mockito.ArgumentMatchers.{eq => eqTo}
import org.mockito.Mockito.reset
import org.mockito.Mockito.times
import org.mockito.Mockito.verify
import org.mockito.Mockito.when
import org.scalatest.BeforeAndAfterEach
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers
import org.scalatestplus.mockito.MockitoSugar
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.libs.json.Json
import play.api.test.Helpers.running
import repositories._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class DataImportServiceSpec extends AnyFreeSpec with Matchers with MockitoSugar with ScalaFutures with BeforeAndAfterEach {

  private val instant: Instant = Instant.now

  private val stubClock: Clock               = Clock.fixed(instant, ZoneId.systemDefault)
  private val mockImportIdRepo               = mock[ImportIdRepository]
  private val mockDataImportRepo             = mock[DataImportRepository]
  private val mockListRepo                   = mock[ListRepository]
  private val mockDataRetrieval              = mock[DataRetrieval]
  private val mockListCollectionIndexManager = mock[ListCollectionIndexManager]

  private val appBuilder: GuiceApplicationBuilder =
    new GuiceApplicationBuilder()
      .overrides(
        bind[ImportIdRepository].toInstance(mockImportIdRepo),
        bind[DataImportRepository].toInstance(mockDataImportRepo),
        bind[ListRepository].toInstance(mockListRepo),
        bind[DataRetrieval].toInstance(mockDataRetrieval),
        bind[Clock].toInstance(stubClock),
        bind[ListCollectionIndexManager].toInstance(mockListCollectionIndexManager)
      )

  override def beforeEach(): Unit = {
    reset(mockImportIdRepo)
    reset(mockDataImportRepo)
    reset(mockListRepo)
    reset(mockDataRetrieval)
    super.beforeEach()
  }

  ".importReferenceData" - {

    "must import all reference data" in {

      val importId          = ImportId(1)
      val referenceData     = Seq(Json.obj("id" -> 1))
      val initialDataImport = DataImport(importId, ImportStatus.Started, Instant.now(stubClock))
      val finalDataImport   = DataImport(importId, ImportStatus.Complete, Instant.now(stubClock), Some(Instant.now(stubClock)))

      when(mockImportIdRepo.nextId) thenReturn Future.successful(importId)
      when(mockDataImportRepo.insert(any())) thenReturn Future.successful(true)
      when(mockDataImportRepo.markFinished(any(), any())) thenReturn Future.successful(finalDataImport)
      when(mockListRepo.insert(any(), any(), any())) thenReturn Future.successful(true)
      when(mockDataRetrieval.getList(any())(any())) thenReturn Future.successful(referenceData)

      val app = appBuilder.build()

      running(app) {

        val service = app.injector.instanceOf[DataImportService]

        val result = service.importReferenceData().futureValue

        val expectedResult = DataImport(importId, ImportStatus.Complete, Instant.now(stubClock), Some(Instant.now(stubClock)))

        result mustEqual expectedResult
        verify(mockListRepo, times(1)).insert(eqTo(CountryCodesFullList), eqTo(importId), eqTo(referenceData))
        verify(mockListRepo, times(1)).insert(eqTo(CountryCodesCommonTransitList), eqTo(importId), eqTo(referenceData))
        verify(mockListRepo, times(1)).insert(eqTo(CustomsOfficesList), eqTo(importId), eqTo(referenceData))
        verify(mockListRepo, times(1)).insert(eqTo(DocumentTypeCommonList), eqTo(importId), eqTo(referenceData))
        verify(mockListRepo, times(1)).insert(eqTo(PreviousDocumentTypeCommonList), eqTo(importId), eqTo(referenceData))
        verify(mockListRepo, times(1)).insert(eqTo(KindOfPackagesList), eqTo(importId), eqTo(referenceData))
        verify(mockListRepo, times(1)).insert(eqTo(TransportModeList), eqTo(importId), eqTo(referenceData))
        verify(mockListRepo, times(1)).insert(eqTo(AdditionalInformationIdCommonList), eqTo(importId), eqTo(referenceData))
        verify(mockListRepo, times(1)).insert(eqTo(SpecificCircumstanceIndicatorList), eqTo(importId), eqTo(referenceData))
        verify(mockListRepo, times(1)).insert(eqTo(UnDangerousGoodsCodeList), eqTo(importId), eqTo(referenceData))
        verify(mockListRepo, times(1)).insert(eqTo(TransportChargesMethodOfPaymentList), eqTo(importId), eqTo(referenceData))
        verify(mockListRepo, times(1)).insert(eqTo(ControlResultList), eqTo(importId), eqTo(referenceData))
        verify(mockListRepo, times(12)).insert(any(), any(), any())
        verify(mockDataImportRepo, times(1)).insert(eqTo(initialDataImport))
        verify(mockDataImportRepo, times(1)).markFinished(importId, ImportStatus.Complete)
      }
    }

    "must mark an import as failed if any of the reference data imports fails" in {

      val importId          = ImportId(1)
      val referenceData     = Seq(Json.obj("id" -> 1))
      val initialDataImport = DataImport(importId, ImportStatus.Started, Instant.now(stubClock))
      val finalDataImport   = DataImport(importId, ImportStatus.Failed, Instant.now(stubClock), Some(Instant.now(stubClock)))

      when(mockImportIdRepo.nextId) thenReturn Future.successful(importId)
      when(mockDataImportRepo.insert(any())) thenReturn Future.successful(true)
      when(mockDataImportRepo.markFinished(any(), any())) thenReturn Future.successful(finalDataImport)
      when(mockListRepo.insert(any(), any(), any())) thenReturn Future.successful(true)
      when(mockListRepo.insert(eqTo(CountryCodesFullList), any(), any())) thenReturn Future.successful(false)
      when(mockDataRetrieval.getList(any())(any())) thenReturn Future.successful(referenceData)

      val app = appBuilder.build()

      running(app) {

        val service = app.injector.instanceOf[DataImportService]

        val result = service.importReferenceData().futureValue

        result mustEqual finalDataImport
        verify(mockDataImportRepo, times(1)).insert(eqTo(initialDataImport))
        verify(mockDataImportRepo, times(1)).markFinished(importId, ImportStatus.Failed)
      }
    }
  }
}
