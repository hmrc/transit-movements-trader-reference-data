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

import api.models.TransportMode
import api.models.Valid
import api.services.TransportModeService
import base.SpecBaseWithAppPerSuite
import org.mockito.Mockito.when
import org.mockito.ArgumentMatchers.any
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.libs.json.Json
import play.api.test.FakeRequest
import play.api.test.Helpers.GET
import play.api.test.Helpers.contentAsJson
import play.api.test.Helpers.route
import play.api.test.Helpers.status
import play.api.test.Helpers._

class TransportModeControllerSpec extends SpecBaseWithAppPerSuite {
  private val transportModes = Seq(transportMode)

  private def transportMode =
    TransportMode(Valid, "2015-07-01", "10", "Sea transport")

  val mockTransportModeService: TransportModeService = mock[TransportModeService]

  override val mocks: Seq[_] = super.mocks :+ mockTransportModeService

  override def guiceApplicationBuilder: GuiceApplicationBuilder =
    super.guiceApplicationBuilder
      .overrides(
        bind[TransportModeService].toInstance(mockTransportModeService)
      )

  "TransportModeController" - {
    "must fetch all transport modes" in {

      when(mockTransportModeService.transportModes).thenReturn(transportModes)

      val request = FakeRequest(
        GET,
        routes.TransportModeController.transportModes().url
      )
      val result = route(app, request).value

      status(result) mustBe OK
      contentAsJson(result) mustBe Json.toJson(transportModes)
    }
    "getTransportMode" - {
      "must get transport mode and return Ok" in {

        when(mockTransportModeService.getTransportModeByCode(any())).thenReturn(Some(transportMode))

        val validCountryCode = "GB"

        val request = FakeRequest(
          GET,
          routes.TransportModeController.getTransportMode(validCountryCode).url
        )
        val result = route(app, request).value

        status(result) mustBe OK
        contentAsJson(result) mustBe Json.toJson(transportMode)
      }

      "must return NotFound when no transport mode is found" in {

        when(mockTransportModeService.getTransportModeByCode(any())).thenReturn(None)

        val invalidCode = "Invalid"

        val request = FakeRequest(
          GET,
          routes.TransportModeController.getTransportMode(invalidCode).url
        )
        val result = route(app, request).value

        status(result) mustBe NOT_FOUND
      }
    }
  }

}
