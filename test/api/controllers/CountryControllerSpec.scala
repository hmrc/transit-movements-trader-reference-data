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

import api.models.Country
import api.services._
import base.SpecBaseWithAppPerSuite
import data.DataRetrieval
import org.mockito.Mockito.when
import org.mockito.ArgumentMatchers.any
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.libs.json.Json
import play.api.test.FakeRequest
import play.api.test.Helpers.route
import play.api.test.Helpers.status
import play.api.test.Helpers._
import org.mockito.Mockito._
import org.mockito.ArgumentMatchers._

import scala.concurrent.Future

class CountryControllerSpec extends SpecBaseWithAppPerSuite {

  private val ukCountry            = Country("valid", "GB", "United Kingdom")
  private val countries            = Seq(ukCountry)
  private val countriesAsJsObjects = Seq(Json.toJsObject(ukCountry))

  private val countryService        = mock[CountryService]
  private val transitCountryService = mock[TransitCountryService]
  private val mockDataRetrieval     = mock[DataRetrieval]

  override val mocks: Seq[_] = super.mocks ++ Seq(countryService, transitCountryService, mockDataRetrieval)

  override def guiceApplicationBuilder: GuiceApplicationBuilder =
    super.guiceApplicationBuilder
      .overrides(
        bind[CountryService].toInstance(countryService),
        bind[TransitCountryService].toInstance(transitCountryService),
        bind[DataRetrieval].toInstance(mockDataRetrieval)
      )

  "CountryController" - {

    "countriesFullList" - {
      "must fetch country full list" in {

        when(mockDataRetrieval.getList(any())(any())).thenReturn(Future.successful(countriesAsJsObjects))

        val request = FakeRequest(
          GET,
          routes.CountryController.countriesFullList().url
        )
        val result = route(app, request).value

        status(result) mustBe OK
        contentAsJson(result) mustBe Json.toJson(countries)
      }
    }

    "transitCountries" - {
      "must fetch transit countries" in {

        when(mockDataRetrieval.getList(any())(any())).thenReturn(Future.successful(countriesAsJsObjects))

        val request = FakeRequest(
          GET,
          routes.CountryController.transitCountries().url
        )
        val result = route(app, request).value

        status(result) mustBe OK
        contentAsJson(result) mustBe Json.toJson(countries)
      }
    }

    "getCountry" - {
      "must get correct country and return Ok" in {

        when(countryService.getCountryByCode(any())).thenReturn(Some(ukCountry))

        val validCountryCode = "GB"

        val request = FakeRequest(
          GET,
          routes.CountryController.getCountry(validCountryCode).url
        )
        val result = route(app, request).value

        status(result) mustBe OK
        contentAsJson(result) mustBe Json.toJson(ukCountry)
      }

      "must return NotFound when no country is found" in {

        when(countryService.getCountryByCode(any())).thenReturn(None)

        val invalidCountryCode = "Invalid"

        val request = FakeRequest(
          GET,
          routes.CountryController.getCountry(invalidCountryCode).url
        )
        val result = route(app, request).value

        status(result) mustBe NOT_FOUND
      }
    }
  }
}
