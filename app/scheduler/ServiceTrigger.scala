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

import repositories.{LockRepository, LockResult}

import scala.concurrent.ExecutionContext
import scala.concurrent.Future

trait ServiceTrigger[A] {
  val lockRepository: LockRepository

  val lockName: String = this.getClass.getCanonicalName

  def invoke(implicit ec: ExecutionContext): Future[A]

  protected def getLock(block: => Future[Either[JobFailed, A]])(implicit ec: ExecutionContext): Future[Either[JobFailed, Option[A]]] = {
    lockRepository.lock(lockName).flatMap {
      case LockResult.LockAcquired =>
        block.flatMap {
          result =>
            lockRepository.unlock(lockName).map(_ => result)
        }
      case LockResult.AlreadyLocked => Future.successful(Right(None))
    }
  }
}
