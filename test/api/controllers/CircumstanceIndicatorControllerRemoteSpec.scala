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

import base.SpecBaseWithAppPerSuite
import data.DataRetrieval
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.when
import org.scalatestplus.mockito.MockitoSugar
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.libs.json.Json
import play.api.test.FakeRequest
import play.api.test.Helpers._

import scala.concurrent.Future

class CircumstanceIndicatorControllerRemoteSpec extends SpecBaseWithAppPerSuite with MockitoSugar {

  private val mockDataRetrieval = mock[DataRetrieval]

  override val mocks: Seq[_] = super.mocks ++ Seq(mockDataRetrieval)

  override def guiceApplicationBuilder: GuiceApplicationBuilder =
    super.guiceApplicationBuilder
      .overrides(
        bind[CircumstanceIndicatorController].to[CircumstanceIndicatorControllerRemote],
        bind[DataRetrieval].toInstance(mockDataRetrieval)
      )

  "CircumstanceIndicatorController" - {
    "must fetch all circumstance indicators" in {

      val data = Seq(Json.obj("key" -> "value"))
      when(mockDataRetrieval.getList(any())(any())).thenReturn(Future.successful(data))

      val request = FakeRequest(
        GET,
        routes.CircumstanceIndicatorController.circumstanceIndicators().url
      )
      val result = route(app, request).value

      status(result) mustBe OK
      contentAsJson(result) mustBe Json.toJson(data)
    }

    "getCircumstanceIndicator" - {
      "must get circumstance indicator and return Ok" in {
        val code = "E"

        val data = Seq(Json.obj("code" -> code))
        when(mockDataRetrieval.getList(any())(any())).thenReturn(Future.successful(data))

        val request = FakeRequest(
          GET,
          routes.CircumstanceIndicatorController.getCircumstanceIndicator(code).url
        )
        val result = route(app, request).value

        status(result) mustBe OK
        contentAsJson(result) mustBe Json.toJson(data.head)
      }

      "must return NotFound when no circumstance indicator found" in {

        when(mockDataRetrieval.getList(any())(any())).thenReturn(Future.successful(Seq.empty))

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
