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
import models.CustomsOffice
import org.scalatest.MustMatchers

class CustomsOfficesServiceSpec extends SpecBase with MustMatchers {

  val customsOffice1: CustomsOffice =
    CustomsOffice("GB000011", "Birmingham Airport", None, List("TRA", "DEP", "DES"))

  val customsOffice2: CustomsOffice =
    CustomsOffice("GB003280", "Workington", None, List.empty)

  "must return customs office list" in {
    val service = app.injector.instanceOf[CustomsOfficesService]

    service.customsOffices.head mustBe customsOffice1
    service.customsOffices.last mustBe customsOffice2
  }

  "getCustomsOffice" - {

    "must return some customs office when given a valid id" in {
      val service = app.injector.instanceOf[CustomsOfficesService]

      service.getCustomsOffice(customsOffice1.id).value mustBe customsOffice1
    }

    "must return None when given an invalid id" in {
      val service   = app.injector.instanceOf[CustomsOfficesService]
      val invalidId = "123"

      service.getCustomsOffice(invalidId) mustBe None
    }

  }
}
