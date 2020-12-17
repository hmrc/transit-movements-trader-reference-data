/*
 * Copyright 2020 HM Revenue & Customs
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

package scheduler.tasks

import javax.inject.Inject
import repositories.DataImport
import repositories.LockRepository
import scheduler.jobs.ScheduleStatus.UnknownExceptionOccurred
import scheduler.jobs.JobFailed
import scheduler.jobs.JobName
import scheduler.services.DataImportService

import scala.concurrent.ExecutionContext
import scala.concurrent.Future

class DataImportTask @Inject() (
  val lockRepository: LockRepository,
  dataImportService: DataImportService
) extends ScheduledTask[Either[JobFailed, Option[DataImport]]] {

  override def invoke(implicit ec: ExecutionContext): Future[Either[JobFailed, Option[DataImport]]] = {

    logger.info("Trigger has been invoked")

    val lock = JobName.ImportData

    withLock(lock) {
      dataImportService.importReferenceData().map {
        dataImport =>
          logger.info(s"Import ${dataImport.importId.value} finished with status ${dataImport.status}")
          Right(Some(dataImport))
      } recover {
        case e: Exception =>
          logger.warn("Something went wrong trying to import reference data", e)
          Left(UnknownExceptionOccurred(e))
      }
    }
  }
}
