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

import api.models.DocumentType
import base.SpecBase
import org.scalatest.MustMatchers

class DocumentTypeServiceSpec extends SpecBase with MustMatchers {

  "must return document types" in {
    val service = app.injector.instanceOf[DocumentTypeService]

    val expectedFirstItem = DocumentType("18", "Movement certificate A.TR.1", transportDocument = false)

    service.documentTypes.head mustEqual expectedFirstItem
  }
}