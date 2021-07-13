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

import api.models.PreviousDocumentType
import base.SpecBaseWithAppPerSuite

class PreviousDocumentTypeServiceSpec extends SpecBaseWithAppPerSuite {

  val service               = app.injector.instanceOf[PreviousDocumentTypeService]
  private val documentType1 = PreviousDocumentType("T2SM", Some("T2SM"))
  private val documentType2 = PreviousDocumentType("CO", None)

  "PreviousDocumentTypeService" - {
    "must return previous document types" in {
      service.previousDocumentTypes.headOption.value mustBe documentType1
      service.previousDocumentTypes.lastOption.value mustBe documentType2
    }

    "getPreviousDocumentTypeByCode" - {
      "must return correct previous document type for the input code with description" in {
        service.getPreviousDocumentTypeByCode("T2SM").value mustBe documentType1
      }

      "must return correct previous document type for the input code without description" in {
        service.getPreviousDocumentTypeByCode("CO").value mustBe documentType2
      }

      "must return None when previous document type cannot be found" in {
        service.getPreviousDocumentTypeByCode("InvalidCode") mustBe None
      }
    }
  }
}
