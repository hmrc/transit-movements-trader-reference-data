/*
 * Copyright 2019 HM Revenue & Customs
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

package controllers

import base.SpecBase
import models.{CountryCode, CustomsOffice}
import org.scalatest.MustMatchers
import org.scalatestplus.mockito.MockitoSugar
import org.mockito.Mockito.when
import play.api.test.FakeRequest
import play.api.inject.bind
import play.api.libs.json.Json
import play.api.test.Helpers.{route, status, _}
import services._

class ReferenceDataControllerSpec
    extends SpecBase
    with MustMatchers
    with MockitoSugar {

  private val customsOffices = Seq(
    CustomsOffice(
      "GB000001",
      "Central Community Transit Office",
      List("TRA", "DEP", "DES")
    )
  )
  private val countryCodes = Seq(CountryCode("valid", "GB", "United Kingdom"))

  private val countryCodesService = mock[CountryCodesService]
  private val customsOfficesService = mock[CustomsOfficesService]
  private val transitCountryCodesService = mock[TransitCountryCodesService]

  when(customsOfficesService.customsOffices).thenReturn(customsOffices)
  when(countryCodesService.countryCodes).thenReturn(countryCodes)
  when(transitCountryCodesService.transitCountryCodes).thenReturn(countryCodes)

  "ReferenceDataController" - {
    "must fetch customs offices" in {

      lazy val app = applicationBuilder()
        .overrides(
          bind[CountryCodesService].toInstance(countryCodesService),
          bind[CustomsOfficesService].toInstance(customsOfficesService),
          bind[TransitCountryCodesService].toInstance(transitCountryCodesService)
        )
        .build()

      val request =
        FakeRequest(GET, routes.ReferenceDataController.customsOffices().url)
      val result = route(app, request).value

      status(result) mustBe OK
      contentAsJson(result) mustBe Json.toJson(customsOffices)
      app.stop()
    }

    "must fetch country code full list" in {

      lazy val app = applicationBuilder()
        .overrides(
          bind[CountryCodesService].toInstance(countryCodesService),
          bind[CustomsOfficesService].toInstance(customsOfficesService),
          bind[TransitCountryCodesService].toInstance(transitCountryCodesService)
        )
        .build()

      val request = FakeRequest(
        GET,
        routes.ReferenceDataController.countryCodeFullList().url
      )
      val result = route(app, request).value

      status(result) mustBe OK
      contentAsJson(result) mustBe Json.toJson(countryCodes)
      app.stop()
    }

    "must fetch transit country codes" in {

      lazy val app = applicationBuilder()
        .overrides(
          bind[CountryCodesService].toInstance(countryCodesService),
          bind[CustomsOfficesService].toInstance(customsOfficesService),
          bind[TransitCountryCodesService].toInstance(transitCountryCodesService)
        )
        .build()

      val request = FakeRequest(
        GET,
        routes.ReferenceDataController.transitCountryCodeList().url
      )
      val result = route(app, request).value

      status(result) mustBe OK
      contentAsJson(result) mustBe Json.toJson(countryCodes)
      app.stop()
    }
  }
}
