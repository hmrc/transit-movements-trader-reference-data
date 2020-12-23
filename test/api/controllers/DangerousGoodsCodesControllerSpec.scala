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

package api.controllers

import api.services.ReferenceDataService
import base.SpecBaseWithAppPerSuite
import org.mockito.ArgumentMatchers.any
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

import scala.concurrent.Future

class DangerousGoodsCodesControllerSpec extends SpecBaseWithAppPerSuite {
  private val mockReferenceDataService = mock[ReferenceDataService]

  override val mocks: Seq[_] = super.mocks ++ Seq(mockReferenceDataService)

  override def guiceApplicationBuilder: GuiceApplicationBuilder =
    super.guiceApplicationBuilder
      .overrides(
        bind[ReferenceDataService].toInstance(mockReferenceDataService)
      )

  "TransportModeController" - {
    "must fetch all transport modes" in {

      val data = Seq(Json.obj("key" -> "value"))
      when(mockReferenceDataService.many(any(), any())).thenReturn(Future.successful(data))

      val request = FakeRequest(
        GET,
        routes.DangerousGoodsCodesController.dangerousGoodsCodes().url
      )
      val result = route(app, request).value

      status(result) mustBe OK
      contentAsJson(result) mustBe Json.toJson(data)
    }

    "must return NotFound when there is no data" in {

      when(mockReferenceDataService.many(any(), any())).thenReturn(Future.successful(Nil))

      val request = FakeRequest(
        GET,
        routes.DangerousGoodsCodesController.dangerousGoodsCodes().url
      )
      val result = route(app, request).value

      status(result) mustBe NOT_FOUND
    }

    "getTransportMode" - {
      "must get transport mode and return Ok" in {

        val validCountryCode = "GB"
        val expected         = Json.obj("code" -> validCountryCode)
        when(mockReferenceDataService.one(any(), any())).thenReturn(Future.successful(Some(expected)))

        val request = FakeRequest(
          GET,
          routes.DangerousGoodsCodesController.getDangerousGoodsCode(validCountryCode).url
        )
        val result = route(app, request).value

        status(result) mustBe OK
        contentAsJson(result) mustBe Json.toJson(expected)
      }

      "must return NotFound when no transport mode is found" in {

        val validCountryCode = "GB"
        val expected         = Json.obj("code" -> validCountryCode)
        when(mockReferenceDataService.one(any(), any())).thenReturn(Future.successful(None))

        val invalidCode = "Invalid"

        val request = FakeRequest(
          GET,
          routes.DangerousGoodsCodesController.getDangerousGoodsCode(invalidCode).url
        )
        val result = route(app, request).value

        status(result) mustBe NOT_FOUND
      }
    }
  }

}
