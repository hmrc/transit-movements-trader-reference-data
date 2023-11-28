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
import controllers.testOnly.testmodels.CustomsOffice

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

  "customsOfficeExit (EXT)" - {

    "get customs office of exit based on country code" in {
      val service = app.injector.instanceOf[CustomsOfficesService]

      service.getCustomsOfficesOfTheCountry("GB", List("EXT")).head mustBe
        CustomsOffice(
          "GB000040",
          "Dover (OTS) Freight Clearance",
          "GB",
          Some("+44 (0)3000 575 988"),
          List("ENT", "EXP", "EXT")
        )
    }

    "must return empty list for invalid country code" in {
      val service            = app.injector.instanceOf[CustomsOfficesService]
      val invalidCountryCode = "123"

      service.getCustomsOfficesOfTheCountry(invalidCountryCode, List("EXT")) mustBe empty
    }

    "must only return list of customs offices from country code provided" in {
      val service = app.injector.instanceOf[CustomsOfficesService]

      service.getCustomsOfficesOfTheCountry("XI", List("EXT")) mustBe Seq(
        CustomsOffice(
          "XI000142",
          "Belfast EPU",
          "XI",
          Some("+44 (0)3000 575 988"),
          List("ENT", "EXP", "EXT")
        ),
        CustomsOffice(
          "XI005160",
          "Warrenpoint",
          "XI",
          Some("+44 (0)3000 575 988"),
          List("ENT", "EXP", "EXT")
        ),
        CustomsOffice(
          "XI005220",
          "Larne",
          "XI",
          Some("+44 (0)3000 575 988"),
          List("ENT", "EXP", "EXT")
        )
      )
    }
  }

  "customsOfficeTransit (TRA)" - {

    "must return the offices of transit for a given country code" ignore {
      val service = app.injector.instanceOf[CustomsOfficesService]

      service.getCustomsOfficesOfTheCountry("GB", List("TRA")).head mustBe
        CustomsOffice(
          "GB000060",
          "Dover/Folkestone Eurotunnel Freight",
          "GB",
          Some("+44 (0)3000 515831"),
          List("DEP", "DES", "ENT", "EXP", "TRA")
        )
    }

    "must return an empty list where no match" in {
      val service = app.injector.instanceOf[CustomsOfficesService]

      service.getCustomsOfficesOfTheCountry("12", List("TRA")) mustBe empty
    }
  }

  "customsOfficeDestination (DES)" - {

    "must return the offices of destination for a given country code" ignore {
      val service = app.injector.instanceOf[CustomsOfficesService]

      service.getCustomsOfficesOfTheCountry("GB", List("DES")).head mustBe
        CustomsOffice(
          "GB000060",
          "Dover/Folkestone Eurotunnel Freight",
          "GB",
          Some("+44 (0)3000 515831"),
          List("DEP", "DES", "ENT", "EXP", "TRA")
        )
    }

    "must return an empty list where no match" in {
      val service = app.injector.instanceOf[CustomsOfficesService]

      service.getCustomsOfficesOfTheCountry("12", List("DES")) mustBe empty
    }
  }

  "customsOfficeDeparture (DEP)" - {

    "get customs office of departure based on country code" ignore {
      val service = app.injector.instanceOf[CustomsOfficesService]

      service.getCustomsOfficesOfTheCountry("GB", List("DEP")).head mustBe
        CustomsOffice(
          "GB000060",
          "Dover/Folkestone Eurotunnel Freight",
          "GB",
          Some("+44 (0)3000 515831"),
          List("DEP", "DES", "ENT", "EXP", "TRA")
        )
    }

    "must return an empty list where no match" in {
      val service = app.injector.instanceOf[CustomsOfficesService]

      service.getCustomsOfficesOfTheCountry("12", List("DEP")) mustBe empty
    }
  }
}
