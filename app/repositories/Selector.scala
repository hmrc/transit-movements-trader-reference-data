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

import models._
import play.api.libs.json.JsObject
import play.api.libs.json.Json
import cats._
import cats.data._
import api.models.requests._

trait Selector[+A] {
  self =>
  def expression: JsObject

  def and[B >: A](other: Selector[B]): Selector[B] =
    new CompoundSelector[B](NonEmptyList.of(this, other))

}

final class CompoundSelector[A](selectors: NonEmptyList[Selector[A]]) extends Selector[A] {
  implicit val monodJsObject = Monoid.instance[JsObject](JsObject.empty, (x, y) => x ++ y)

  final override def expression: JsObject =
    Monoid.combineAll(selectors.toList.map(_.expression))

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

  case class ById(id: String) extends Selector[ReferenceDataList] {

    val expression: JsObject = Json.obj("id" -> id)
  }

  case class ByCode(code: String) extends Selector[ReferenceDataList] {

    val expression: JsObject = Json.obj("code" -> code)
  }

  case class ExcludeCountriesCodes(countryCodes: Seq[String]) extends Selector[CountryCodesCustomsOfficeLists.type] {

    override def expression: JsObject =
      Json.obj(
        "code" ->
          Json.obj(
            "$nin" -> countryCodes
          )
      )
  }

  case class CountryMembershipQuery(membership: CountryMembership*) extends Selector[CountryCodesFullList.type] {

    val query = membership.toList match {
      case head :: Nil =>
        Json.obj(
          "$eq" -> head.dbValue
        )
      case lst =>
        Json.obj(
          "$in" -> lst.map(_.dbValue)
        )
    }

    override def expression: JsObject =
      Json.obj(
        "countryRegimeCode" -> query
      )
  }
}
