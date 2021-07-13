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

import api.controllers.testOnly.services.OfficeOfTransitService
import javax.inject.Inject
import play.api.libs.json.Json
import play.api.mvc.Action
import play.api.mvc.AnyContent
import play.api.mvc.ControllerComponents
import uk.gov.hmrc.play.bootstrap.controller.BackendController

class OfficeOfTransitController @Inject() (officeOfTransitService: OfficeOfTransitService, cc: ControllerComponents) extends BackendController(cc) {

  def officesOfTransit(): Action[AnyContent] =
    Action {

      Ok(Json.toJson(officeOfTransitService.officesOfTransit))
    }

  def getOfficeOfTransit(id: String): Action[AnyContent] =
    Action {

      officeOfTransitService
        .getOfficeOfTransit(id)
        .map {
          officesOfTransit =>
            Ok(Json.toJson(officesOfTransit))
        }
        .getOrElse {
          NotFound
        }
    }
}
