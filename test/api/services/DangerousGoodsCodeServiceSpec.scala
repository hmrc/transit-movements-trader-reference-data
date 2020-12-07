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

import api.models.DangerousGoodsCode
import base.SpecBaseWithAppPerSuite

class DangerousGoodsCodeServiceSpec extends SpecBaseWithAppPerSuite {

  val code                = "0004"
  val dangerousGoodsCode1 = DangerousGoodsCode("0004", "AMMONIUM PICRATE dry or wetted with less than 10% water, by mass")
  val dangerousGoodsCode2 = DangerousGoodsCode("0005", "CARTRIDGES FOR WEAPONS with bursting charge")

  "must return dangerous goods code list" in {
    val service = app.injector.instanceOf[DangerousGoodsCodeService]

    service.dangerousGoodsCodes.head mustBe dangerousGoodsCode1
    service.dangerousGoodsCodes.last mustBe dangerousGoodsCode2
  }

  "get dangerous goods code" - {

    "must return some dangerous good code for a valid code" in {
      val service = app.injector.instanceOf[DangerousGoodsCodeService]

      service.getDangerousGoodsCodeByCode(code).value mustBe dangerousGoodsCode1
    }

    "must return None for an invalid code" in {
      val service     = app.injector.instanceOf[DangerousGoodsCodeService]
      val invalidCode = "1234"

      service.getDangerousGoodsCodeByCode(invalidCode) mustBe None
    }
  }
}
