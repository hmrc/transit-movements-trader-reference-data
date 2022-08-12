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
import controllers.testOnly.testmodels.CustomsOffice
import controllers.testOnly.testmodels.CustomsOfficeP5

class CustomsOfficeServiceSpec extends SpecBaseWithAppPerSuite {

  val officeId1 = "GB000040"
  val officeId2 = "AT530100"

  val customsOffice1: CustomsOffice =
    CustomsOffice(officeId1, "Dover (OTS) Freight Clearance", "GB", Some("+44 (0)3000 575 988"), List("ENT", "EXP", "EXT"))

  val customsOffice2: CustomsOffice =
    CustomsOffice(officeId2, "Wels, Terminal Wels", "AT", Some("+44 (0)3000 575 988"), List("ENT"))

  "must return customs office list" in {
    val service = app.injector.instanceOf[CustomsOfficesService]

    val result = service.customsOffices
    result.length mustBe 14
    result.head mustBe customsOffice1
  }

  "getCustomsOffice" - {

    "must return some customs office for a valid office id" in {
      val service = app.injector.instanceOf[CustomsOfficesService]

      service.getCustomsOffice(officeId1).value mustBe customsOffice1
    }

    "must return None for an invalid office value" in {
      val service = app.injector.instanceOf[CustomsOfficesService]

      service.getCustomsOffice("1234") mustBe None
    }
  }

  "customsOfficeExit" - {

    "get customs office of exit based on country code" in {
      val service = app.injector.instanceOf[CustomsOfficesService]

      service.customsOfficeExit("CZ").head mustBe CustomsOfficeP5("CZ530299", "Letiště Tuřany, Brno-Tuřany")
    }

    "must return empty list for invalid country code" in {
      val service            = app.injector.instanceOf[CustomsOfficesService]
      val invalidCountryCode = "123"

      service.customsOfficeExit(invalidCountryCode) mustBe empty
    }

    "must only return list of customs offices from country code provided" in {
      val service = app.injector.instanceOf[CustomsOfficesService]

      service.customsOfficeExit("DE") mustBe Seq(
        CustomsOfficeP5("DE002705", "Weeze"),
        CustomsOfficeP5("DE003030", "Erfurt-Flughafen Luftverkehr")
      )
    }
  }

  "customsOfficeTransit" - {

    "must return the offices of transit for a given country code" in {
      val service = app.injector.instanceOf[CustomsOfficesService]

      service.customsOfficeTransit("FR") mustBe Seq(
        CustomsOfficeP5("FR001260", "Dunkerque port bureau")
      )
    }

    "must return an empty list where no match" in {
      val service = app.injector.instanceOf[CustomsOfficesService]

      service.customsOfficeTransit("12") mustBe empty
    }
  }

  "customsOfficeDestination" - {

    "must return the offices of destination for a given country code" in {
      val service = app.injector.instanceOf[CustomsOfficesService]

      service.customsOfficeDestination("SM") mustBe Seq(
        CustomsOfficeP5("SM000001", "Ufficio Tributario Rep. San Marino")
      )
    }

    "must return an empty list where no match" in {
      val service = app.injector.instanceOf[CustomsOfficesService]

      service.customsOfficeDestination("12") mustBe empty
    }
  }

  "customsOfficeDeparture" - {

    "get customs office of departure based on country code" in {
      val service = app.injector.instanceOf[CustomsOfficesService]

      service.customsOfficeDeparture("XI").head mustBe CustomsOfficeP5("XI000142", "Belfast")
    }

    "must return an empty list where no match" in {
      val service = app.injector.instanceOf[CustomsOfficesService]

      service.customsOfficeDeparture("12") mustBe empty
    }
  }

  "customsOfficeTransitExit" - {

    "get customs office of transit exit based on country code" in {
      val service = app.injector.instanceOf[CustomsOfficesService]

      service.customsOfficeTransitExit("CY") mustBe Seq(
        CustomsOfficeP5("CY000440", "LARNACA AIRPORT"),
        CustomsOfficeP5("CY000640", "PAPHOS AIRPORT")
      )
    }

    "must return an empty list when no match" in {
      val service = app.injector.instanceOf[CustomsOfficesService]

      service.customsOfficeDeparture("12") mustBe empty
    }
  }
}
