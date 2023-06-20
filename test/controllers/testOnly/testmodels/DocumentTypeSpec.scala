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

package controllers.testOnly.testmodels

import base.SpecBase
import play.api.libs.json.Json

class DocumentTypeSpec extends SpecBase {

  "must deserialise" - {

    "when reading P4 data" - {
      "when previous" in {
        val json = Json.parse(
          """
            |{
            |  "code" : "pCode",
            |  "description" : "pDescription"
            |}
            |""".stripMargin)

        val result = json.as[DocumentType]

        result mustBe PreviousDocumentType("pCode", Some("pDescription"))
      }

      "when supporting" in {
        val json = Json.parse(
          """
            |{
            |  "code" : "sCode",
            |  "description" : "sDescription",
            |  "transportDocument" : false
            |}
            |""".stripMargin)

        val result = json.as[DocumentType]

        result mustBe SupportingDocumentType("sCode", Some("sDescription"))
      }

      "when transport" in {
        val json = Json.parse(
          """
            |{
            |  "code" : "tCode",
            |  "description" : "tDescription",
            |  "transportDocument" : true
            |}
            |""".stripMargin)

        val result = json.as[DocumentType]

        result mustBe TransportDocumentType("tCode", Some("tDescription"))
      }
    }
  }

  "must serialise" - {
    "when previous" in {
      val document = PreviousDocumentType("pCode", Some("pDescription"))

      val result = Json.toJson[DocumentType](document)

      result mustBe Json.parse(
        """
          |{
          |  "code" : "pCode",
          |  "description" : "pDescription"
          |}
          |""".stripMargin)
    }

    "when supporting" in {
      val document = SupportingDocumentType("sCode", Some("sDescription"))

      val result = Json.toJson[DocumentType](document)

      result mustBe Json.parse(
        """
          |{
          |  "code" : "sCode",
          |  "description" : "sDescription",
          |  "transportDocument" : false
          |}
          |""".stripMargin)
    }

    "when transport" in {
      val document = TransportDocumentType("tCode", Some("tDescription"))

      val result = Json.toJson[DocumentType](document)

      result mustBe Json.parse(
        """
          |{
          |  "code" : "tCode",
          |  "description" : "tDescription",
          |  "transportDocument" : true
          |}
          |""".stripMargin)
    }
  }

}
