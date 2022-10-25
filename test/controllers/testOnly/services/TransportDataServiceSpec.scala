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
import controllers.testOnly.testmodels.Nationality
import controllers.testOnly.testmodels.TransportMeans
import controllers.testOnly.testmodels.TransportMode
import controllers.testOnly.testmodels.Valid

class TransportDataServiceSpec extends SpecBaseWithAppPerSuite {
  private val service       = app.injector.instanceOf[TransportDataService]
  private val transportMode = TransportMode(Valid, "2015-07-01", "17", "(semi) Trailer on sea-going vessel")
  private val transportMean = TransportMeans("TestName1", "Name of a sea-going vehicle")
  private val nationality   = Nationality("AR", "Argentina")

  "TransportModeService" - {
    "must return transport modes" in {
      service.aggregateData.modes.head mustBe transportMode
    }

    "must return transport means" in {
      service.aggregateData.means.head mustBe transportMean
    }

    "must return transport nationalities" in {
      service.aggregateData.nationalities.head mustBe nationality
    }

  }
}
