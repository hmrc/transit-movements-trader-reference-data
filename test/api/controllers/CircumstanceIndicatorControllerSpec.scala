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

import api.models.CircumstanceIndicator
import api.services.CircumstanceIndicatorService
import base.SpecBaseWithAppPerSuite
import org.mockito.Mockito.when
import org.mockito.ArgumentMatchers.any
import org.scalatestplus.mockito.MockitoSugar
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.libs.json.Json
import play.api.test.FakeRequest
import play.api.test.Helpers._

class CircumstanceIndicatorControllerSpec extends SpecBaseWithAppPerSuite with MockitoSugar {
  private val circumstanceIndicator  = CircumstanceIndicator("E", "Authorised economic operators")
  private val circumstanceIndicators = Seq(circumstanceIndicator)

  val mockCircumstanceIndicatorService: CircumstanceIndicatorService = mock[CircumstanceIndicatorService]

  override val mocks: Seq[_] = super.mocks :+ mockCircumstanceIndicatorService

  override def guiceApplicationBuilder: GuiceApplicationBuilder =
    super.guiceApplicationBuilder.overrides(
      bind[CircumstanceIndicatorService].toInstance(mockCircumstanceIndicatorService)
    )

  "CircumstanceIndicatorController" - {
    "must fetch all circumstance indicators" in {

      when(mockCircumstanceIndicatorService.circumstanceIndicators).thenReturn(circumstanceIndicators)

      val request = FakeRequest(
        GET,
        routes.CircumstanceIndicatorController.circumstanceIndicators().url
      )
      val result = route(app, request).value

      status(result) mustBe OK
      contentAsJson(result) mustBe Json.toJson(circumstanceIndicators)
    }

    "getCircumstanceIndicator" - {
      "must get circumstance indicator and return Ok" in {

        when(mockCircumstanceIndicatorService.getCircumstanceIndicator(any())).thenReturn(Some(circumstanceIndicator))

        val code = "E"

        val request = FakeRequest(
          GET,
          routes.CircumstanceIndicatorController.getCircumstanceIndicator(code).url
        )
        val result = route(app, request).value

        status(result) mustBe OK
        contentAsJson(result) mustBe Json.toJson(circumstanceIndicator)
      }

      "must return NotFound when no circumstance indicator found" in {

        when(mockCircumstanceIndicatorService.getCircumstanceIndicator(any())).thenReturn(None)

        val invalidCode = "Invalid"

        val request = FakeRequest(
          GET,
          routes.CircumstanceIndicatorController.getCircumstanceIndicator(invalidCode).url
        )
        val result = route(app, request).value

        status(result) mustBe NOT_FOUND
      }
    }
  }
}
