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

import api.models.Country
import api.services.ReferenceDataService
import base.SpecBaseWithAppPerSuite
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.when
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.libs.json.Json
import play.api.test.FakeRequest
import play.api.test.Helpers.route
import play.api.test.Helpers.status
import play.api.test.Helpers._

import scala.concurrent.Future

class TransitCountriesControllerMongoSpec extends SpecBaseWithAppPerSuite {

  private val ukCountry            = Country("valid", "GB", "United Kingdom")
  private val otherCountry         = Country("active", "AA", "An AA")
  private val anotherCountry       = Country("active", "BB", "A BB")
  private val countries            = Seq(ukCountry, otherCountry, anotherCountry)
  private val countriesAsJsObjects = countries.map(x => Json.toJsObject(x))

  private val mockReferenceDataService = mock[ReferenceDataService]

  override val mocks: Seq[_] = super.mocks ++ Seq(mockReferenceDataService)

  override def guiceApplicationBuilder: GuiceApplicationBuilder =
    super.guiceApplicationBuilder
      .overrides(
        bind[TransitCountriesController].to[TransitCountriesControllerMongo],
        bind[ReferenceDataService].toInstance(mockReferenceDataService)
      )

  "transitCountries" - {
    "must return Ok when there are transit countries" in {

      when(mockReferenceDataService.many(any(), any())).thenReturn(Future.successful(countriesAsJsObjects))

      val request = FakeRequest(
        GET,
        routes.TransitCountriesController.transitCountries().url
      )
      val result = route(app, request).value

      status(result) mustBe OK
      contentAsJson(result) mustBe Json.toJson(countries)
    }

    "must return Ok when there are transit countries filtering out the excluded country" in {

      when(mockReferenceDataService.many(any(), any())).thenReturn(Future.successful(countriesAsJsObjects))

      val request = FakeRequest(
        GET,
        "/transit-movements-trader-reference-data/transit-countries?excludeCountries=GB"
      )
      val result = route(app, request).value

      status(result) mustBe OK
      contentAsJson(result) mustBe Json.toJson(Seq(otherCountry, anotherCountry))
    }

    "must return Ok when there are transit countries filtering out multiple excluded country" in {

      when(mockReferenceDataService.many(any(), any())).thenReturn(Future.successful(countriesAsJsObjects))

      val request = FakeRequest(
        GET,
        "/transit-movements-trader-reference-data/transit-countries?excludeCountries=GB&excludeCountries=aa"
      )
      val result = route(app, request).value

      status(result) mustBe OK
      contentAsJson(result) mustBe Json.toJson(Seq(anotherCountry))
    }

    "must return Not Found when the transit countries cannot be retrieved" in {

      when(mockReferenceDataService.many(any(), any())).thenReturn(Future.successful(Nil))

      val request = FakeRequest(
        GET,
        routes.TransitCountriesController.transitCountries().url
      )
      val result = route(app, request).value

      status(result) mustBe NOT_FOUND
    }

    "must return Not Found when the transit countries exists but all are filtered out" in {

      when(mockReferenceDataService.many(any(), any())).thenReturn(Future.successful(countriesAsJsObjects))

      val request = FakeRequest(
        GET,
        "/transit-movements-trader-reference-data/transit-countries?excludeCountries=GB&excludeCountries=aa&excludeCountries=bb"
      )
      val result = route(app, request).value

      status(result) mustBe NOT_FOUND
    }
  }

  "nonEUTransitCountries" - {
    "must return Ok when there are non eu transit countries" in {

      when(mockReferenceDataService.many(any(), any())).thenReturn(Future.successful(countriesAsJsObjects))

      val request = FakeRequest(
        GET,
        routes.TransitCountriesController.nonEUTransitCountries().url
      )
      val result = route(app, request).value

      status(result) mustBe OK
      contentAsJson(result) mustBe Json.toJson(countries)
    }

    "must return Ok when there are non eu transit countries filtering out the excluded country" in {

      when(mockReferenceDataService.many(any(), any())).thenReturn(Future.successful(countriesAsJsObjects))

      val request = FakeRequest(
        GET,
        "/transit-movements-trader-reference-data/non-eu-transit-countries?excludeCountries=GB"
      )
      val result = route(app, request).value

      status(result) mustBe OK
      contentAsJson(result) mustBe Json.toJson(Seq(otherCountry, anotherCountry))
    }

    "must return Ok when there are non eu transit countries filtering out multiple excluded country" in {

      when(mockReferenceDataService.many(any(), any())).thenReturn(Future.successful(countriesAsJsObjects))

      val request = FakeRequest(
        GET,
        "/transit-movements-trader-reference-data/non-eu-transit-countries?excludeCountries=GB&excludeCountries=aa"
      )
      val result = route(app, request).value

      status(result) mustBe OK
      contentAsJson(result) mustBe Json.toJson(Seq(anotherCountry))
    }

    "must return Not Found when the non eu transit countries cannot be retrieved" in {

      when(mockReferenceDataService.many(any(), any())).thenReturn(Future.successful(Nil))

      val request = FakeRequest(
        GET,
        routes.TransitCountriesController.nonEUTransitCountries().url
      )
      val result = route(app, request).value

      status(result) mustBe NOT_FOUND
    }

    "must return Not Found when the non eu transit countries exists but all are filtered out" in {

      when(mockReferenceDataService.many(any(), any())).thenReturn(Future.successful(countriesAsJsObjects))

      val request = FakeRequest(
        GET,
        "/transit-movements-trader-reference-data/non-eu-transit-countries?excludeCountries=GB&excludeCountries=aa&excludeCountries=bb"
      )
      val result = route(app, request).value

      status(result) mustBe NOT_FOUND
    }
  }
}
