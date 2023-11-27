/*
 * Copyright 2023 HM Revenue & Customs
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

package controllers.testOnly.services

import base.SpecBaseWithAppPerSuite
import controllers.testOnly.testmodels.Country
import models.requests.CountryMembership.{CtcMember, EuMember, NonEuMember}
import models.requests.CountryQueryFilter

class CountryServiceSpec extends SpecBaseWithAppPerSuite {

  private val service = app.injector.instanceOf[CountryService]

  "getCountryByCode" - {

    "returns a country for a valid code" in {
      val expected = Some(Country("valid", "GB", "United Kingdom"))
      service.getCountryByCode("GB") mustBe expected
    }

    "returns  none for an invalid code" in {
      service.getCountryByCode("ZE") mustBe None
    }
  }

  "filterCountries" - {

    val expectedIds = Seq("AD", "AR", "AU", "FR", "GB", "IT", "AT", "SM", "AE")

    "ctc member" - {
      "returns the list of countries" in {
        val filter = CountryQueryFilter(None, Seq.empty, Some(CtcMember))
        val result = service.filterCountries(filter)

        result.length mustBe expectedIds.length
        expectedIds.map(result.toString.contains(_) mustBe true)
      }
    }

    "EU member" - {
      "returns the list of countries" in {
        val filter = CountryQueryFilter(None, Seq.empty, Some(EuMember))
        val result = service.filterCountries(filter)

        result.length mustBe expectedIds.length
        expectedIds.map(result.toString.contains(_) mustBe true)
      }
    }

    "Non EU member" - {
      "returns the list of countries" in {
        val filter = CountryQueryFilter(None, Seq.empty, Some(NonEuMember))
        val result = service.filterCountries(filter)

        result.length mustBe expectedIds.length
        expectedIds.map(result.toString.contains(_) mustBe true)
      }
    }
  }
}
