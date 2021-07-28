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

import api.controllers.testOnly.models.MethodOfPayment
import base.SpecBaseWithAppPerSuite

class MethodOfPaymentServiceSpec extends SpecBaseWithAppPerSuite {

  "must return method of payment" in {
    val service = app.injector.instanceOf[MethodOfPaymentService]

    val expectedFirstItem = MethodOfPayment("A", "Payment in cash")

    service.methodOfPayment.head mustEqual expectedFirstItem
  }
}
