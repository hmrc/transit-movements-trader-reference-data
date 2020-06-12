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

package services

import base.SpecBase
import models.Country
import org.scalatest.MustMatchers

class CountryServiceSpec extends SpecBase with MustMatchers {

  val service = app.injector.instanceOf[CountryService]

  "countries" - {
    "must return full list of countries" in {
      val firstCountryCode = Country("valid", "AD", "Andorra")
      val lastCountryCode  = Country("valid", "GB", "United Kingdom")

      service.countries.head mustBe firstCountryCode
      service.countries.last mustBe lastCountryCode
    }
  }

  "getCountryByCode" - {
    "must return correct country by country code" in {
      val expectedResult = Country("valid", "GB", "United Kingdom")

      service.getCountryByCode("GB").value mustBe expectedResult
    }

    "must return None when country cannot be found" in {

      service.getCountryByCode("Invalid country code") mustBe None
    }
  }

}
