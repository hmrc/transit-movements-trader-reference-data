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

package controllers.testOnly.services

import base.SpecBaseWithAppPerSuite
import controllers.testOnly.helpers.{P4, P5}
import controllers.testOnly.testmodels.Country
import models.requests.CountryMembership.{CtcMember, EuMember, NonEuMember}
import models.requests.CountryQueryFilter

class CountryServiceSpec  extends SpecBaseWithAppPerSuite {

  "getCountryByCode" - {

    "returns a country for a valid code" in {

      val service = app.injector.instanceOf[CountryService]

      service.getCountryByCode("GB") mustBe Some(Country("valid", "GB", "United Kingdom"))

    }

    "returns  none for an invalid code" in {

      val service = app.injector.instanceOf[CountryService]

      service.getCountryByCode("ZE") mustBe None

    }

  }


    "filterCountries" - {

      "For P4" - {

        "ctc member" - {

          "returns the P4 list of countries" in {

            val service = app.injector.instanceOf[CountryService]
            val filter = CountryQueryFilter(None, Seq.empty, Some(CtcMember))

            service.filterCountries(filter, Some(P4)).length mustBe 3

          }

        }

        "EU member" - {

          "returns the P4 list of countries" in {

            val service = app.injector.instanceOf[CountryService]
            val filter = CountryQueryFilter(None, Seq.empty, Some(EuMember))

            service.filterCountries(filter, Some(P4)).length mustBe 3

          }

        }

        "Non EU member" - {

          "returns the P4 list of countries" in {

            val service = app.injector.instanceOf[CountryService]
            val filter = CountryQueryFilter(None, Seq.empty, Some(NonEuMember))

            service.filterCountries(filter, Some(P4)).length mustBe 3

          }

        }

      }

      "For P5" - {

        "ctc member" - {

          "returns the P4 list of countries" in {

            val service = app.injector.instanceOf[CountryService]
            val filter = CountryQueryFilter(None, Seq.empty, Some(CtcMember))

            service.filterCountries(filter, Some(P5)).length mustBe 6

          }

        }

        "EU member" - {

          "returns the P4 list of countries" in {

            val service = app.injector.instanceOf[CountryService]
            val filter = CountryQueryFilter(None, Seq.empty, Some(EuMember))

            service.filterCountries(filter, Some(P5)).length mustBe 3

          }

        }

        "Non EU member" - {

          "returns the P4 list of countries" in {

            val service = app.injector.instanceOf[CountryService]
            val filter = CountryQueryFilter(None, Seq.empty, Some(NonEuMember))

            service.filterCountries(filter, Some(P5)).length mustBe 8

          }

        }

      }

    }

}
