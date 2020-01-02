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

package models

import generators.ModelGenerators
import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.Gen
import org.scalatest.FreeSpec
import org.scalatest.MustMatchers
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import play.api.libs.json._

class CustomsOfficeSpec extends FreeSpec with ScalaCheckPropertyChecks with ModelGenerators with MustMatchers {

  private val customsOffice = CustomsOffice("GB000074", "Immingham", Seq("TRA", "DEP", "DES"))

  "Customs Office model" - {
    "must deserialize from json to a valid model" in {
      val result = validCustomsOfficeJson(customsOffice).as[CustomsOffice]

      result mustBe customsOffice
    }

    "must serialize to json from valid json model" in {
      val result: JsValue = Json.toJson[CustomsOffice](customsOffice)

      result mustBe expectedCustomsOfficeJson()
    }

    "must serialize to json and deserialize to a valid model" in {
      forAll(arbitrary[CustomsOffice]) {
        customsOffice =>
          val json = validCustomsOfficeJson(customsOffice)
          json.as[CustomsOffice] mustBe customsOffice
      }
    }

    "must fail to deserialize" in {

      val invalidJsonGenerator: Gen[JsObject] = for {
        invalidKey   <- arbitrary[String]
        invalidValue <- arbitrary[String]
      } yield Json.obj(invalidKey -> invalidValue)

      forAll(invalidJsonGenerator) {
        invalidJson =>
          intercept[JsResultException](invalidJson.as[CustomsOffice])
      }
    }
  }

  def expectedCustomsOfficeJson(office: CustomsOffice = customsOffice): JsValue = Json.obj(
    "id"    -> office.id,
    "name"  -> office.name,
    "roles" -> Json.toJson(office.roles)
  )

  def validCustomsOfficeJson(office: CustomsOffice): JsValue = Json.parse(s"""
       |{
       |  "CUST_OFF_ID":"${office.id}",
       |  "CUST_OFF_NAM":"${office.name}",
       |  "CUSTOMS_OFFICE_ROLES":${Json.toJson(office.roles)}
       |  }
       |""".stripMargin)
}
