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

package controllers.consumption

import services.ReferenceDataService
import logging.Logging
import models.RequestedDocumentTypeList
import play.api.libs.json.Json
import play.api.mvc.Action
import play.api.mvc.AnyContent
import play.api.mvc.ControllerComponents
import repositories.Selector
import uk.gov.hmrc.play.bootstrap.backend.controller.BackendController

import javax.inject.Inject
import scala.concurrent.ExecutionContext

trait RequestedDocumentTypeController {
  def getAll: Action[AnyContent]
  def getRequestedDocumentType(code: String): Action[AnyContent]
}

class RequestedDocumentTypeControllerMongo @Inject() (
  cc: ControllerComponents,
  referenceData: ReferenceDataService
)(implicit ec: ExecutionContext)
    extends BackendController(cc)
    with RequestedDocumentTypeController
    with Logging {

  def getAll: Action[AnyContent] =
    Action.async {
      referenceData.many(RequestedDocumentTypeList, Selector.All()).map {
        case data if data.nonEmpty => Ok(Json.toJson(data))
        case _ =>
          logger.warn(s"No data found for ${RequestedDocumentTypeList.listName}")
          NotFound
      }
    }

  def getRequestedDocumentType(code: String): Action[AnyContent] =
    Action.async {
      referenceData.one(RequestedDocumentTypeList, Selector.ByCode(code)).map {
        case Some(data) => Ok(data)
        case _ =>
          logger.warn(s"No data found for code: $code for list: ${RequestedDocumentTypeList.listName}")
          NotFound
      }
    }
}