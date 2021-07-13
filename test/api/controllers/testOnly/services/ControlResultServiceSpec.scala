/*
 * Copyright 2021 HM Revenue & Customs
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

package api.controllers.testOnly.services

import api.models.ControlResult
import base.SpecBaseWithAppPerSuite

class ControlResultServiceSpec extends SpecBaseWithAppPerSuite {

  val code                              = "A1"
  val firstControlResult: ControlResult = ControlResult("A1", "Satisfactory")
  val lastControlResult: ControlResult  = ControlResult("C1", "")

  "ControlResultService" - {
    "controlResults" - {
      "must return control result list" in {
        val service = app.injector.instanceOf[ControlResultService]

        service.controlResults.head mustBe firstControlResult
        service.controlResults.last mustBe lastControlResult
      }
    }

    "getControlResultsByCode" - {

      "must return some control result for a valid code" in {
        val service = app.injector.instanceOf[ControlResultService]

        service.getControlResultByCode(code).value mustBe firstControlResult
      }

      "must return None for an invalid code" in {
        val service     = app.injector.instanceOf[DangerousGoodsCodeService]
        val invalidCode = "1234"

        service.getDangerousGoodsCodeByCode(invalidCode) mustBe None
      }
    }
  }
}
