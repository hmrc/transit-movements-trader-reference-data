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
import controllers.testOnly.testmodels.UnLocode

class UnLocodeServiceSpec extends SpecBaseWithAppPerSuite {

  private val service = app.injector.instanceOf[UnLocodeService]

  "UnLocodeService" - {
    "get" - {
      "must return every UN/LOCODE" in {
        val result = service.get
        result.length mustBe 14
        result.head mustBe UnLocode("ADALV", "Andorra la Vella", None, "--34-6--", "AI", "0601", Some("4230N 00131E"), Some("Muy Vella"))
      }
    }
  }
}
