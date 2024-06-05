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

package controllers.consumption

import base.SpecBaseWithAppPerSuite
import models.RequestedDocumentTypeList
import org.mockito.ArgumentMatchers.{any, eq => eqTo}
import org.mockito.Mockito.when
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.libs.json.Json
import play.api.test.FakeRequest
import play.api.test.Helpers.{GET, contentAsJson, route, status, _}
import repositories.Selector
import services.ReferenceDataService

import scala.concurrent.Future

class RequestedDocumentTypeControllerMongoSpec extends SpecBaseWithAppPerSuite {
  private val mockReferenceDataService = mock[ReferenceDataService]

  override val mocks: Seq[_] = super.mocks ++ Seq(mockReferenceDataService)

  override def guiceApplicationBuilder: GuiceApplicationBuilder =
    super.guiceApplicationBuilder
      .overrides(
        bind[ReferenceDataService].toInstance(mockReferenceDataService)
      )

  "RequestedDocumentTypeController" - {
    "getAll" - {
      "must fetch all ControlResult data" in {

        val data = Seq(Json.obj("key" -> "value"))
        when(mockReferenceDataService.many(eqTo(RequestedDocumentTypeList), any(), any())).thenReturn(Future.successful(data))

        val request = FakeRequest(
          GET,
          routes.RequestedDocumentTypeController.getAll().url
        )
        val result = route(app, request).value

        status(result) mustBe OK
        contentAsJson(result) mustBe Json.toJson(data)
      }

      "returns a 404 when no data is present" in {

        when(mockReferenceDataService.many(eqTo(RequestedDocumentTypeList), any(), any())).thenReturn(Future.successful(Seq.empty))

        val request = FakeRequest(
          GET,
          routes.RequestedDocumentTypeController.getAll().url
        )
        val result = route(app, request).value

        status(result) mustBe NOT_FOUND
      }

    }
    "getRequestedDocumentType" - {
      "must fetch the RequestedDocumentType data by the code" in {

        val data = Some(Json.obj("key" -> "value"))
        val code = "value2"
        when(mockReferenceDataService.one(eqTo(RequestedDocumentTypeList), eqTo(Selector.ByCode(code)), any())).thenReturn(Future.successful(data))

        val request = FakeRequest(
          GET,
          routes.RequestedDocumentTypeController.getRequestedDocumentType(code).url
        )
        val result = route(app, request).value

        status(result) mustBe OK
        contentAsJson(result) mustBe data.get
      }

      "returns a 404 when no data is present" in {

        val code = "value2"
        when(mockReferenceDataService.one(eqTo(RequestedDocumentTypeList), eqTo(Selector.ByCode(code)), any())).thenReturn(Future.successful(None))

        val request = FakeRequest(
          GET,
          routes.RequestedDocumentTypeController.getRequestedDocumentType(code).url
        )
        val result = route(app, request).value

        status(result) mustBe NOT_FOUND
      }
    }
  }

}