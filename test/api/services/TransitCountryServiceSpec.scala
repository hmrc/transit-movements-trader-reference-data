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

package api.services

import api.models.Country
import base.SpecBaseWithAppPerSuite

class TransitCountryServiceSpec extends SpecBaseWithAppPerSuite {

  "must return transit countries" in {
    val service = app.injector.instanceOf[TransitCountryService]

    val firstCountryCode = Country("valid", "AD", "Andorra")
    val lastCountryCode  = Country("valid", "GB", "United Kingdom")

    service.transitCountryCodes.head mustBe firstCountryCode
    service.transitCountryCodes.last mustBe lastCountryCode
  }
}
