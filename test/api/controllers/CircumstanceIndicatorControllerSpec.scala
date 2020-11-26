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
import base.SpecBase
import org.mockito.Matchers.any
import org.mockito.Mockito.reset
import org.mockito.Mockito.when
import org.scalatest.BeforeAndAfterEach
import org.scalatest.MustMatchers
import org.scalatestplus.mockito.MockitoSugar
import play.api.inject.bind
import play.api.libs.json.Json
import play.api.test.FakeRequest
import play.api.test.Helpers.GET
import play.api.test.Helpers.contentAsJson
import play.api.test.Helpers.route
import play.api.test.Helpers.status
import play.api.test.Helpers._

class CircumstanceIndicatorControllerSpec extends SpecBase with MustMatchers with MockitoSugar with BeforeAndAfterEach {
  private val circumstanceIndicator  = CircumstanceIndicator("E", "Authorised economic operators")
  private val circumstanceIndicators = Seq(circumstanceIndicator)

  val mockCircumstanceIndicatorService: CircumstanceIndicatorService = mock[CircumstanceIndicatorService]

  override def beforeEach(): Unit = {
    reset(mockCircumstanceIndicatorService)
    super.beforeEach()
  }

  "CircumstanceIndicatorController" - {
    "must fetch all circumstance indicators" in {

      when(mockCircumstanceIndicatorService.circumstanceIndicators).thenReturn(circumstanceIndicators)

      val app = applicationBuilder()
        .overrides(
          bind[CircumstanceIndicatorService].toInstance(mockCircumstanceIndicatorService)
        )
        .build()

      val request = FakeRequest(
        GET,
        routes.CircumstanceIndicatorController.circumstanceIndicators().url
      )
      val result = route(app, request).value

      status(result) mustBe OK
      contentAsJson(result) mustBe Json.toJson(circumstanceIndicators)
      app.stop()
    }

    "getCircumstanceIndicator" - {
      "must get circumstance indicator and return Ok" in {

        when(mockCircumstanceIndicatorService.getCircumstanceIndicator(any())).thenReturn(Some(circumstanceIndicator))

        val app = applicationBuilder()
          .overrides(
            bind[CircumstanceIndicatorService].toInstance(mockCircumstanceIndicatorService)
          )
          .build()

        val code = "E"

        val request = FakeRequest(
          GET,
          routes.CircumstanceIndicatorController.getCircumstanceIndicator(code).url
        )
        val result = route(app, request).value

        status(result) mustBe OK
        contentAsJson(result) mustBe Json.toJson(circumstanceIndicator)
        app.stop()
      }

      "must return NotFound when no circumstance indicator found" in {

        when(mockCircumstanceIndicatorService.getCircumstanceIndicator(any())).thenReturn(None)

        val app = applicationBuilder()
          .overrides(
            bind[CircumstanceIndicatorService].toInstance(mockCircumstanceIndicatorService)
          )
          .build()

        val invalidCode = "Invalid"

        val request = FakeRequest(
          GET,
          routes.CircumstanceIndicatorController.getCircumstanceIndicator(invalidCode).url
        )
        val result = route(app, request).value

        status(result) mustBe NOT_FOUND
        app.stop()
      }
    }
  }
}
