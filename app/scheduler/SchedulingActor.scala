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

import akka.actor.Actor
import akka.actor.ActorLogging
import akka.actor.Props
import logging.Logging
import repositories.DataImport
import scheduler.SchedulingActor.ScheduledMessage

import scala.concurrent.ExecutionContext.Implicits.global

class SchedulingActor extends Actor with ActorLogging with Logging {

  override def receive: Receive = {
    case message: ScheduledMessage[_] =>
      logger.info(s"Received message: ${message.getClass.getCanonicalName}")
      message.trigger.invoke
  }
}

object SchedulingActor {

  def props: Props = Props[SchedulingActor]

  sealed trait ScheduledMessage[A] {
    val trigger: ServiceTrigger[A]
  }

  case class ImportDataMessage(trigger: DataImportTrigger) extends ScheduledMessage[Either[JobFailed, Option[DataImport]]]
}
