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
import controllers.testOnly.helpers.P4
import controllers.testOnly.helpers.P5
import controllers.testOnly.testmodels.Country
import models.requests.CountryMembership.CtcMember
import models.requests.CountryMembership.EuMember
import models.requests.CountryMembership.NonEuMember
import models.requests.CountryQueryFilter
import org.scalacheck.Gen

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

    "For P4" - {

      val version = Gen.oneOf(Some(P4), None).sample.value

      val expectedIds = Seq("AD", "AR", "AU", "FR", "GB", "IT", "AT", "SM")

      "ctc member" - {

        "returns the P4 list of countries" in {
          val filter = CountryQueryFilter(None, Seq.empty, Some(CtcMember))
          val result = service.filterCountries(filter, version)

          result.length mustBe expectedIds.length
          expectedIds.map(result.toString.contains(_) mustBe true)
        }
      }

      "EU member" - {

        "returns the P4 list of countries" in {
          val filter = CountryQueryFilter(None, Seq.empty, Some(EuMember))
          val result = service.filterCountries(filter, version)

          result.length mustBe expectedIds.length
          expectedIds.map(result.toString.contains(_) mustBe true)
        }
      }

      "Non EU member" - {

        "returns the P4 list of countries" in {
          val filter = CountryQueryFilter(None, Seq.empty, Some(NonEuMember))
          val result = service.filterCountries(filter, version)

          result.length mustBe expectedIds.length
          expectedIds.map(result.toString.contains(_) mustBe true)
        }
      }
    }

    "For P5" - {

      val version = Some(P5)

      "ctc member" - {

        val expectedIds = Seq("AD", "FR", "GB", "IT", "AT", "SM")

        "returns the P4 list of countries" in {
          val filter = CountryQueryFilter(None, Seq.empty, Some(CtcMember))
          val result = service.filterCountries(filter, version)

          result.length mustBe expectedIds.length
          expectedIds.map(result.toString.contains(_) mustBe true)
        }
      }

      "EU member" - {

        val expectedIds = Seq("FR", "IT", "AT")

        "returns the P4 list of countries" in {
          val filter = CountryQueryFilter(None, Seq.empty, Some(EuMember))
          val result = service.filterCountries(filter, version)

          println(result.toString())

          result.length mustBe expectedIds.length
          expectedIds.map(result.toString.contains(_) mustBe true)
        }
      }

      "Non EU member" - {

        val expectedIds = Seq("AD", "AR", "AU", "FR", "GB", "IT", "AT", "SM")

        "returns the P4 list of countries" in {
          val filter = CountryQueryFilter(None, Seq.empty, Some(NonEuMember))
          val result = service.filterCountries(filter, version)

          result.length mustBe expectedIds.length
          expectedIds.map(result.toString.contains(_) mustBe true)
        }
      }

      "None" - {

        val expectedIds = Seq("AD", "AR", "AU", "FR", "GB", "IT", "AT", "SM")

        "returns the P4 list of countries" in {
          val filter = CountryQueryFilter(None, Seq.empty, None)
          val result = service.filterCountries(filter, version)

          result.length mustBe expectedIds.length
          expectedIds.map(result.toString.contains(_) mustBe true)
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
