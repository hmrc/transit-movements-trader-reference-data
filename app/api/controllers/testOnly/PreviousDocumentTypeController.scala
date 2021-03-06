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

import api.controllers.testOnly.services.PreviousDocumentTypeService
import javax.inject.Inject
import play.api.libs.json.Json
import play.api.mvc.Action
import play.api.mvc.AnyContent
import play.api.mvc.ControllerComponents
import uk.gov.hmrc.play.bootstrap.controller.BackendController

class PreviousDocumentTypeController @Inject() (
  cc: ControllerComponents,
  previousDocumentTypeService: PreviousDocumentTypeService
) extends BackendController(cc) {

  def previousDocumentTypes(): Action[AnyContent] =
    Action {
      Ok(Json.toJson(previousDocumentTypeService.previousDocumentTypes))
    }

  def getPreviousDocumentType(code: String): Action[AnyContent] =
    Action {

      previousDocumentTypeService
        .getPreviousDocumentTypeByCode(code)
        .map {
          documentType =>
            Ok(Json.toJson(documentType))
        }
        .getOrElse {
          NotFound
        }
    }
}
