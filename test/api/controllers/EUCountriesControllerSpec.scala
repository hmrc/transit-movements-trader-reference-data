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

class EUCountiesControllerSpec extends SpecBaseWithAppPerSuite {

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

  "EuCountriesController" - {

    "getEUCountries" - {
      "must fetch eu countries list" in {

        when(mockReferenceDataService.many(any(), any(), any())).thenReturn(Future.successful(countriesAsJsObjects))

        val request = FakeRequest(
          GET,
          routes.EuCountriesController.getEUCountries().url
        )
        val result = route(app, request).value

        status(result) mustBe OK
        contentAsJson(result) mustBe Json.toJson(countries)
      }

      "must return NotFound when there is no data" in {

        when(mockReferenceDataService.many(any(), any(), any())).thenReturn(Future.successful(Nil))

        val request = FakeRequest(
          GET,
          routes.EuCountriesController.getEUCountries().url
        )
        val result = route(app, request).value

        status(result) mustBe NOT_FOUND
      }
    }
  }
}
