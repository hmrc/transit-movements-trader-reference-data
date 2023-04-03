/*
 * Copyright 2023 HM Revenue & Customs
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
import controllers.testOnly.helpers.P5
import controllers.testOnly.testmodels.CustomsOffice
import org.scalacheck.Gen

class CustomsOfficeServiceSpec extends SpecBaseWithAppPerSuite {

  val officeId1 = "GB000040"
  val officeId2 = "AT530100"
  val version   = Some(P5)

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

      service.getCustomsOfficesOfTheCountry("GB", List("EXT"), version).head mustBe
        CustomsOffice(
          "GB000011",
          "Birmingham Airport",
          "GB",
          Some("+44 (0)121 781 7850"),
          List("DEP", "DEP", "DES", "DES", "ENT", "EXP", "EXP", "EXT", "EXT", "TRA", "TRA")
        )
    }

    "must return empty list for invalid country code" in {
      val service            = app.injector.instanceOf[CustomsOfficesService]
      val version            = Gen.oneOf(Some(P5), None).sample.value
      val invalidCountryCode = "123"

      service.getCustomsOfficesOfTheCountry(invalidCountryCode, List("EXT"), version) mustBe empty
    }

    "must only return list of customs offices from country code provided" in {
      val service = app.injector.instanceOf[CustomsOfficesService]

      service.getCustomsOfficesOfTheCountry("XI", List("EXT"), version) mustBe Seq(
        CustomsOffice(
          "XI000142",
          "Belfast EPU",
          "XI",
          Some("+44 (0)02896 931537"),
          List("DEP", "AUT", "DEP", "DEP", "DES", "DES", "DES", "ENT", "ENT", "EXT", "EXT", "EXT", "TRA", "TRA", "TRA")
        ),
        CustomsOffice(
          "XIREX001",
          "Implementation of IE/NI protocol",
          "XI",
          None,
          List(
            "RRG",
            "RRG",
            "RRG",
            "RRG",
            "DEP",
            "RCA",
            "DEP",
            "DEP",
            "DES",
            "DES",
            "DES",
            "ENT",
            "EXP",
            "EXP",
            "EXT",
            "EXT",
            "TRA",
            "TRA",
            "TRA",
            "ACE",
            "ACP",
            "ACR",
            "ACT",
            "AUT",
            "AWB",
            "BTI",
            "CAE",
            "CAU",
            "CCA",
            "CCD",
            "CCL",
            "CGU",
            "CND",
            "CVA",
            "CWP",
            "DIS",
            "DPO",
            "EIN",
            "EIR",
            "ENL",
            "ENQ",
            "ETD",
            "EUS",
            "EXC",
            "EXL",
            "GUA",
            "INC",
            "INC",
            "IPO",
            "MCA",
            "OPO",
            "PLA",
            "PRT",
            "RAC",
            "REC",
            "REG",
            "RFC",
            "RSS",
            "SAS",
            "SCO",
            "SDE",
            "SSE",
            "TEA",
            "TRD",
            "TST",
            "TXT"
          )
        )
      )
    }
  }

  "customsOfficeTransit" - {

    "must return the offices of transit for a given country code" in {
      val service = app.injector.instanceOf[CustomsOfficesService]

      service.getCustomsOfficesOfTheCountry("GB", List("TRA"), version).head mustBe
        CustomsOffice("GB000001", "Central Community Transit Office.", "GB", Some("+44 01268666688"), List("AUT", "RCA", "TRA", "CAU", "ENQ", "GUA", "REC"))
    }

    "must return an empty list where no match" in {
      val service = app.injector.instanceOf[CustomsOfficesService]

      service.getCustomsOfficesOfTheCountry("12", List("TRA"), version) mustBe empty
    }
  }

  "customsOfficeDestination" - {

    "must return the offices of destination for a given country code" in {
      val service = app.injector.instanceOf[CustomsOfficesService]

      service.getCustomsOfficesOfTheCountry("GB", List("DES"), version).head mustBe
        CustomsOffice(
          "GB000011",
          "Birmingham Airport",
          "GB",
          Some("+44 (0)121 781 7850"),
          List("DEP", "DEP", "DES", "DES", "ENT", "EXP", "EXP", "EXT", "EXT", "TRA", "TRA")
        )
    }

    "must return an empty list where no match" in {
      val service = app.injector.instanceOf[CustomsOfficesService]

      service.getCustomsOfficesOfTheCountry("12", List("DES"), version) mustBe empty
    }
  }

  "customsOfficeDeparture" - {

    "get customs office of departure based on country code" in {
      val service = app.injector.instanceOf[CustomsOfficesService]

      service.getCustomsOfficesOfTheCountry("GB", List("DEP"), version).head mustBe
        CustomsOffice("GB000008", "Heysham", "GB", Some("03000599436"), List("DEP"))
    }

    "must return an empty list where no match" in {
      val service = app.injector.instanceOf[CustomsOfficesService]

      service.getCustomsOfficesOfTheCountry("12", List("DEP"), version) mustBe empty
    }
  }

  "customsOfficeTransitExit" - {

    "get customs office of transit exit based on country code" in {
      val service = app.injector.instanceOf[CustomsOfficesService]

      service.getCustomsOfficesOfTheCountry("CY", List("TXT"), version).head mustBe
        CustomsOffice(
          "CY000440",
          "LARNACA AIRPORT",
          "CY",
          Some("00357.24.816057"),
          List(
            "CAE",
            "TXT",
            "PLA",
            "DIS",
            "RFC",
            "SCO",
            "CAU",
            "CAU",
            "CAU",
            "DEP",
            "DEP",
            "DES",
            "DES",
            "EIN",
            "ENL",
            "ENL",
            "ENL",
            "ENQ",
            "ENT",
            "ENT",
            "ENT",
            "EXC",
            "EXC",
            "EXL",
            "EXL",
            "EXL",
            "EXP",
            "EXP",
            "EXT",
            "EXT",
            "GUA",
            "GUA",
            "REC"
          )
        )
    }

    "must return an empty list when no match" in {
      val service = app.injector.instanceOf[CustomsOfficesService]

      service.getCustomsOfficesOfTheCountry("12", List("TXT"), version) mustBe empty
    }
  }
}
