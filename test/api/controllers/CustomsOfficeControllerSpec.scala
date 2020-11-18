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

import api.models.CustomsOffice
import base.SpecBase
import org.mockito.Mockito.reset
import org.mockito.Mockito.when
import org.mockito.Matchers.any
import org.mockito.Matchers.{eq => eqTo}
import org.scalatest.BeforeAndAfterAll
import org.scalatest.BeforeAndAfterEach
import org.scalatest.MustMatchers
import org.scalatestplus.mockito.MockitoSugar
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.libs.json.Json
import play.api.mvc.Result
import play.api.test.FakeRequest
import play.api.test.Helpers._
import api.services.CustomsOfficesService

import scala.concurrent.Future

class CustomsOfficeControllerSpec extends SpecBase with MustMatchers with MockitoSugar with BeforeAndAfterEach with BeforeAndAfterAll {

  private val customsOfficeId = "GB000001"
  private val customsOffices = Seq(
    CustomsOffice(
      customsOfficeId,
      "Central Community Transit Office",
      "GB",
      Some("+44 (0)3000 999 982"),
      List("TRA", "DEP", "DES")
    ),
    CustomsOffice(
      "GB000002",
      "Central Community Transit Office",
      "GB",
      None,
      List("TRA", "DEP", "DES")
    )
  )

  private val invalidId = "123"

  private val mockCustomsOfficesService = mock[CustomsOfficesService]

  private def application: GuiceApplicationBuilder =
    applicationBuilder()
      .overrides(
        bind[CustomsOfficesService].toInstance(mockCustomsOfficesService)
      )

  "CustomsOfficeController" - {

    "must return Ok and fetch some customs office" in {

      when(mockCustomsOfficesService.getCustomsOffice(eqTo(customsOfficeId))).thenReturn(Some(customsOffices.head))

      lazy val app = application.build()

      val customsOffice = customsOffices.head

      val request = FakeRequest(GET, routes.CustomsOfficeController.getCustomsOffice(customsOfficeId).url)

      val result: Future[Result] = route(app, request).value

      status(result) mustEqual OK

      contentAsJson(result) mustBe Json.toJson(customsOffice)

      app.stop()
    }

    "must return NotFound when no customs office is found" in {
      when(mockCustomsOfficesService.getCustomsOffice(any())).thenReturn(None)

      lazy val app = application.build()

      val request = FakeRequest(GET, routes.CustomsOfficeController.getCustomsOffice(invalidId).url)
      val result  = route(app, request).value

      status(result) mustBe NOT_FOUND
    }

    "must fetch customs offices" in {
      when(mockCustomsOfficesService.customsOffices).thenReturn(customsOffices)

      lazy val app = application.build()

      val request =
        FakeRequest(GET, routes.CustomsOfficeController.customsOffices().url)
      val result = route(app, request).value

      status(result) mustBe OK
      contentAsJson(result) mustBe Json.toJson(customsOffices)
    }

    "must return customs offices of the input country" in {

      when(mockCustomsOfficesService.getCustomsOfficesOfTheCountry(any())).thenReturn(customsOffices)

      lazy val app = application.build()

      val request = FakeRequest(GET, routes.CustomsOfficeController.customsOfficesOfTheCountry(customsOfficeId).url)

      val result: Future[Result] = route(app, request).value

      status(result) mustEqual OK

      contentAsJson(result) mustBe Json.toJson(customsOffices)

      app.stop()
    }
  }

  override protected def afterEach(): Unit = {
    super.afterEach()
    reset(mockCustomsOfficesService)
  }
}
