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

package controllers.testOnly.services

import controllers.testOnly.Version
import controllers.testOnly.Version2
import controllers.testOnly.testmodels.Country
import models.requests.CountryMembership.CtcMember
import models.requests.CountryMembership.EuMember
import models.requests.CountryMembership.NonEuMember
import models.requests.CountryMembership
import models.requests.CountryQueryFilter
import play.api.Environment

import javax.inject.Inject

private[testOnly] class CountryService @Inject() (override val env: Environment, config: ResourceConfig) extends ResourceService {

  def getCountryByCode(code: String): Option[Country] =
    getData[Country](config.countryCodes).find(_.code == code)

  def filterCountries(countryQueryFilter: CountryQueryFilter, version: Option[Version] = None): Seq[Country] = {

    val resource = version.fold(config.countryCodes)(
      v =>
        if (v == Version2)
          checkMembership(countryQueryFilter.membership)
        else
          config.countryCodes
    )

    getData[Country](resource).filterNot(
      country => countryQueryFilter.excludeCountryCodes.contains(country.code)
    )
  }

  // TODO - Do we need any more filtering? I.e. like member etc?
  val countryCustomsOfficeSecurityAgreementArea: Seq[Country] =
    getData[Country](config.countryCustomsOfficeSecurityAgreementArea)

  private def checkMembership(membership: Option[CountryMembership]): String =
    membership match {
      case None              => config.countryCodesV2
      case Some(CtcMember)   => config.countryCodesCommonTransitList
      case Some(EuMember)    => config.countryCodesCommunityList
      case Some(NonEuMember) => config.countryCodesCommonTransitOutsideCommunityList
    }
}
