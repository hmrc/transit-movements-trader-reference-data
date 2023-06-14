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

import controllers.testOnly.testmodels.PreviousDocumentType
import base.SpecBaseWithAppPerSuite
import controllers.testOnly.helpers._
import org.scalacheck.Gen
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks

class PreviousDocumentTypeServiceSpec extends SpecBaseWithAppPerSuite with ScalaCheckPropertyChecks {

  val service               = app.injector.instanceOf[PreviousDocumentTypeService]
  private val documentType1 = PreviousDocumentType("T1", Some("Document T1"))
  private val documentType2 = PreviousDocumentType("CO", Some("SAD - Community goods subject"))
  private val documentType3 = PreviousDocumentType("821", None)

  "PreviousDocumentTypeService" - {
    "must return previous document types" - {
      "when P4" in {
        forAll(Gen.oneOf(Some(P4), None)) {
          version =>
            service.previousDocumentTypes(version).headOption.value mustBe documentType1
            service.previousDocumentTypes(version).lastOption.value mustBe documentType3
        }
      }

      "when P5" in {
        service.previousDocumentTypes(Some(P5)).headOption.value.code mustBe "C512"
        service.previousDocumentTypes(Some(P5)).lastOption.value.code mustBe "NMRN"
      }
    }

    "getPreviousDocumentTypeByCode" - {
      "must return correct previous document type for the input code with description" in {
        service.getPreviousDocumentTypeByCode("T1").value mustBe documentType1
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
