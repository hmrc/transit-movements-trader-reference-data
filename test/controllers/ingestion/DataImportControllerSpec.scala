/*
 * Copyright 2021 HM Revenue & Customs
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

package controllers.ingestion

import java.time.Instant

import base.SpecBaseWithAppPerSuite
import services.DataImportService
import models.ReferenceDataList
import org.mockito.ArgumentMatchers.any
import org.mockito.ArgumentMatchers.{eq => eqTo}
import org.mockito.Mockito.when
import org.scalacheck.Gen
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.libs.json.Json
import play.api.mvc.AnyContentAsJson
import play.api.test.FakeRequest
import play.api.test.Helpers._
import repositories.DataImport
import repositories.ImportId
import repositories.ImportStatus

import scala.concurrent.Future

class DataImportControllerSpec extends SpecBaseWithAppPerSuite {

  private val instant: Instant = Instant.now

  private val mockDataImportService = mock[DataImportService]

  override val mocks: Seq[_] = super.mocks ++ Seq(mockDataImportService)

  override def guiceApplicationBuilder: GuiceApplicationBuilder =
    super.guiceApplicationBuilder
      .overrides(bind[DataImportService].toInstance(mockDataImportService))

  ".post" - {

    "must import data and return Ok" in {

      val data       = Json.arr(Json.obj("id" -> 1))
      val list       = Gen.oneOf(ReferenceDataList.values.toList).sample.value
      val dataImport = DataImport(ImportId(1), list, 1, ImportStatus.Complete, instant, Some(instant))

      when(mockDataImportService.importData(eqTo(list), any())) thenReturn Future.successful(dataImport)

      val request: FakeRequest[AnyContentAsJson] =
        FakeRequest(POST, routes.DataImportController.post(list).url).withJsonBody(data)

      val result = route(app, request).value

      status(result) mustEqual OK
    }

    "must return Internal Server Error when something goes wrong saving the data" in {

      val data = Json.arr(Json.obj("id" -> 1))
      val list = Gen.oneOf(ReferenceDataList.values.toList).sample.value

      when(mockDataImportService.importData(eqTo(list), any())) thenReturn Future.failed(new Exception("foo"))

      val request: FakeRequest[AnyContentAsJson] =
        FakeRequest(POST, routes.DataImportController.post(list).url).withJsonBody(data)

      val result = route(app, request).value

      status(result) mustEqual INTERNAL_SERVER_ERROR
    }
  }
}
