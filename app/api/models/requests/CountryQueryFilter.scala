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
import CustomsOfficeRole._

final case class CountryQueryFilter(
  customsOfficesRole: Option[CustomsOfficeRole],
  excludeCountryCodes: Seq[String]
) {

  def queryParamters: (ReferenceDataList, Selector[ReferenceDataList], Option[Projection[ReferenceDataList]]) =
    this match {
      case CountryQueryFilter(Some(AnyCustomsOfficeRole), Nil)   => (CountryCodesCustomsOfficeLists, Selector.All(), None)
      case CountryQueryFilter(Some(AnyCustomsOfficeRole), codes) => (CountryCodesCustomsOfficeLists, Selector.excludeCountriesCodes(codes), None)
      case CountryQueryFilter(None, Nil)                         => (CountryCodesFullList, Selector.All(), None)
      case CountryQueryFilter(_, x)                              => (CountryCodesFullList, Selector.excludeCountriesCodes(x), None)
    }
}

object CountryQueryFilter {
  import CustomsOfficeRole._

  object FilterKeys {

    val customsOfficeRole: String = "customsOfficeRole"
    val exclude: String           = "exclude"

  }

  val noFilters: CountryQueryFilter = CountryQueryFilter(Some(CustomsOfficeRole.AnyCustomsOfficeRole), Seq.empty)

  implicit val queryStringBindableCountryQueryFilter: QueryStringBindable[CountryQueryFilter] =
    new QueryStringBindable[CountryQueryFilter] {

      def bind(key: String, params: Map[String, Seq[String]]): Option[Either[String, CountryQueryFilter]] =
        (
          Binders.bind[Option[CustomsOfficeRole]](None)(FilterKeys.customsOfficeRole, params),
          Binders.bind[Seq[String]](FilterKeys.exclude, params)
        ).mapN[Binders.BinderResult[CountryQueryFilter]] {
          case (None, Nil)                       => Binders.failed
          case (Some(AnyCustomsOfficeRole), Nil) => Binders.successful(CountryQueryFilter(Some(AnyCustomsOfficeRole), Nil))
          case (role, excludesCountryCodes)      => Binders.successful(CountryQueryFilter(role, excludesCountryCodes))
        }.flatten
          .value

      def unbind(key: String, value: CountryQueryFilter): String =
        Seq(
          Binders.unbind(FilterKeys.customsOfficeRole, value.customsOfficesRole),
          Binders.unbind(FilterKeys.exclude, value.excludeCountryCodes)
        ).filterNot(_.isEmpty).mkString("&")

    }

}
