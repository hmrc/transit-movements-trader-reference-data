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

import api.controllers.ControlResultController
import api.services.ControlResultService
import play.api.libs.json.Json
import play.api.mvc.Action
import play.api.mvc.AnyContent
import play.api.mvc.ControllerComponents
import uk.gov.hmrc.play.bootstrap.backend.controller.BackendController

import javax.inject.Inject
import scala.concurrent.ExecutionContext

class ControlResultTestController @Inject() (
  cc: ControllerComponents,
  controlResultService: ControlResultService
)(implicit ec: ExecutionContext)
    extends BackendController(cc)
    with ControlResultController {

  override def getAll(): Action[AnyContent] =
    Action {
      Ok(Json.toJson(controlResultService.controlResults))
    }

  override def getControlResult(code: String): Action[AnyContent] =
    Action {
      Ok(Json.toJson(controlResultService.getControlResultByCode(code)))
    }
}
