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
import controllers.testOnly.helpers.Version
import controllers.testOnly.helpers.VersionHelper
import controllers.testOnly.services.CountryService

import javax.inject.Inject
import play.api.libs.json.Json
import play.api.mvc.Action
import play.api.mvc.AnyContent
import play.api.mvc.ControllerComponents
import uk.gov.hmrc.play.bootstrap.backend.controller.BackendController
import models.requests.CountryQueryFilter

class CountryController @Inject() (
  cc: ControllerComponents,
  countryService: CountryService
) extends BackendController(cc) {

  def get(countryQueryFilter: CountryQueryFilter): Action[AnyContent] =
    Action {
      request =>
        val version: Option[Version] = VersionHelper.getVersion(request)
        Ok(Json.toJson(countryService.filterCountries(countryQueryFilter, version)))
    }

  def getCountryCustomsOfficeSecurityAgreementArea(): Action[AnyContent] =
    Action {
      request =>
        val version: Option[Version] = VersionHelper.getVersion(request)

        version.fold(NoContent)(
          v =>
            if (v == P5)
              Ok(Json.toJson(countryService.countryCustomsOfficeSecurityAgreementArea))
            else
              NoContent
        )

    }

  def getCountry(code: String): Action[AnyContent] =
    Action {
      countryService
        .getCountryByCode(code)
        .map {
          country =>
            Ok(Json.toJson(country))
        }
        .getOrElse {
          NotFound
        }
    }
}
