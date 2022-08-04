/*
 * Copyright 2022 HM Revenue & Customs
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

package controllers.consumption

import models.{Country, CountryCodesCommonTransitList, CountryCodesCommonTransitListVersion2, CountryCodesCustomsOfficeLists, CountryCodesFullList}
import services.ReferenceDataService
import base.SpecBaseWithAppPerSuite
import org.mockito.ArgumentMatchers
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.when
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.libs.json.JsObject
import play.api.libs.json.Json
import play.api.test.FakeRequest
import play.api.test.Helpers.route
import play.api.test.Helpers.status
import play.api.test.Helpers._

import scala.concurrent.Future
import repositories.Selector

class CountryControllerMongoSpec extends SpecBaseWithAppPerSuite {

  private val ukCountry            = Country("valid", "GB", "United Kingdom")
  private val countries            = Seq(ukCountry)
  private val countriesAsJsObjects = Seq(Json.toJsObject(ukCountry))

  private val mockReferenceDataService = mock[ReferenceDataService]

  override val mocks: Seq[_] = super.mocks ++ Seq(mockReferenceDataService)

  override def guiceApplicationBuilder: GuiceApplicationBuilder =
    super.guiceApplicationBuilder
      .overrides(
        bind[ReferenceDataService].toInstance(mockReferenceDataService)
      )

  "CountryController" - {

    "get" - {
      "when the request doesn't have filters, then results from the repository returned" in {
        when(mockReferenceDataService.many(ArgumentMatchers.eq(CountryCodesFullList), ArgumentMatchers.eq(Selector.All()), any()))
          .thenReturn(Future.successful(countriesAsJsObjects))

        val request = FakeRequest(GET, "/transit-movements-trader-reference-data/countries")
        val result  = route(app, request).value

        status(result) mustBe OK
        contentAsJson(result) mustBe Json.toJson(countries)
      }

      "when the request filter for customs Offices with any role, then results from the repository returned" in {
        when(mockReferenceDataService.many(ArgumentMatchers.eq(CountryCodesCustomsOfficeLists), ArgumentMatchers.eq(Selector.All()), any()))
          .thenReturn(Future.successful(countriesAsJsObjects))

        val request = FakeRequest(GET, "/transit-movements-trader-reference-data/countries?customsOfficeRole=ANY")
        val result  = route(app, request).value

        status(result) mustBe OK
        contentAsJson(result) mustBe Json.toJson(countries)

      }


      "when the request filter for customs office with any role and excluded countries, then results from the repository returned" in {
        when(
          mockReferenceDataService.many(
            ArgumentMatchers.eq(CountryCodesCustomsOfficeLists),
            ArgumentMatchers.eq(Selector.ExcludeCountriesCodes(Seq("AA", "BB"))),
            any()
          )
        )
          .thenReturn(Future.successful(countriesAsJsObjects))

        val request = FakeRequest(GET, "/transit-movements-trader-reference-data/countries?customsOfficeRole=ANY&exclude=AA&exclude=BB")
        val result  = route(app, request).value

        status(result) mustBe OK
        contentAsJson(result) mustBe Json.toJson(countries)
      }

      "when the request filter for and excluded countries, then results from the repository returned" in {
        when(
          mockReferenceDataService.many(
            ArgumentMatchers.eq(CountryCodesFullList),
            ArgumentMatchers.eq(Selector.ExcludeCountriesCodes(Seq("AA", "BB"))),
            any()
          )
        )
          .thenReturn(Future.successful(countriesAsJsObjects))

        val request = FakeRequest(GET, "/transit-movements-trader-reference-data/countries?exclude=AA&exclude=BB")
        val result  = route(app, request).value

        status(result) mustBe OK
        contentAsJson(result) mustBe Json.toJson(countries)
      }

      "when the request filter for CTC Version1 and excluded countries, then results from the repository returned" in {
        when(
          mockReferenceDataService.many(
            ArgumentMatchers.eq(CountryCodesCommonTransitList),
            ArgumentMatchers.eq(Selector.ExcludeCountriesCodes(Seq("AA", "BB"))),
            any()
          )
        )
          .thenReturn(Future.successful(countriesAsJsObjects))

        val request = FakeRequest(GET, "/transit-movements-trader-reference-data/countries?exclude=AA&exclude=BB&membership=ctc")
        val result  = route(app, request).value

        status(result) mustBe OK
        contentAsJson(result) mustBe Json.toJson(countries)
      }

      "when the request filter for CTC Version2 and excluded countries, then results from the repository returned" in {
        val sampleAcceptHeader = "Accept" -> "application/vnd.hmrc.2.0+json"

        when(
          mockReferenceDataService.many(
            ArgumentMatchers.eq(CountryCodesCommonTransitListVersion2),
            ArgumentMatchers.eq(Selector.ExcludeCountriesCodes(Seq("AA", "BB"))),
            any()
          )
        )
          .thenReturn(Future.successful(countriesAsJsObjects))

        val request = FakeRequest(GET, "/transit-movements-trader-reference-data/countries?exclude=AA&exclude=BB&membership=ctc").withHeaders(sampleAcceptHeader)
        val result  = route(app, request).value

        status(result) mustBe OK
        contentAsJson(result) mustBe Json.toJson(countries)
      }

      "when the call to the repository returns an empty result set then a Not Found is returned" in {
        when(mockReferenceDataService.many(any(), any(), any()))
          .thenReturn(Future.successful(Seq.empty))

        val request = FakeRequest(GET, "/transit-movements-trader-reference-data/countries?customsOffice=true")
        val result  = route(app, request).value

        status(result) mustBe NOT_FOUND
      }

    }

    "getCountry" - {
      "must get correct country and return Ok" in {

        when(mockReferenceDataService.one(any(), any(), any())).thenReturn(Future.successful(Some(Json.toJson(ukCountry).as[JsObject])))

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

        when(mockReferenceDataService.one(any(), any(), any())).thenReturn(Future.successful(None))

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
