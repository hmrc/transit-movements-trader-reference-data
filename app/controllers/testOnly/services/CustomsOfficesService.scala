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

import controllers.testOnly.testmodels.CustomsOffice
import controllers.testOnly.testmodels.CustomsOfficeP5
import javax.inject.Inject
import play.api.Environment

private[testOnly] class CustomsOfficesService @Inject() (override val env: Environment, config: ResourceConfig) extends ResourceService {

  val customsOffices: Seq[CustomsOffice] =
    getData[CustomsOffice](config.customsOffice).sortBy(_.name)

  def customsOfficeTransit(code: String): Seq[CustomsOfficeP5] =
    getData[CustomsOfficeP5](config.customsOfficeTransit).filter(_.getCountryCode() == code).sortBy(_.name)

  def customsOfficeDestination(code: String): Seq[CustomsOfficeP5] =
    getData[CustomsOfficeP5](config.customsOfficeDestination).filter(_.getCountryCode() == code).sortBy(_.name)

  def customsOfficeExit(code: String): Seq[CustomsOfficeP5] =
    getData[CustomsOfficeP5](config.customsOfficeExit).filter(_.getCountryCode() == code).sortBy(_.name)

  def customsOfficeTransitExit(code: String): Seq[CustomsOfficeP5] =
    getData[CustomsOfficeP5](config.customsOfficeTransitExit).filter(_.getCountryCode() == code).sortBy(_.name)

  def getCustomsOffice(officeId: String): Option[CustomsOffice] =
    getData[CustomsOffice](config.customsOffice).find(_.id == officeId)

  def customsOfficeDeparture(countryId: String): Seq[CustomsOfficeP5] =
    getData[CustomsOfficeP5](config.customsOfficeDeparture).filter(_.getCountryCode() == countryId).sortBy(_.name)

  def getCustomsOfficesOfTheCountry(countryId: String, excludedRoles: List[String]): Seq[CustomsOffice] =
    (countryId, excludedRoles) match {
      case ("SM", List("TRA")) =>
        getData[CustomsOffice](config.customsOffice)
          .filter(
            x => x.countryId == countryId && !x.roles.contains("TRA")
          )
          .sortBy(_.name)
      case _ => getData[CustomsOffice](config.customsOffice).filter(_.countryId == countryId).sortBy(_.name)
    }
}
