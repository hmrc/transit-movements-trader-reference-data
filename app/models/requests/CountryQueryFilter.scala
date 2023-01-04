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

package models.requests

import cats.implicits._
import models._
import models.requests.CountryMembership._
import models.requests.CustomsOfficeRole._
import play.api.mvc.QueryStringBindable
import repositories.Projection
import repositories.Selector

final case class CountryQueryFilter(
  customsOfficesRole: Option[CustomsOfficeRole],
  excludeCountryCodes: Seq[String],
  membership: Option[CountryMembership]
) {

  def queryParameters: (ReferenceDataList, Selector[ReferenceDataList], Option[Projection[ReferenceDataList]]) =
    this match {
      case CountryQueryFilter(None, codes, membership)    => (referenceDataList(membership), countriesSelector(codes), None)
      case CountryQueryFilter(Some(_), codes, membership) => (CountryCodesCustomsOfficeLists, customsOfficesSelector(codes, membership), None)
    }

  private def countriesSelector(codes: Seq[String]): Selector[ReferenceDataList] =
    codes match {
      case Nil => Selector.All()
      case _   => Selector.ExcludeCountriesCodes(codes)
    }

  private def customsOfficesSelector(codes: Seq[String], membership: Option[CountryMembership]): Selector[ReferenceDataList] =
    (codes, membership) match {
      case (_, None)               => countriesSelector(codes)
      case (Nil, Some(membership)) => Selector.CountryMembershipQuery(membership)
      case (_, Some(membership))   => Selector.ExcludeCountriesCodes(codes) and Selector.CountryMembershipQuery(membership)
    }

  private def referenceDataList(membership: Option[CountryMembership]): ReferenceDataList =
    membership match {
      case None              => CountryCodesFullList
      case Some(CtcMember)   => CountryCodesCommonTransitList
      case Some(EuMember)    => CountryCodesCommunityList
      case Some(NonEuMember) => CountryCodesCommonTransitOutsideCommunityList
    }
}

object CountryQueryFilter {

  object FilterKeys {

    val customsOfficeRole: String = "customsOfficeRole"
    val exclude: String           = "exclude"
    val membership: String        = "membership"

  }

  implicit val queryStringBindableCountryQueryFilter: QueryStringBindable[CountryQueryFilter] =
    new QueryStringBindable[CountryQueryFilter] {

      def bind(key: String, params: Map[String, Seq[String]]): Option[Either[String, CountryQueryFilter]] =
        (
          Binders.bind[Option[CustomsOfficeRole]](None)(FilterKeys.customsOfficeRole, params),
          Binders.bind[Seq[String]](FilterKeys.exclude, params),
          Binders.bind[Option[CountryMembership]](None)(FilterKeys.membership, params)
        ).mapN(CountryQueryFilter.apply).value

      def unbind(key: String, value: CountryQueryFilter): String =
        Seq(
          Binders.unbind("", value.customsOfficesRole),
          Binders.unbind("", value.membership),
          Binders.unbind(FilterKeys.exclude, value.excludeCountryCodes)
        ).filterNot(_.isEmpty).mkString("&")

    }

}
