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

class ControlTypeSpec extends SpecBase with ScalaCheckPropertyChecks with ModelGenerators {

  private val controlType = ControlType("42", "Intrusive inspection")

  "Customs Office model" - {
    "must deserialize from json to a valid model" in {
      val result = validControlTypeJson(controlType).as[ControlType]

      result mustBe controlType
    }

    "must serialize to json from valid json model" in {
      val result: JsValue = Json.toJson[ControlType](controlType)

      result mustBe expectedControlTypeJson()
    }

    "must serialize to json and deserialize to a valid model" in {
      forAll(arbitrary[ControlType]) {
        controlType =>
          val json = validControlTypeJson(controlType)
          json.as[ControlType] mustBe controlType
      }
    }

    "must fail to deserialize" in {

      val invalidJsonGenerator: Gen[JsObject] = for {
        invalidKey <- arbitrary[String]
        invalidValue <- arbitrary[String]
      } yield Json.obj(invalidKey -> invalidValue)

      forAll(invalidJsonGenerator) {
        invalidJson =>
          intercept[JsResultException](invalidJson.as[ControlType])
      }
    }
  }

  def expectedControlTypeJson(controlType: ControlType = controlType): JsValue =
    Json.obj(
      "code" -> controlType.code,
      "description" -> controlType.description)

  def validControlTypeJson(controlType: ControlType): JsValue = {

    Json.parse(
      s"""
         |{
         |  "code":"${controlType.code}",
         |  "description":"${controlType.description}"
         |  }
         |""".stripMargin)
  }

}
