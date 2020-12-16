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

package repositories

import models.CustomsOfficesList
import models.ReferenceDataList
import play.api.libs.json.JsObject
import play.api.libs.json.Json

// TODO *if needed* - this could have a projection if we find the get methods need one
// TODO: Should we make this sealed?
trait Selector[A] {
  val importId: ImportId
  protected val expression: JsObject

  final private val baseExpression: JsObject =
    Json.obj("importId" -> Json.toJson(importId))

  final lazy val fullExpression: JsObject =
    baseExpression ++ expression
}

// TODO: Create more selectors
object Selector {

  case class All(importId: ImportId) extends Selector[ReferenceDataList] {

    protected val expression: JsObject =
      Json.obj()
  }

  case class ByCountry(importId: ImportId, countryCode: String) extends Selector[CustomsOfficesList.type] {

    protected val expression: JsObject =
      Json.obj("countryCode" -> countryCode)
  }

  case class ByCustomsOfficeId(importId: ImportId, officeId: String) extends Selector[CustomsOfficesList.type] {

    protected val expression: JsObject =
      Json.obj("officeId" -> officeId)
  }
}
