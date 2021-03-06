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

package data.controllers

import data.services.DataImportService
import javax.inject.Inject
import logging.Logging
import models.ReferenceDataList
import play.api.libs.json.JsObject
import play.api.mvc.Action
import play.api.mvc.ControllerComponents
import uk.gov.hmrc.play.bootstrap.backend.controller.BackendController

import scala.concurrent.ExecutionContext
import java.util.UUID

class DataImportController @Inject() (cc: ControllerComponents, loadDataService: DataImportService)(implicit ec: ExecutionContext)
    extends BackendController(cc)
    with Logging {

  def post(list: ReferenceDataList): Action[Seq[JsObject]] =
    Action.async(parse.json[Seq[JsObject]]) {
      implicit request =>
        val ingestLogId =
          request.headers
            .get("X-Request-Id")
            .map(
              x => s"XRequestId=$x"
            )
            .getOrElse(s"GeneratedRequestId=${UUID.randomUUID()}")

        logger.info(s"[DataImport][Start][$ingestLogId] List name = ${list.listName}")

        loadDataService
          .importData(list, request.body)
          .map {
            _ =>
              logger.info(s"[DataImport][Successful][$ingestLogId] List name = ${list.listName}")

              Ok
          }
          .recover {
            case e: Exception =>
              logger.error(s"[DataImport][Failed][$ingestLogId] List name = ${list.listName}", e)
              InternalServerError
          }
    }
}
