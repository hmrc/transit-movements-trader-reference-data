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
import controllers.testOnly.helpers._
import controllers.testOnly.testmodels.Country
import models.requests.CountryQueryFilter
import org.scalacheck.Gen

class CountryServiceSpec extends SpecBaseWithAppPerSuite {

  private val service = app.injector.instanceOf[CountryService]

  "CountryService" - {

    "getCountryByCode" - {
      "when code exists" - {
        "must return country that corresponds to code" in {
          val result = service.getCountryByCode("GB")
          result.get mustBe Country("valid", "GB", "United Kingdom")
        }
      }

      "when code does not exist" - {
        "must return None" in {
          val result = service.getCountryByCode("ZZ")
          result must not be defined
        }
      }
    }

    "filterCountries" - {
      "when P4" - {
        val version = Gen.oneOf(None, Some(P4)).sample.value

        "when excluding GB" - {
          "must not return GB" in {
            val result = service.filterCountries(CountryQueryFilter(None, Seq("GB"), None), version)
            result must not contain Country("valid", "GB", "United Kingdom")
          }
        }

        "when not excluding GB" - {
          "must return GB" in {
            val result = service.filterCountries(CountryQueryFilter(None, Nil, None), version)
            result must contain(Country("valid", "GB", "United Kingdom"))
          }
        }
      }

      "when P5" - {
        val version = P5

        "when no membership filter" - {
          "and no exclusions" - {
            "must return all countries" in {
              val result = service.filterCountries(CountryQueryFilter(None, Nil, None), Some(version))
              result.length mustBe 8
            }
          }

          "and excluding country" - {
            "must return all countries minus the excluded country" in {
              val result = service.filterCountries(CountryQueryFilter(None, Seq("GB"), None), Some(version))
              result.length mustBe 7
            }
          }
        }
      }
    }

    "countryCustomsOfficeSecurityAgreementArea" - {
      "must return the customs office security agreement areas" in {
        val result = service.countryCustomsOfficeSecurityAgreementArea
        result.length mustBe 3
        result.head mustBe Country("valid", "FR", "France")
      }
    }

    "countryAddressPostcodeBased" - {
      "must return the countries whose addresses are postcode based" in {
        val result = service.countryAddressPostcodeBased
        result.length mustBe 2
        result mustBe Seq(
          Country("valid", "IE", "Ireland"),
          Country("valid", "NL", "Netherlands")
        )
      }
    }
  }

}
