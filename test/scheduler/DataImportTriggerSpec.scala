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

import java.time.Instant

import org.mockito.ArgumentMatchers.any
import org.mockito.ArgumentMatchers.{eq => eqTo}
import org.mockito.Mockito.reset
import org.mockito.Mockito.times
import org.mockito.Mockito.verify
import org.mockito.Mockito.when
import org.scalatest.BeforeAndAfterEach
import org.scalatest.EitherValues
import org.scalatest.OptionValues
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers
import org.scalatestplus.mockito.MockitoSugar
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.test.Helpers.running
import repositories.DataImport
import repositories.ImportId
import repositories.ImportStatus
import repositories.LockRepository
import repositories.LockResult
import scheduler.ScheduleStatus.MongoUnlockException
import scheduler.ScheduleStatus.UnknownExceptionOccurred

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

class DataImportTriggerSpec extends AnyFreeSpec with Matchers with MockitoSugar with ScalaFutures with BeforeAndAfterEach with EitherValues with OptionValues {

  private val fixedInstant          = Instant.ofEpochMilli(1)
  private val mockLockRepo          = mock[LockRepository]
  private val mockDataImportService = mock[DataImportService]

  val appBuilder: GuiceApplicationBuilder =
    new GuiceApplicationBuilder()
      .overrides(
        bind[LockRepository].toInstance(mockLockRepo),
        bind[DataImportService].toInstance(mockDataImportService)
      )

  override def beforeEach(): Unit = {
    reset(mockLockRepo)
    reset(mockDataImportService)
    super.beforeEach()
  }

  ".invoke" - {

    "when a lock can be acquired" - {

      "and importing data is successful" - {

        "must return details of the import and release the lock" in {

          val dataImport = DataImport(ImportId(1), ImportStatus.Complete, fixedInstant, Some(fixedInstant))
          val lock       = JobName.ImportData

          when(mockLockRepo.lock(eqTo(lock))) thenReturn Future.successful(LockResult.LockAcquired)
          when(mockLockRepo.unlock(eqTo(lock))) thenReturn Future.successful(true)
          when(mockDataImportService.importReferenceData()(any())) thenReturn Future.successful(dataImport)

          val app = appBuilder.build()

          running(app) {

            val trigger = app.injector.instanceOf[DataImportTrigger]

            val result = trigger.invoke.futureValue

            result.right.value.value mustEqual dataImport
            verify(mockLockRepo, times(1)).unlock(eqTo(lock))
          }
        }

        "and releasing the lock fails" - {

          "must return a Mongo unlock exception" in {

            val dataImport = DataImport(ImportId(1), ImportStatus.Complete, fixedInstant, Some(fixedInstant))
            val lock       = JobName.ImportData
            val exception  = new Exception("Could not unlock")

            when(mockLockRepo.lock(eqTo(lock))) thenReturn Future.successful(LockResult.LockAcquired)
            when(mockLockRepo.unlock(eqTo(lock))) thenReturn Future.failed(exception)
            when(mockDataImportService.importReferenceData()(any())) thenReturn Future.successful(dataImport)

            val app = appBuilder.build()

            running(app) {

              val trigger = app.injector.instanceOf[DataImportTrigger]

              val result = trigger.invoke.futureValue

              result.left.value mustEqual MongoUnlockException(exception)
            }
          }
        }
      }

      "and importing the data fails" - {

        "must return an exception and release the lock" in {

          val lock      = JobName.ImportData
          val exception = new Exception("foo")

          when(mockLockRepo.lock(eqTo(lock))) thenReturn Future.successful(LockResult.LockAcquired)
          when(mockLockRepo.unlock(eqTo(lock))) thenReturn Future.successful(true)
          when(mockDataImportService.importReferenceData()(any())) thenReturn Future.failed(exception)

          val app = appBuilder.build()

          running(app) {

            val trigger = app.injector.instanceOf[DataImportTrigger]

            val result = trigger.invoke.futureValue

            result.left.value mustEqual UnknownExceptionOccurred(exception)
            verify(mockLockRepo, times(1)).unlock(eqTo(lock))
          }
        }
      }
    }

    "when a lock cannot be acquired" - {

      "must return successfully with no content, and not attempt to import data" in {

        val lock = JobName.ImportData

        when(mockLockRepo.lock(eqTo(lock))) thenReturn Future.successful(LockResult.AlreadyLocked)

        val app = appBuilder.build()

        running(app) {

          val trigger = app.injector.instanceOf[DataImportTrigger]

          val result = trigger.invoke.futureValue

          result.right.value must not be defined
          verify(mockDataImportService, times(0)).importReferenceData()
          verify(mockLockRepo, times(0)).unlock(eqTo(lock))
        }
      }
    }
  }
}
