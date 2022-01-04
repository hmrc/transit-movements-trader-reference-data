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

package controllers.ingestion

import services.DataImportService
import javax.inject.Inject
import logging.Logging
import models.ReferenceDataList
import play.api.libs.json.JsObject
import play.api.mvc.Action
import play.api.mvc.ControllerComponents
import uk.gov.hmrc.play.bootstrap.backend.controller.BackendController
import com.kenshoo.play.metrics.Metrics
import scala.concurrent.ExecutionContext
import java.util.UUID
import metrics.HasActionMetrics
import play.api.mvc.Request
import scala.util.control.NonFatal

class DataImportController @Inject() (
  cc: ControllerComponents,
  loadDataService: DataImportService,
  val metrics: Metrics
)(implicit ec: ExecutionContext)
    extends BackendController(cc)
    with Logging
    with HasActionMetrics {

  private def requestItemCountHistogram(list: ReferenceDataList) = histo(s"data-ingest-item-count.${list.listName}")
  private def requestSizeHistogram(list: ReferenceDataList)      = histo(s"data-ingest-request-size.${list.listName}")

  def post(list: ReferenceDataList): Action[Seq[JsObject]] =
    withMetricsTimerAction(s"data-ingest-timer-${list.listName}") {
      Action.async(parse.json[Seq[JsObject]]) {
        implicit request: Request[Seq[JsObject]] =>
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
              dataImportDetails =>
                logger.info(s"[DataImport][Successful][$ingestLogId] List name = ${list.listName}")

                requestItemCountHistogram(list).update(dataImportDetails.records)
                request.headers
                  .get(CONTENT_LENGTH)
                  .foreach(
                    contentLength =>
                      try requestSizeHistogram(list).update(contentLength.toInt)
                      catch {
                        case NonFatal(e) => ()
                      }
                  )

                Ok
            }
            .recover {
              case e: Exception =>
                logger.error(s"[DataImport][Failed][$ingestLogId] List name = ${list.listName}", e)
                InternalServerError
            }
      }

    }
}
