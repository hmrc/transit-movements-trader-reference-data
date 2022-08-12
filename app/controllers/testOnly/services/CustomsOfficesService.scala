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
import play.api.Environment

import javax.inject.Inject

private[testOnly] class CustomsOfficesService @Inject() (override val env: Environment, config: ResourceConfig) extends ResourceService {

  val customsOffices: Seq[CustomsOffice] =
    getData[CustomsOffice](config.customsOffice)

  def customsOfficeTransit(code: String): Seq[CustomsOfficeP5] =
    getData[CustomsOfficeP5](config.customsOfficeTransit).filter(_.getCountryCode() == code)

  def customsOfficeDestination(code: String): Seq[CustomsOfficeP5] =
    getData[CustomsOfficeP5](config.customsOfficeDestination).filter(_.getCountryCode() == code)

  def customsOfficeExit(code: String): Seq[CustomsOfficeP5] =
    getData[CustomsOfficeP5](config.customsOfficeExit).filter(_.getCountryCode() == code)

  def customsOfficeTransitExit(code: String): Seq[CustomsOfficeP5] =
    getData[CustomsOfficeP5](config.customsOfficeTransitExit).filter(_.getCountryCode() == code)

  def customsOfficeDeparture(countryId: String): Seq[CustomsOfficeP5] =
    getData[CustomsOfficeP5](config.customsOfficeDeparture).filter(_.getCountryCode() == countryId)

  def getCustomsOffice(officeId: String): Option[CustomsOffice] =
    customsOffices.find(_.id == officeId)

  def getCustomsOfficesOfTheCountry(countryId: String, excludedRoles: List[String]): Seq[CustomsOffice] =
    customsOffices
      .filter(_.countryId == countryId)
      .filter {
        x =>
          (countryId, excludedRoles) match {
            case ("SM", List("TRA")) => !x.roles.contains("TRA")
            case _                   => true
          }
      }
}
