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

import base.SpecBase
import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.Gen
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import play.api.libs.json.JsError
import play.api.libs.json.JsString
import play.api.libs.json.JsSuccess
import play.api.libs.json.Json
import play.api.mvc.PathBindable

class ReferenceDataListSpec extends SpecBase with ScalaCheckPropertyChecks {

  "pathBindable" - {
    "given a valid list name will return the ReferenceDataList for that list name" in {
      forAll(Gen.oneOf(ReferenceDataList.values.toList)) {
        testList =>
          val result: Either[String, ReferenceDataList] = implicitly[PathBindable[ReferenceDataList]].bind("", testList.listName)

          result.right.value mustEqual testList

      }

    }

    "given an invalid list name will return a left with an error message" in {
      val invalidTestList = "invalidTestList"

      val result: Either[String, ReferenceDataList] = implicitly[PathBindable[ReferenceDataList]].bind("", invalidTestList)

      result.left.value.contains("Unknown reference data list name") mustEqual true
      result.left.value.contains(invalidTestList) mustEqual true
    }
  }

  "writes" - {

    "must write a JsString with the list's name" in {

      forAll(Gen.oneOf(ReferenceDataList.values.toList)) {
        list =>
          Json.toJson(list) mustEqual JsString(list.listName)
      }
    }
  }

  "reads" - {

    "must read a JsString of the list's name" in {

      forAll(Gen.oneOf(ReferenceDataList.values.toList)) {
        list =>
          JsString(list.listName).validate[ReferenceDataList] mustEqual JsSuccess(list)
      }
    }

    "must fail for any string that isn't a valid list name" in {

      forAll(arbitrary[String] suchThat (s => !ReferenceDataList.values.toList.map(_.listName).contains(s))) {
        string =>
          JsString(string).validate[ReferenceDataList] mustBe a[JsError]
      }
    }
  }
}
