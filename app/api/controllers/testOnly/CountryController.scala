/*
 * Copyright 2020 HM Revenue & Customs
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

package api.controllers.testOnly

import api.services.CountryService
import api.services.TransitCountryService
import data.DataRetrieval
import javax.inject.Inject
import models.CountryCodesFullList
import models.ReferenceDataList.Constants.CountryCodesFullListFieldNames
import play.api.libs.json.Json
import play.api.mvc.Action
import play.api.mvc.AnyContent
import play.api.mvc.ControllerComponents
import uk.gov.hmrc.play.bootstrap.controller.BackendController

import scala.concurrent.ExecutionContext

class CountryController @Inject() (
  cc: ControllerComponents,
  countryService: CountryService,
  transitCountryService: TransitCountryService
)(implicit ec: ExecutionContext)
    extends BackendController(cc) {

  def countriesFullList(): Action[AnyContent] =
    Action {
      Ok(Json.toJson(countryService.countries))
    }

  def transitCountries(): Action[AnyContent] =
    Action {
      Ok(Json.toJson(transitCountryService.transitCountryCodes))
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
