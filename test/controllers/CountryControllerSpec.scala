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

package controllers

import base.SpecBase
import models.Country
import org.mockito.Matchers.any
import org.mockito.Mockito.when
import org.scalatest.MustMatchers
import org.scalatestplus.mockito.MockitoSugar
import play.api.inject.bind
import play.api.libs.json.Json
import play.api.test.FakeRequest
import play.api.test.Helpers.route
import play.api.test.Helpers.status
import play.api.test.Helpers._
import services._

class CountryControllerSpec extends SpecBase with MustMatchers with MockitoSugar {

  private val ukCountry = Country("valid", "GB", "United Kingdom")
  private val countries = Seq(ukCountry)

  private val countryService        = mock[CountryService]
  private val transitCountryService = mock[TransitCountryService]

  "CountryController" - {

    "countriesFullList" - {
      "must fetch country full list" in {

        when(countryService.countries).thenReturn(countries)

        val app = applicationBuilder()
          .overrides(
            bind[CountryService].toInstance(countryService)
          )
          .build()

        val request = FakeRequest(
          GET,
          routes.CountryController.countriesFullList().url
        )
        val result = route(app, request).value

        status(result) mustBe OK
        contentAsJson(result) mustBe Json.toJson(countries)
        app.stop()
      }
    }

    "transitCountries" - {
      "must fetch transit countries" in {

        when(transitCountryService.transitCountryCodes).thenReturn(countries)

        val app = applicationBuilder()
          .overrides(
            bind[TransitCountryService].toInstance(transitCountryService)
          )
          .build()

        val request = FakeRequest(
          GET,
          routes.CountryController.transitCountries().url
        )
        val result = route(app, request).value

        status(result) mustBe OK
        contentAsJson(result) mustBe Json.toJson(countries)
        app.stop()
      }
    }

    "getCountry" - {
      "must get correct country and return Ok" in {

        when(countryService.getCountryByCode(any())).thenReturn(Some(ukCountry))

        val app = applicationBuilder()
          .overrides(
            bind[CountryService].toInstance(countryService)
          )
          .build()

        val validCountryCode = "GB"

        val request = FakeRequest(
          GET,
          routes.CountryController.getCountry(validCountryCode).url
        )
        val result = route(app, request).value

        status(result) mustBe OK
        contentAsJson(result) mustBe Json.toJson(ukCountry)
        app.stop()
      }

      "must return NotFound when no country is found" in {

        when(countryService.getCountryByCode(any())).thenReturn(None)

        val app = applicationBuilder()
          .overrides(
            bind[CountryService].toInstance(countryService)
          )
          .build()

        val invalidCountryCode = "Invalid"

        val request = FakeRequest(
          GET,
          routes.CountryController.getCountry(invalidCountryCode).url
        )
        val result = route(app, request).value

        status(result) mustBe NOT_FOUND
        app.stop()
      }
    }
  }
}
