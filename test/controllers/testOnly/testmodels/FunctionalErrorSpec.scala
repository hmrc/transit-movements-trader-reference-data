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

import base.ModelGenerators
import base.SpecBase
import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.Gen
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import play.api.libs.json._

class FunctionalErrorSpec extends SpecBase with ScalaCheckPropertyChecks with ModelGenerators {

  private val functionalError = FunctionalError("42", "Intrusive inspection")

  "Customs Office model" - {
    "must deserialize from json to a valid model" in {
      val result = validFunctionalErrorTypeJson(functionalError).as[FunctionalError]

      result mustBe functionalError
    }

    "must serialize to json from valid json model" in {
      val result: JsValue = Json.toJson[FunctionalError](functionalError)

      result mustBe expectedFunctionalErrorTypeJson()
    }

    "must serialize to json and deserialize to a valid model" in {
      forAll(arbitrary[FunctionalError]) {
        functionalError =>
          val json = validFunctionalErrorTypeJson(functionalError)
          json.as[FunctionalError] mustBe functionalError
      }
    }

    "must fail to deserialize" in {

      val invalidJsonGenerator: Gen[JsObject] = for {
        invalidKey   <- arbitrary[String]
        invalidValue <- arbitrary[String]
      } yield Json.obj(invalidKey -> invalidValue)

      forAll(invalidJsonGenerator) {
        invalidJson =>
          intercept[JsResultException](invalidJson.as[FunctionalError])
      }
    }
  }

  def expectedFunctionalErrorTypeJson(functionalError: FunctionalError = functionalError): JsValue =
    Json.obj("code" -> functionalError.code, "description" -> functionalError.description)

  def validFunctionalErrorTypeJson(functionalError: FunctionalError): JsValue =
    Json.parse(s"""
         |{
         |  "code":"${functionalError.code}",
         |  "description":"${functionalError.description}"
         |  }
         |""".stripMargin)

}
