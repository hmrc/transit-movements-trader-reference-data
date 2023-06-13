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
import controllers.testOnly.helpers._
import controllers.testOnly.testmodels.DocumentType
import org.scalacheck.Gen
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks

class DocumentTypeServiceSpec extends SpecBaseWithAppPerSuite with ScalaCheckPropertyChecks {

  private val service = app.injector.instanceOf[DocumentTypeService]

  "must return document types" - {
    "when P4" in {
      forAll(Gen.oneOf(Some(P4), None)) {
        version =>
          val expectedFirstItem = DocumentType("18", "Movement certificate A.TR.1", transportDocument = false)

          service.documentTypes(version).head mustEqual expectedFirstItem
      }
    }

    "when P5" in {
      service.documentTypes(Some(P5)).head.code mustEqual "C085"
    }
  }
}
