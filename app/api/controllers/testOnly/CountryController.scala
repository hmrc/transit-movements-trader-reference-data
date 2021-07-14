/*
 * Copyright 2021 HM Revenue & Customs
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

import api.controllers.testOnly.services.CountryService
import api.controllers.testOnly.services.TransitCountryService
import javax.inject.Inject
import play.api.libs.json.Json
import play.api.mvc.Action
import play.api.mvc.AnyContent
import play.api.mvc.ControllerComponents
import uk.gov.hmrc.play.bootstrap.controller.BackendController
import api.models.requests.CountryQueryFilter

class CountryController @Inject() (
  cc: ControllerComponents,
  countryService: CountryService,
  transitCountryService: TransitCountryService
) extends BackendController(cc) {

  def get(countryQueryFilter: CountryQueryFilter): Action[AnyContent] =
    Action {
      countryQueryFilter match {
        case CountryQueryFilter(true) => Ok(Json.toJson(countryService.countries))
        case CountryQueryFilter(false) =>
          val customsOffices = Seq(
            api.models.Country("active", "GB", "United Kingdom (GB)"),
            api.models.Country("active", "IT", "Italy"),
            api.models.Country("active", "FR", "France"),
            api.models.Country("active", "XI", "United Kingdom (NI)")
          )

          Ok(Json.toJson(customsOffices))

      }
    }

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
