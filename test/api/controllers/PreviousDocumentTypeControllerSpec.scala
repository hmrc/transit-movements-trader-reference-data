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

import api.models.PreviousDocumentType
import api.services.PreviousDocumentTypeService
import base.SpecBaseWithAppPerSuite
import org.mockito.ArgumentMatchers._
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

class PreviousDocumentTypeControllerSpec extends SpecBaseWithAppPerSuite {

  private def previousDocumentType = PreviousDocumentType("T1", "T1")

  private val previousDocumentTypes = Seq(previousDocumentType)

  val mockService: PreviousDocumentTypeService = mock[PreviousDocumentTypeService]

  override val mocks: Seq[_] = super.mocks ++ Seq(mockService)

  override def guiceApplicationBuilder: GuiceApplicationBuilder =
    super.guiceApplicationBuilder
      .overrides(
        bind[PreviousDocumentTypeService].toInstance(mockService)
      )

  "TransportModeController" - {
    "must fetch all transport modes" in {

      when(mockService.previousDocumentTypes).thenReturn(previousDocumentTypes)

      val request = FakeRequest(
        GET,
        routes.PreviousDocumentTypeController.previousDocumentTypes().url
      )
      val result = route(app, request).value

      status(result) mustBe OK
      contentAsJson(result) mustBe Json.toJson(previousDocumentTypes)
    }
    "getPreviousDocumentType" - {
      "must get transport mode and return Ok" in {

        when(mockService.getPreviousDocumentTypeByCode(any())).thenReturn(Some(previousDocumentType))

        val code = "T1"

        val request = FakeRequest(
          GET,
          routes.PreviousDocumentTypeController.getPreviousDocumentType(code).url
        )
        val result = route(app, request).value

        status(result) mustBe OK
        contentAsJson(result) mustBe Json.toJson(previousDocumentType)
      }

      "must return NotFound when no transport mode is found" in {

        when(mockService.getPreviousDocumentTypeByCode(any())).thenReturn(None)

        val invalidCode = "Invalid"

        val request = FakeRequest(
          GET,
          routes.PreviousDocumentTypeController.getPreviousDocumentType(invalidCode).url
        )
        val result = route(app, request).value

        status(result) mustBe NOT_FOUND
      }
    }
  }

}
