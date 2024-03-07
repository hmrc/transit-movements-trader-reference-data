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

package services

import logging.Logging
import models.ReferenceDataList
import play.api.libs.json.JsObject
import repositories.ListRepository.ListRepositoryProvider
import repositories._

import java.time.Clock
import java.time.Instant
import javax.inject.Inject
import scala.concurrent.ExecutionContext
import scala.concurrent.Future

class DataImportService @Inject() (
  importIdRepository: ImportIdRepository,
  dataImportRepository: DataImportRepository,
  listRepositoryProvider: ListRepositoryProvider,
  clock: Clock
)(implicit ec: ExecutionContext)
    extends Logging {

  def importData(list: ReferenceDataList, data: Seq[JsObject]): Future[DataImport] =
    for {
      importId <- importIdRepository.nextId
      dataImport = DataImport(importId, list, data.size, ImportStatus.Started, Instant.now(clock))
      _            <- dataImportRepository.insert(dataImport)
      insertResult <- insertData(list, importId, data)
      importStatus = if (insertResult) ImportStatus.Complete else ImportStatus.Failed
      updateResult <- dataImportRepository.markFinished(importId, importStatus)
      _            <- cleanUp(list, importId, importStatus)
    } yield updateResult

  private def insertData(list: ReferenceDataList, importId: ImportId, data: Seq[JsObject]): Future[Boolean] =
    if (data.nonEmpty) {
      listRepositoryProvider(list)
        .insert(importId, data)
        .recover {
          case e: Exception =>
            logger.error(s"Error inserting ${list.listName} data", e)
            false
        }
    } else {
      logger.error(s"data for ${list.listName} is empty")
      Future.successful(false)
    }

  private def cleanUp(list: ReferenceDataList, currentImportId: ImportId, importStatus: ImportStatus): Future[Boolean] =
    if (importStatus == ImportStatus.Complete) {
      logger.info(s"Deleting ${list.listName} data with an import id less than ${currentImportId.value}")
      listRepositoryProvider(list).deleteOldImports(currentImportId)
    } else {
      logger.warn(s"Not deleting any ${list.listName} data as the import failed")
      Future.successful(false)
    }
}
