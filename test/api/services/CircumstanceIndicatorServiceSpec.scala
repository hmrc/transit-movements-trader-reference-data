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

import api.models.CircumstanceIndicator
import base.SpecBase
import org.scalatest.MustMatchers

class CircumstanceIndicatorServiceSpec extends SpecBase with MustMatchers {

  private val code                   = "E"
  private val circumstanceIndicator1 = CircumstanceIndicator("E", "Authorised economic operators")
  private val circumstanceIndicator2 = CircumstanceIndicator("B", "Ship and aircraft supplies")

  "must return circumstance indicator list" in {
    val service = app.injector.instanceOf[CircumstanceIndicatorService]

    service.circumstanceIndicators.head mustBe circumstanceIndicator1
    service.circumstanceIndicators.last mustBe circumstanceIndicator2
  }

  "get circumstance indicator code" - {

    "must return some circumstance indicator for a valid code" in {
      val service = app.injector.instanceOf[CircumstanceIndicatorService]

      service.getCircumstanceIndicator(code).value mustBe circumstanceIndicator1
    }

    "must return None for an invalid code" in {
      val service     = app.injector.instanceOf[CircumstanceIndicatorService]
      val invalidCode = "1234"

      service.getCircumstanceIndicator(invalidCode) mustBe None
    }
  }

}
