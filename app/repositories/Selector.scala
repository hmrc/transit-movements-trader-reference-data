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

package repositories

import models._
import models.requests._
import org.bson.conversions.Bson
import org.mongodb.scala.bson.BsonDocument
import org.mongodb.scala.model.Filters

trait Selector[+A] {
  self =>

  def expression: Bson

  def and[B >: A](other: Selector[B]): Selector[B] =
    new CompoundSelector[B](self :: other :: Nil)
}

final class CompoundSelector[A](selectors: Seq[Selector[A]]) extends Selector[A] {
  override def expression: Bson = Filters.and(selectors.map(_.expression): _*)
}

object Selector {

  implicit class SingleImportSelector[A](selector: Selector[A]) {

    def forImport(importId: ImportId): Selector[A] =
      new Selector[A] {

        val expression: Bson = Filters.and(
          Filters.eq("importId", importId.value),
          selector.expression
        )
      }
  }

  case class All() extends Selector[ReferenceDataList] {

    override val expression: Bson = BsonDocument()
  }

  case class OptionallyByRole(roles: Seq[String]) extends Selector[CustomsOfficesList.type] {

    override val expression: Bson = roles.map(_.toUpperCase) match {
      case Nil    => BsonDocument()
      case uRoles => Filters.all("roles.role", uRoles: _*)
    }
  }

  case class ByCountry(countryId: String) extends Selector[CustomsOfficesList.type] {

    override val expression: Bson = Filters.eq("countryId", countryId)
  }

  case class ById(id: String) extends Selector[ReferenceDataList] {

    override val expression: Bson = Filters.eq("id", id)
  }

  case class ByCode(code: String) extends Selector[ReferenceDataList] {

    override val expression: Bson = Filters.eq("code", code)
  }

  case class ExcludeCountriesCodes(countryCodes: Seq[String]) extends Selector[CountryCodesCustomsOfficeLists.type] {

    override val expression: Bson = Filters.nin("code", countryCodes: _*)
  }

  case class CountryMembershipQuery(membership: CountryMembership) extends Selector[CountryCodesFullList.type] {

    override val expression: Bson = Filters.in("countryRegimeCode", membership.dbValues: _*)
  }
}
