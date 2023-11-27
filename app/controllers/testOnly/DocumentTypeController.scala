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

import controllers.testOnly.services._
import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent, ControllerComponents}

import javax.inject.Inject

class DocumentTypeController @Inject() (
  cc: ControllerComponents,
  documentTypeService: DocumentTypeService
) extends ReferenceDataController(cc) {

  def documentTypes(): Action[AnyContent] =
    Action {
      _ =>
        Ok(Json.toJson(documentTypeService.documentTypes()))
    }

  def previousDocumentTypes(): Action[AnyContent] =
    Action {
      _ =>
        Ok(Json.toJson(documentTypeService.previousDocumentTypes()))
    }

  def getPreviousDocumentType(code: String): Action[AnyContent] =
    Action {
      documentTypeService
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
