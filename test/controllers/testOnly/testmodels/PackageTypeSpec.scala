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
import controllers.testOnly.testmodels.PackageType._
import play.api.libs.json.JsString
import play.api.libs.json.Json

class PackageTypeSpec extends SpecBase {

  "must deserialise" - {

    "when bulk" in {
      val json   = JsString("Bulk")
      val result = json.as[PackageType]
      result mustBe Bulk
    }

    "when unpacked" in {
      val json   = JsString("Unpacked")
      val result = json.as[PackageType]
      result mustBe Unpacked
    }

    "when other" in {
      val json   = JsString("Other")
      val result = json.as[PackageType]
      result mustBe Other
    }
  }

  "must serialise" - {
    "when bulk" in {
      val packageType: PackageType = Bulk
      val result                   = Json.toJson(packageType)
      result mustBe JsString("Bulk")
    }

    "when unpacked" in {
      val packageType: PackageType = Unpacked
      val result                   = Json.toJson(packageType)
      result mustBe JsString("Unpacked")
    }

    "when other" in {
      val packageType: PackageType = Other
      val result                   = Json.toJson(packageType)
      result mustBe JsString("Other")
    }
  }

}
