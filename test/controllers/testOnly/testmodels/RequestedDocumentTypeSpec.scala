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

import base.{ModelGenerators, SpecBase}
import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.Gen
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import play.api.libs.json._

class RequestedDocumentTypeSpec extends SpecBase with ScalaCheckPropertyChecks with ModelGenerators {

  private val requestedDocumentType = RequestedDocumentType("Y031", "This certificate code may be used to indicate that shipments are coming from or going to an Authorised Economic Operator…")

  "Customs Office model" - {
    "must deserialize from json to a valid model" in {
      val result = validRequestedDocumentTypeJson(requestedDocumentType).as[ControlType]

      result mustBe requestedDocumentType
    }

    "must serialize to json from valid json model" in {
      val result: JsValue = Json.toJson[RequestedDocumentType](requestedDocumentType)

      result mustBe expectedRequestedDocumentTypeJson()
    }

    "must serialize to json and deserialize to a valid model" in {
      forAll(arbitrary[RequestedDocumentType]) {
        requestedDocumentType =>
          val json = validRequestedDocumentTypeJson(requestedDocumentType)
          json.as[RequestedDocumentType] mustBe requestedDocumentType
      }
    }

    "must fail to deserialize" in {

      val invalidJsonGenerator: Gen[JsObject] = for {
        invalidKey   <- arbitrary[String]
        invalidValue <- arbitrary[String]
      } yield Json.obj(invalidKey -> invalidValue)

      forAll(invalidJsonGenerator) {
        invalidJson =>
          intercept[JsResultException](invalidJson.as[RequestedDocumentType])
      }
    }
  }

  def expectedRequestedDocumentTypeJson(requestedDocumentType: RequestedDocumentType = requestedDocumentType): JsValue =
    Json.obj("code" -> requestedDocumentType.code, "description" -> requestedDocumentType.description)

  def validRequestedDocumentTypeJson(controlType: RequestedDocumentType): JsValue =
    Json.parse(s"""
         |{
         |  "code":"${controlType.code}",
         |  "description":"${controlType.description}"
         |  }
         |""".stripMargin)

}
