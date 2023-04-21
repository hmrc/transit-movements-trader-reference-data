/*
 * Copyright 2023 HM Revenue & Customs
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

import controllers.testOnly.services.ControlTypeService
import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent, ControllerComponents}

import javax.inject.Inject

class ControlTypeController @Inject()(
  cc: ControllerComponents,
  controlTypeService: ControlTypeService
) extends ReferenceDataController(cc) {

  def getControlTypes: Action[AnyContent] =
    Action {
      val controlTypes = controlTypeService.getControlResults
      Ok(Json.toJson(controlTypes))
    }
}
