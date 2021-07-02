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

package repositories

import models.CustomsOfficesList
import models.ReferenceDataList
import play.api.libs.json.JsObject
import play.api.libs.json.Json

trait Selector[A] {
  self =>
  val expression: JsObject

  def and[B <: A](other: Selector[B]): Selector[B] =
    new Selector[B] {
      val expression: JsObject = self.expression ++ other.expression
    }
}

object Selector {

  implicit class SingleImportSelector[A](selector: Selector[A]) {

    def forImport(importId: ImportId): Selector[A] =
      new Selector[A] {

        val expression: JsObject =
          Json.obj("importId" -> importId) ++ selector.expression
      }
  }

  case class All() extends Selector[ReferenceDataList] {

    val expression: JsObject =
      Json.obj()
  }

  case class OptionallyByRole(roles: Seq[String]) extends Selector[CustomsOfficesList.type] {

    val expression: JsObject = roles.map(_.toUpperCase) match {
      case Nil    => Json.obj()
      case uRoles => Json.obj("roles.role" -> Json.obj("$all" -> uRoles))
    }
  }

  case class ByCountry(countryId: String) extends Selector[CustomsOfficesList.type] {

    val expression: JsObject =
      Json.obj("countryId" -> countryId)
  }

  // TODO: Type reference data lists to restrict this to those which make sense
  case class ById(id: String) extends Selector[ReferenceDataList] {

    val expression: JsObject = Json.obj("id" -> id)
  }

  case class ByCode(code: String) extends Selector[ReferenceDataList] {

    val expression: JsObject = Json.obj("code" -> code)
  }
}
