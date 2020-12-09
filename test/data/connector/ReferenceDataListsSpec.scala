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

package data.connector

import base.SpecBase
import models.CircumstanceIndicatorList
import play.api.libs.json.Json

class ReferenceDataListsSpec extends SpecBase {

  "should read all known lists when there are only known lists" in {

    val testObject =
      Json.obj(
        "_self"                            -> Json.obj("href" -> "self/path"),
        CircumstanceIndicatorList.listName -> Json.obj("href" -> "CircumstanceIndicatorList/path")
      )

    val expected = ReferenceDataLists(
      Map(
        CircumstanceIndicatorList -> "CircumstanceIndicatorList/path"
      )
    )

    val result = testObject.as[ReferenceDataLists]

    result mustEqual expected
  }

  "should read all known lists when there are known lists" in {

    val testObject =
      Json.obj(
        "_self"                            -> Json.obj("href" -> "self/path"),
        CircumstanceIndicatorList.listName -> Json.obj("href" -> "CircumstanceIndicatorList/path"),
        "UnknownListName"                  -> Json.obj("href" -> "UnknownListName/path")
      )

    val expected = ReferenceDataLists(
      Map(
        CircumstanceIndicatorList -> "CircumstanceIndicatorList/path"
      )
    )

    val result = testObject.as[ReferenceDataLists]

    result mustEqual expected

  }

}
