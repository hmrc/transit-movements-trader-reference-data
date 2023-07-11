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

import base.ModelGenerators
import base.SpecBaseWithAppPerSuite
import controllers.testOnly.testmodels.FunctionalError
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks.forAll
import org.scalacheck.Arbitrary.arbitrary

class FunctionalErrorServiceSpec extends SpecBaseWithAppPerSuite with ModelGenerators {

  val functionalError1: FunctionalError = FunctionalError("15", "Condition violation (Not allowed)")

  "getFunctionalError" - {
    "must return existing control type" in {
      val service = app.injector.instanceOf[FunctionalErrorService]

      val result = service.getFunctionalError("15")
      result.get.description mustBe "Condition violation (Not allowed)"
      result mustBe Some(functionalError1)
    }

    "must return None for control type that do not exist" in {
      val service = app.injector.instanceOf[FunctionalErrorService]

      val result = service.getFunctionalError("9999")
      result mustBe None
    }
  }

  "getAllErrorCodes" - {
    "must return sequence of functional errors" in {
      val service = app.injector.instanceOf[FunctionalErrorService]

      val result = service.getAllErrorCodes

      forAll(arbitrary[FunctionalError]) {
        functionalError =>
          result.contains(functionalError) mustBe true
      }
    }
  }
}
