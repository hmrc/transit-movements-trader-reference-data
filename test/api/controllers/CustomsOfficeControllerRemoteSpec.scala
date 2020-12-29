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
import base.SpecBaseWithAppPerSuite
import data.DataRetrieval
import org.mockito.Mockito.when
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.libs.json.JsObject
import play.api.libs.json.Json
import play.api.mvc.Result
import play.api.test.FakeRequest
import play.api.test.Helpers._
import org.mockito.ArgumentMatchers.any

import scala.concurrent.Future

class CustomsOfficeControllerRemoteSpec extends SpecBaseWithAppPerSuite {

  private val customsOfficeId = "GB000001"

  private val countryCode = "GB"

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

  private val customsOfficesJsObjects: Seq[JsObject] =
    customsOffices.map(Json.toJsObject(_))

  private val invalidId = "123"

  private val mockDataRetrieval = mock[DataRetrieval]

  override val mocks: Seq[_] = super.mocks ++ Seq(mockDataRetrieval)

  override def guiceApplicationBuilder: GuiceApplicationBuilder =
    super.guiceApplicationBuilder
      .overrides(
        bind[CustomsOfficeController].to[CustomsOfficeControllerRemote],
        bind[DataRetrieval].toInstance(mockDataRetrieval)
      )

  "CustomsOfficeController" - {

    "customsOffices" - {

      "must fetch customs offices" in {
        when(mockDataRetrieval.getList(any())(any())).thenReturn(Future.successful(customsOfficesJsObjects))

        val request =
          FakeRequest(GET, routes.CustomsOfficeController.customsOffices().url)
        val result = route(app, request).value

        status(result) mustBe OK
        contentAsJson(result) mustBe Json.toJson(customsOffices)
      }

      "must return Not Found when there are no customs offices" in {
        when(mockDataRetrieval.getList(any())(any())).thenReturn(Future.successful(Seq.empty))

        val request =
          FakeRequest(GET, routes.CustomsOfficeController.customsOffices().url)
        val result = route(app, request).value

        status(result) mustBe NOT_FOUND

      }

    }

    "customsOfficesOfTheCountry" - {

      "must return customs offices of the input country" in {

        when(mockDataRetrieval.getList(any())(any())).thenReturn(Future.successful(customsOfficesJsObjects))

        val request = FakeRequest(GET, routes.CustomsOfficeController.customsOfficesOfTheCountry(countryCode).url)

        val result: Future[Result] = route(app, request).value

        status(result) mustEqual OK

        contentAsJson(result) mustBe Json.toJson(customsOffices)
      }

      "must return Not Found when there are no customs offices for the country" in {
        when(mockDataRetrieval.getList(any())(any())).thenReturn(Future.successful(Seq.empty))

        val request = FakeRequest(GET, routes.CustomsOfficeController.customsOfficesOfTheCountry("TEST").url)

        val result: Future[Result] = route(app, request).value

        status(result) mustEqual NOT_FOUND

      }

      "must return BadRequest when the country code is not known" ignore {
        // TODO: Add this test when the data is saved in Mongo
      }

    }

    "getCustomsOffice" - {

      "must return Ok with a customs office when there is a matching customs office for the office id" in {

        when(mockDataRetrieval.getList(any())(any())).thenReturn(Future.successful(customsOfficesJsObjects))

        val customsOffice = customsOffices.head

        val request = FakeRequest(GET, routes.CustomsOfficeController.getCustomsOffice(customsOfficeId).url)

        val result: Future[Result] = route(app, request).value

        status(result) mustEqual OK

        contentAsJson(result) mustBe Json.toJson(customsOffice)

      }

      "must return NotFound when no matching customs office is found" in {
        when(mockDataRetrieval.getList(any())(any())).thenReturn(Future.successful(customsOfficesJsObjects))

        val request = FakeRequest(GET, routes.CustomsOfficeController.getCustomsOffice(invalidId).url)
        val result  = route(app, request).value

        status(result) mustBe NOT_FOUND
      }

    }

  }

}
