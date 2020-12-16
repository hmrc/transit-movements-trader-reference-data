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

package scheduler

import javax.inject.Inject
import play.api.Logging
import repositories.{DataImport, LockRepository, LockResult}
import scheduler.ScheduleStatus.{MongoUnlockException, UnknownExceptionOccurred}

import scala.concurrent.{ExecutionContext, Future}

class DataImportTrigger @Inject() (
  val lockRepository: LockRepository,
  dataImportService: DataImportService
) extends ServiceTrigger[Either[JobFailed, Option[DataImport]]] with Logging {

  override def invoke(implicit ec: ExecutionContext): Future[Either[JobFailed, Option[DataImport]]] = {

    logger.info("Trigger has been invoked")

    val lock = this.getClass.getCanonicalName

    lockRepository.lock(lock) flatMap {
      case LockResult.LockAcquired =>
        dataImportService.importReferenceData().map {
          dataImport => Right(Some(dataImport))
        } recover {
          case e: Exception =>
            logger.warn("Something went wrong trying to import reference data", e)
            Left(UnknownExceptionOccurred(e))
        }

      case LockResult.AlreadyLocked =>
        logger.info("Could not get a lock - may have been triggered on another instance")
        Future.successful(Right(None))
    } recover {
      case e: Exception =>
        logger.warn("Something went wrong getting a lock", e)
        Left(MongoUnlockException(e))
    }
  }
}
