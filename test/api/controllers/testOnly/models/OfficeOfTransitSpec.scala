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

package api.controllers.testOnly.models

import api.generators.ModelGenerators
import base.SpecBase
import org.scalacheck.Arbitrary.arbitrary
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import org.scalacheck.Gen
import play.api.libs.json._

class OfficeOfTransitSpec extends SpecBase with ScalaCheckPropertyChecks with ModelGenerators {

  private val officesOfTransit = OfficeOfTransit("DE009583", "Stuttgart, Stuttgart-Hauptbahnhof")

  "Transit Office model" - {

    "must deserialise from json to a valid model" in {

      val result = validOfficeOfTransitsJson(officesOfTransit).as[OfficeOfTransit]

      result mustBe officesOfTransit
    }

    "must serialize to json from valid json model" in {
      val result = Json.toJson(officesOfTransit)
      result mustBe expectedOfficesOfTransitJson()
    }

    "must serialize to json and deserialize to a valid model" in {
      forAll(arbitrary[OfficeOfTransit]) {
        officeOfTransit =>
          val json = validOfficeOfTransitsJson(officeOfTransit)
          json.as[OfficeOfTransit] mustBe officeOfTransit
      }
    }

    "must fail to deserialize" in {

      val invalidJsonGenerator: Gen[JsObject] = for {
        invalidKey   <- arbitrary[String]
        invalidValue <- arbitrary[String]
      } yield Json.obj(invalidKey -> invalidValue)

      forAll(invalidJsonGenerator) {
        invalidJson =>
          intercept[JsResultException](invalidJson.as[OfficeOfTransit])
      }
    }
  }

  def expectedOfficesOfTransitJson(officeOfTransit: OfficeOfTransit = officesOfTransit): JsValue =
    Json.obj(
      "id"   -> officeOfTransit.id,
      "name" -> officeOfTransit.name
    )

  def validOfficeOfTransitsJson(office: OfficeOfTransit): JsValue =
    Json.parse(s"""
                  |{
                  |  "ID":"${office.id}",
                  |  "NAME":"${office.name}"
                  |  }
                  |""".stripMargin)

}
