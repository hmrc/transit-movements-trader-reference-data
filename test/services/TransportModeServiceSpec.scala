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
import models.TransportMode
import models.Valid
import org.scalatest.MustMatchers

class TransportModeServiceSpec extends SpecBase with MustMatchers {
  val service                = app.injector.instanceOf[TransportModeService]
  private val transportMode  = TransportMode(Valid, "2015-07-01", "17", "(semi) Trailer on sea-going vessel")
  private val transportMode1 = TransportMode(Valid, "2015-07-01", "10", "Sea transport")

  "TransportModeService" - {
    "must return transport modes" in {
      service.transportModes.head mustBe transportMode
      service.transportModes.last mustBe transportMode1
    }

    "getTransportMode" - {
      "must return correct transport mode for the input code" in {
        service.getTransportModeByCode("10").value mustBe transportMode1
      }

      "must return None when transport mode cannot be found" in {
        service.getTransportModeByCode("InvalidCode") mustBe None
      }
    }
  }
}
