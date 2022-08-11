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
import controllers.testOnly.testmodels.{CustomsOffice, CustomsOfficeP5}

class CustomsOfficeServiceSpec extends SpecBaseWithAppPerSuite {

  val officeId1 = "GB005010"
  val officeId2 = "AT530100"

  val customsOffice1: CustomsOffice =
    CustomsOffice(officeId1, "Appledore", "GB", Some("+44 (0)1255 244725"), List("RSS"))

  val customsOffice2: CustomsOffice =
    CustomsOffice(officeId2, "Wels, Terminal Wels", "AT", Some("+44 (0)3000 575 988"), List("ENT"))

  "must return customs office list" in {
    val service = app.injector.instanceOf[CustomsOfficesService]

    service.customsOffices.head mustBe customsOffice1
    service.customsOffices.last mustBe customsOffice2
  }

  "getCustomsOffice" - {

    "must return some customs office for a valid office id" in {
      val service = app.injector.instanceOf[CustomsOfficesService]

      service.getCustomsOffice(officeId1).value mustBe customsOffice1
    }

    "must return None for an invalid office value" in {
      val service = app.injector.instanceOf[CustomsOfficesService]
      val invalidOfficeValue = "1234"

      service.getCustomsOffice(invalidOfficeValue) mustBe None
    }

  }

  "customsOfficeExit" - {
    val customsOfficeExit1: CustomsOfficeP5 = CustomsOfficeP5("CZ530299", "Letiště Tuřany, Brno-Tuřany")

    "get customs office of exit based on country code" in {
      val service = app.injector.instanceOf[CustomsOfficesService]

      service.customsOfficeExit("CZ").head mustBe customsOfficeExit1

    }

    "must return empty list for invalid country code" in {
      val service = app.injector.instanceOf[CustomsOfficesService]
      val invalidCountryCode = "123"

      service.customsOfficeExit(invalidCountryCode) mustBe empty

    }

    "must only return list of customs offices from country code provided" in {
      val service = app.injector.instanceOf[CustomsOfficesService]

      service.customsOfficeExit("DE").filter(_.getCountryCode() != "DE") mustBe empty
    }

    "customsOfficeTransit" - {

      "must return the offices of transit for a given country code" in {

        val service = app.injector.instanceOf[CustomsOfficesService]
        val expected = Seq(CustomsOfficeP5("FR001260", "Dunkerque port bureau"))

        service.customsOfficeTransit("FR") mustBe expected

      }

      "must return an empty list where no match" in {

        val service = app.injector.instanceOf[CustomsOfficesService]
        val expected = Seq.empty

        service.customsOfficeTransit("12") mustBe expected

      }

    }

    "customsOfficeDestination" - {

      "must return the offices of destination for a given country code" in {

        val service = app.injector.instanceOf[CustomsOfficesService]
        val expected = Seq(CustomsOfficeP5("SM000001", "Ufficio Tributario Rep. San Marino"))

        service.customsOfficeDestination("SM") mustBe expected

      }

      "must return an empty list where no match" in {

        val service = app.injector.instanceOf[CustomsOfficesService]
        val expected = Seq.empty

        service.customsOfficeDestination("12") mustBe expected

      }
    }
  }

  "customsOfficeDeparture" - {

    val customsOfficeDeparture: CustomsOfficeP5 = CustomsOfficeP5("XI000142", "Belfast")

    "get customs office of departure based on country code" in {

      val service = app.injector.instanceOf[CustomsOfficesService]

      service.customsOfficeDeparture("XI").head mustBe customsOfficeDeparture
    }

    "must return an empty list where no match" in {

      val service = app.injector.instanceOf[CustomsOfficesService]
      val expected = Seq.empty

      service.customsOfficeDeparture("12") mustBe expected
    }
  }

}
