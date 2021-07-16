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

package api.models.requests

import play.api.mvc.QueryStringBindable
import cats.implicits._
import models.ReferenceDataList
import repositories.Selector
import repositories.Projection
import models.CountryCodesFullList
import models.CountryCodesCustomsOfficeLists

final case class CountryQueryFilter(
  customsOffices: Boolean,
  excludeCountries: Seq[String]
) {

  def queryParamters: Option[(ReferenceDataList, Selector[ReferenceDataList], Option[Projection[ReferenceDataList]])] =
    this match {
      case CountryQueryFilter(true, Nil) => Some((CountryCodesCustomsOfficeLists, Selector.All(), None))
      case CountryQueryFilter(true, x)   => Some((CountryCodesCustomsOfficeLists, Selector.excludeCountriesCodes(x), None))
      case CountryQueryFilter(false, _)  => Some((CountryCodesFullList, Selector.All(), None))
    }
}

object CountryQueryFilter {

  object FilterKeys {

    val customsOffice: String    = "customsOffice"
    val excludeCountries: String = "excludeCountries"

  }

  val noFilters: CountryQueryFilter = CountryQueryFilter(false, Seq.empty)

  implicit val queryStringBindableCountryQueryFilter: QueryStringBindable[CountryQueryFilter] =
    new QueryStringBindable[CountryQueryFilter] {

      def bind(key: String, params: Map[String, Seq[String]]): Option[Either[String, CountryQueryFilter]] = (
        Binders.bind[Boolean](false)(FilterKeys.customsOffice, params),
        Binders.bind[Seq[String]](Seq.empty)(FilterKeys.excludeCountries, params)
      ).mapN(CountryQueryFilter.apply).value

      def unbind(key: String, value: CountryQueryFilter): String =
        key match {
          case x if x == FilterKeys.customsOffice    => QueryStringBindable.bindableBoolean.unbind(FilterKeys.customsOffice, value.customsOffices)
          case x if x == FilterKeys.excludeCountries => QueryStringBindable.bindableSeq[String].unbind(FilterKeys.excludeCountries, value.excludeCountries)
          case _                                     => ""
        }

    }

}