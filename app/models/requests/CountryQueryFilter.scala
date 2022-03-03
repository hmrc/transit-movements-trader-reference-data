/*
 * Copyright 2022 HM Revenue & Customs
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
import repositories.{Projection, Selector}

final case class CountryQueryFilter(
  customsOfficesRole: Option[CustomsOfficeRole],
  excludeCountryCodes: Seq[String],
  membership: Option[CountryMembership]
) {

  def queryParameters: (ReferenceDataList, Selector[ReferenceDataList], Option[Projection[ReferenceDataList]]) =
    this match {
      case CountryQueryFilter(None, Nil, None) => (CountryCodesFullList, Selector.All(), None)

      case CountryQueryFilter(Some(AnyCustomsOfficeRole), Nil, None) => (CountryCodesCustomsOfficeLists, Selector.All(), None)
      case CountryQueryFilter(None, codes, None)                     => (CountryCodesFullList, Selector.ExcludeCountriesCodes(codes), None)
      case CountryQueryFilter(None, Nil, Some(EuMember))             => (CountryCodesCommunityList, Selector.All(), None)
      case CountryQueryFilter(None, Nil, Some(CtcMember))            => (CountryCodesCommonTransitList, Selector.All(), None)
      case CountryQueryFilter(None, Nil, Some(NonEuMember))            => (CountryCodesCommonTransitOutsideCommunityList, Selector.All(), None)

      case CountryQueryFilter(Some(AnyCustomsOfficeRole), codes, None) => (CountryCodesCustomsOfficeLists, Selector.ExcludeCountriesCodes(codes), None)
      case CountryQueryFilter(Some(AnyCustomsOfficeRole), Nil, Some(NonEuMember)) =>
        (CountryCodesCommonTransitOutsideCommunityList, Selector.CountryMembershipQuery(NonEuMember), None)
      case CountryQueryFilter(Some(AnyCustomsOfficeRole), Nil, Some(membership)) =>
        (CountryCodesCustomsOfficeLists, Selector.CountryMembershipQuery(membership), None)

      case CountryQueryFilter(None, codes, Some(EuMember)) =>
        (CountryCodesCommunityList, Selector.ExcludeCountriesCodes(codes), None)
      case CountryQueryFilter(None, codes, Some(CtcMember)) =>
        (CountryCodesCommonTransitList, Selector.ExcludeCountriesCodes(codes), None)
      case CountryQueryFilter(None, codes, Some(NonEuMember)) =>
        (CountryCodesCommonTransitOutsideCommunityList, Selector.ExcludeCountriesCodes(codes), None)

      case CountryQueryFilter(Some(AnyCustomsOfficeRole), codes, Some(NonEuMember)) =>
        (CountryCodesCommonTransitOutsideCommunityList, Selector.ExcludeCountriesCodes(codes) and Selector.CountryMembershipQuery(NonEuMember), None)
      case CountryQueryFilter(Some(AnyCustomsOfficeRole), codes, Some(membership)) =>
        (CountryCodesCustomsOfficeLists, Selector.ExcludeCountriesCodes(codes) and Selector.CountryMembershipQuery(membership), None)
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
