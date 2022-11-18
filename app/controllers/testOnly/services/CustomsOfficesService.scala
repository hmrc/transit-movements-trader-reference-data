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

import controllers.testOnly.helpers.P5
import controllers.testOnly.helpers.Version
import controllers.testOnly.helpers.VersionHelper
import controllers.testOnly.testmodels.CustomsOffice
import play.api.Environment
import play.api.libs.json.Json

import javax.inject.Inject

private[testOnly] class CustomsOfficesService @Inject() (override val env: Environment, config: ResourceConfig) extends ResourceService {

  val customsOffices: Seq[CustomsOffice] =
    getData[CustomsOffice](config.customsOffice)

  val customsOfficesP5: Seq[CustomsOffice] =
    getData[CustomsOffice](config.customsOfficeP5)

  def getCustomsOffice(officeId: String): Option[CustomsOffice] =
    customsOffices.find(_.id == officeId)

  def getCustomsOfficesOfTheCountry(countryId: String, roles: List[String], version: Option[Version] = None) =
    version.fold(getCustomsOfficesOfTheCountryExcluding(countryId, excludedRoles = roles))(
      v =>
        if (v == P5)
          getCustomsOfficesForCountryRoles(countryId, roles)
        else
          getCustomsOfficesOfTheCountryExcluding(countryId, excludedRoles = roles)
    )

  private def getCustomsOfficesOfTheCountryExcluding(countryId: String, excludedRoles: List[String]): Seq[CustomsOffice] =
    customsOffices
      .filter(_.countryId == countryId)
      .filter {
        x =>
          (countryId, excludedRoles) match {
            case ("SM", List("TRA")) => !x.roles.contains("TRA")
            case _                   => true
          }
      }

  private def getCustomsOfficesForCountryRoles(countryId: String, roles: List[String]): Seq[CustomsOffice] =
    customsOfficesP5.filter(_.countryId == countryId).filter(_.roles.exists(roles.contains _))

}
