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

package controllers.testOnly.testmodels

import base.ModelGenerators
import base.SpecBase
import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.Gen
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import play.api.libs.json._

class DangerousGoodsCodeSpec extends SpecBase with ScalaCheckPropertyChecks with ModelGenerators {

  private val dangerousGoodsCode = DangerousGoodsCode("0004", "AMMONIUM PICRATE dry or wetted with less than 10% water, by mass")

  "Transit Office model" - {

    "must deserialise from json to a valid model" in {

      val result = validDangerousGoodsCodeJson(dangerousGoodsCode).as[DangerousGoodsCode]

      result mustBe dangerousGoodsCode
    }

    "must serialize to json from valid json model" in {
      val result = Json.toJson(dangerousGoodsCode)
      result mustBe expectedDangerousGoodsCodeJson()
    }

    "must serialize to json and deserialize to a valid model" in {
      forAll(arbitrary[DangerousGoodsCode]) {
        dangerousGoodsCode =>
          val json = validDangerousGoodsCodeJson(dangerousGoodsCode)
          json.as[DangerousGoodsCode] mustBe dangerousGoodsCode
      }
    }

    "must fail to deserialize" in {

      val invalidJsonGenerator: Gen[JsObject] = for {
        invalidKey   <- arbitrary[String]
        invalidValue <- arbitrary[String]
      } yield Json.obj(invalidKey -> invalidValue)

      forAll(invalidJsonGenerator) {
        invalidJson =>
          intercept[JsResultException](invalidJson.as[DangerousGoodsCode])
      }
    }
  }

  def expectedDangerousGoodsCodeJson(dangerousGoodsCode: DangerousGoodsCode = dangerousGoodsCode): JsValue =
    Json.obj(
      "code"        -> dangerousGoodsCode.code,
      "description" -> dangerousGoodsCode.description
    )

  def validDangerousGoodsCodeJson(dangerousGoodsCode: DangerousGoodsCode): JsValue =
    Json.parse(s"""
                  |{
                  |  "code":"${dangerousGoodsCode.code}",
                  |  "description":"${dangerousGoodsCode.description}"
                  |  }
                  |""".stripMargin)

}
