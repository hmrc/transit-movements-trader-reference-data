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
import javax.inject.Inject
import logging.Logging
import models.DocumentTypeCommonList
import play.api.libs.json.Json
import play.api.mvc.Action
import play.api.mvc.AnyContent
import play.api.mvc.ControllerComponents
import repositories.Selector
import uk.gov.hmrc.play.bootstrap.backend.controller.BackendController

import scala.concurrent.ExecutionContext

trait DocumentTypeController {
  def getAll(): Action[AnyContent]
}

class DocumentTypeControllerMongo @Inject() (
  cc: ControllerComponents,
  referenceDataService: ReferenceDataService
)(implicit ec: ExecutionContext)
    extends BackendController(cc)
    with Logging
    with DocumentTypeController {

  def getAll(): Action[AnyContent] =
    Action.async {
      referenceDataService.many(DocumentTypeCommonList, Selector.All()).map {
        case data if data.nonEmpty =>
          Ok(Json.toJson(data))
        case _ =>
          logger.error(s"No data found for ${DocumentTypeCommonList.listName}")
          NotFound
      }
    }
}
