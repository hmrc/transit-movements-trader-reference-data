/*
 * Copyright 2019 HM Revenue & Customs
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
import org.scalatest.FreeSpec
import org.scalatest.MustMatchers
import org.scalatestplus.scalacheck.ScalaCheckDrivenPropertyChecks
import play.api.libs.json.JsError
import play.api.libs.json.JsPath
import play.api.libs.json.Json
import play.api.libs.json.JsonValidationError

class CountryCodesSpec extends FreeSpec with MustMatchers with ScalaCheckDrivenPropertyChecks with ModelGenerators {

  "CountryCodes" - {

    "must serialize to json" in {
      json.as[CountryCodes] mustBe countryCodes
    }

    "must deserialize to valid model" in {
      Json.toJson(countryCodes) mustBe json
    }

    "must serialise and deserialize to and from json" in {

      forAll(arbitrary[CountryCodes]) {
        countryCodes =>
          val json = Json.toJson(countryCodes)
          json.as[CountryCodes] mustBe countryCodes
      }
    }

    "must fail to deserialize for invalid json" in {
      Json.fromJson[CountryCodes](Json.obj()) must
        be(JsError(List((JsPath \ "countryCodes", List(JsonValidationError(List("error.path.missing")))))))
    }
  }

  val countryCodes = CountryCodes(List(CountryCode("valid", "GB", "United Kingdom"), CountryCode("valid", "AD", "Andorra")))

  val json = Json.parse("""
      |{"countryCodes":[
      |    {
      |      "code":"GB",
      |      "state":"valid",
      |      "description":"United Kingdom"
      |    },
      |    {
      |      "code":"AD",
      |      "state":"valid",
      |      "description":"Andorra"
      |    }]
      |}""".stripMargin)

}
