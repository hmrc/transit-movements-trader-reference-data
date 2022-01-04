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

package controllers.consumption

import javax.inject.Inject
import scala.concurrent.ExecutionContext
import play.api.mvc.ControllerComponents
import play.api.mvc.Action
import models.CountryCodesCommunityList
import uk.gov.hmrc.play.bootstrap.backend.controller.BackendController
import play.api.libs.json.Json
import services.ReferenceDataService
import logging.Logging
import play.api.mvc.AnyContent
import repositories.Selector
import repositories.Projection

class EuCountriesController @Inject() (
  cc: ControllerComponents,
  referenceDataService: ReferenceDataService
)(implicit ec: ExecutionContext)
    extends BackendController(cc)
    with Logging {

  def getEUCountries(): Action[AnyContent] =
    Action.async {
      referenceDataService.many(CountryCodesCommunityList, Selector.All(), Projection.SuppressId.toOption).map {
        case Nil =>
          logger.error(s"[getEUCountries] No data found for ${CountryCodesCommunityList.listName}")
          NotFound
        case countries => Ok(Json.toJson(countries))
      }
    }
}
