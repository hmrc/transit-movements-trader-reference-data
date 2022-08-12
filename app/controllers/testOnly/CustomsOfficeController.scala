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

package controllers.testOnly

import controllers.testOnly.helpers.P5
import controllers.testOnly.helpers.VersionHelper
import controllers.testOnly.services.CustomsOfficesService
import controllers.testOnly.testmodels.CustomsOfficeP5
import play.api.libs.json.Json
import play.api.mvc.Action
import play.api.mvc.AnyContent
import play.api.mvc.ControllerComponents
import uk.gov.hmrc.play.bootstrap.backend.controller.BackendController

import javax.inject.Inject

class CustomsOfficeController @Inject() (
  cc: ControllerComponents,
  customsOfficesService: CustomsOfficesService
) extends BackendController(cc) {

  def customsOfficeTransit(code: String): Action[AnyContent] = get {
    customsOfficesService.customsOfficeTransit(code)
  }

  def customsOfficeDestination(code: String): Action[AnyContent] = get {
    customsOfficesService.customsOfficeDestination(code)
  }

  def customsOfficeDeparture(code: String): Action[AnyContent] = get {
    customsOfficesService.customsOfficeDeparture(code)
  }

  def customsOfficeExit(code: String): Action[AnyContent] = get {
    customsOfficesService.customsOfficeExit(code)
  }

  def customsOfficeTransitExit(code: String): Action[AnyContent] = get {
    customsOfficesService.customsOfficeTransitExit(code)
  }

  private def get(offices: Seq[CustomsOfficeP5]): Action[AnyContent] =
    Action {
      request =>
        VersionHelper.getVersion(request) match {
          case Some(P5) => Ok(Json.toJson(offices))
          case _        => NoContent
        }
    }

  def customsOffices(): Action[AnyContent] =
    Action {
      Ok(Json.toJson(customsOfficesService.customsOffices))
    }

  def customsOfficesOfTheCountry(countryCode: String, excludedRoles: List[String]): Action[AnyContent] =
    Action {
      Ok(Json.toJson(customsOfficesService.getCustomsOfficesOfTheCountry(countryCode, excludedRoles)))
    }

  def getCustomsOffice(officeId: String): Action[AnyContent] =
    Action {
      customsOfficesService
        .getCustomsOffice(officeId)
        .map {
          customsOffice =>
            Ok(Json.toJson(customsOffice))
        }
        .getOrElse {
          NotFound
        }
    }
}
