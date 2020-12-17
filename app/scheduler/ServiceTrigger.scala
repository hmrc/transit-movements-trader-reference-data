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

import logging.Logging
import repositories.LockRepository
import repositories.LockResult
import scheduler.ScheduleStatus.MongoUnlockException
import scheduler.ScheduleStatus.UnknownExceptionOccurred

import scala.concurrent.ExecutionContext
import scala.concurrent.Future

trait ServiceTrigger[A] extends Logging {
  val lockRepository: LockRepository

  def invoke(implicit ec: ExecutionContext): Future[A]

  protected def withLock[T](lock: String)(block: => Future[Either[JobFailed, Option[T]]])(implicit ec: ExecutionContext): Future[Either[JobFailed, Option[T]]] =
    lockRepository.lock(lock) flatMap {
      case LockResult.LockAcquired =>
        logger.info("Acquired a lock")

        block.flatMap {
          result =>
            lockRepository
              .unlock(lock)
              .map(_ => result)
              .recover {
                case e: Exception =>
                  logger.warn(s"Unable to release lock $lock")
                  Left(MongoUnlockException(e))
              }
        }

      case LockResult.AlreadyLocked =>
        logger.info("Could not get a lock - may have been triggered on another instance")
        Future.successful(Right(None))
    } recoverWith {
      case e: Exception =>
        logger.warn("Something went wrong", e)
        lockRepository
          .unlock(lock)
          .map(_ => Left(UnknownExceptionOccurred(e)))
          .recover {
            case e: Exception =>
              logger.warn(s"Unable to release lock $lock")
              Left(MongoUnlockException(e))
          }
    }
}
