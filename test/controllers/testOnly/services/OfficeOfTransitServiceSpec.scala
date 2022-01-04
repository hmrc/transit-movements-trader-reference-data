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

import controllers.testOnly.testmodels.OfficeOfTransit
import base.SpecBaseWithAppPerSuite

class OfficeOfTransitServiceSpec extends SpecBaseWithAppPerSuite {

  val officeId = "AD000001"

  val officeOfTransit1: OfficeOfTransit =
    OfficeOfTransit(officeId, "SANT JULIÀ DE LÒRIA, CUSTOMS OFFICE SANT JULIÀ DE LÒRIA")

  val officeOfTransit2: OfficeOfTransit =
    OfficeOfTransit("DE006302", "Heiligenhafen, Heiligenhafen")

  "must return offices of transit list" in {
    val service = app.injector.instanceOf[OfficeOfTransitService]

    service.officesOfTransit.head mustBe officeOfTransit1
    service.officesOfTransit.last mustBe officeOfTransit2
  }

  "get office of transit" - {

    "must return some office of transit for a valid office id" in {
      val service = app.injector.instanceOf[OfficeOfTransitService]

      service.getOfficeOfTransit(officeId).value mustBe officeOfTransit1
    }

    "must return None for an invalid office value" in {
      val service            = app.injector.instanceOf[OfficeOfTransitService]
      val invalidOfficeValue = "1234"

      service.getOfficeOfTransit(invalidOfficeValue) mustBe None
    }

  }

}
