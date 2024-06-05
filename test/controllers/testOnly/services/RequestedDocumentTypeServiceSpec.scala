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
import controllers.testOnly.testmodels.{ControlResult, RequestedDocumentType}

class RequestedDocumentTypeServiceSpec extends SpecBaseWithAppPerSuite {

  val code                              = "Y031"
  val firstRequestedDocumentType: RequestedDocumentType = RequestedDocumentType("Y031", "This certificate code may be used to indicate that shipments are coming from or going to an Authorised Economic Operator…")
  val lastRequestedDocumentType: RequestedDocumentType  = RequestedDocumentType("C085", "Common Health Entry Document for Plants and Plant Products…")

  "RequestedDocumentTypeService" - {
    "requestedDocumentTypes" - {
      "must return requested Document type list" in {
        val service = app.injector.instanceOf[RequestedDocumentTypeService]

        service.requestedDocumentTypes.head mustBe firstRequestedDocumentType
        service.requestedDocumentTypes.last mustBe lastRequestedDocumentType
      }
    }

    "getRequestedDocumentTypeByCode" - {

      "must return some requested document type for a valid code" in {
        val service = app.injector.instanceOf[RequestedDocumentTypeService]

        service.getRequestedDocumentTypeByCode(code).value mustBe firstRequestedDocumentType
      }

      "must return None for an invalid code" in {
        val service     = app.injector.instanceOf[RequestedDocumentTypeService]
        val invalidCode = "1234"

        service.getRequestedDocumentTypeByCode(invalidCode) mustBe None
      }
    }
  }
}
