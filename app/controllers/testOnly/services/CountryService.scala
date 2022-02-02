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

import controllers.testOnly.testmodels.Country
import models.requests.CountryQueryFilter
import javax.inject.Inject
import play.api.Environment

private[testOnly] class CountryService @Inject() (override val env: Environment, config: ResourceConfig) extends ResourceService {

  val countries: Seq[Country] =
    getData[Country](config.countryCodes).sortBy(_.description)

  val nonEuCountries: Seq[Country] =
    getData[Country](config.nonEuCountryList).sortBy(_.description)

  def getCountryByCode(code: String): Option[Country] =
    getData[Country](config.countryCodes).find(_.code == code)

  def filterCountries(countryQueryFilter: CountryQueryFilter): Seq[Country] =
    getData[Country](config.countryCodes).filterNot(
      country => countryQueryFilter.excludeCountryCodes.contains(country.code)
    )

}
