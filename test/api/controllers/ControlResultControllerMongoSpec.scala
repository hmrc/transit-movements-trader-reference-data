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

package api.controllers

import api.services.ReferenceDataService
import base.SpecBaseWithAppPerSuite
import models.AdditionalInformationIdCommonList
import models.ControlResultList
import org.mockito.ArgumentMatchers.any
import org.mockito.ArgumentMatchers.{eq => eqTo}
import org.mockito.Mockito.when
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.libs.json.Json
import play.api.test.FakeRequest
import play.api.test.Helpers.GET
import play.api.test.Helpers.contentAsJson
import play.api.test.Helpers.route
import play.api.test.Helpers.status
import play.api.test.Helpers._
import repositories.Selector

import scala.concurrent.Future

class ControlResultControllerMongoSpec extends SpecBaseWithAppPerSuite {
  private val mockReferenceDataService = mock[ReferenceDataService]

  override val mocks: Seq[_] = super.mocks ++ Seq(mockReferenceDataService)

  override def guiceApplicationBuilder: GuiceApplicationBuilder =
    super.guiceApplicationBuilder
      .overrides(
        bind[ControlResultController].to[ControlResultControllerMongo],
        bind[ReferenceDataService].toInstance(mockReferenceDataService)
      )

  "ControlResultsController" - {
    "getAll" - {
      "must fetch all ControlResult data" in {

        val data = Seq(Json.obj("key" -> "value"))
        when(mockReferenceDataService.many(eqTo(ControlResultList), any(), any())).thenReturn(Future.successful(data))

        val request = FakeRequest(
          GET,
          routes.ControlResultController.getAll().url
        )
        val result = route(app, request).value

        status(result) mustBe OK
        contentAsJson(result) mustBe Json.toJson(data)
      }

      "returns a 404 when no data is present" in {

        when(mockReferenceDataService.many(eqTo(ControlResultList), any(), any())).thenReturn(Future.successful(Seq.empty))

        val request = FakeRequest(
          GET,
          routes.ControlResultController.getAll().url
        )
        val result = route(app, request).value

        status(result) mustBe NOT_FOUND
      }

    }
    "getControlResult" - {
      "must fetch the ControlResult data by the code" in {

        val data = Some(Json.obj("key" -> "value"))
        val code = "value2"
        when(mockReferenceDataService.one(eqTo(ControlResultList), eqTo(Selector.ByCode(code)), any())).thenReturn(Future.successful(data))

        val request = FakeRequest(
          GET,
          routes.ControlResultController.getControlResult(code).url
        )
        val result = route(app, request).value

        status(result) mustBe OK
        contentAsJson(result) mustBe data.get
      }

      "returns a 404 when no data is present" in {

        val code = "value2"
        when(mockReferenceDataService.one(eqTo(ControlResultList), eqTo(Selector.ByCode(code)), any())).thenReturn(Future.successful(None))

        val request = FakeRequest(
          GET,
          routes.ControlResultController.getControlResult(code).url
        )
        val result = route(app, request).value

        status(result) mustBe NOT_FOUND
      }
    }
  }

}
