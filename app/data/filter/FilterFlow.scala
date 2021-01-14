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

package data.filter

import java.time.LocalDate

import akka.NotUsed
import akka.stream.ActorAttributes
import akka.stream.Attributes
import akka.stream.Supervision
import akka.stream.scaladsl.Flow
import models.ReferenceDataList.Constants.Common
import play.api.Logger
import play.api.libs.json.JsObject

case class FilterFlow() {

  val logger: Logger = Logger(getClass.getSimpleName)

  private def isActiveRecord(jsObject: JsObject): Boolean =
    (jsObject \ Common.state).as[String] == Common.valid && (jsObject \ Common.activeFrom)
      .asOpt[LocalDate]
      .exists(_.isBefore(LocalDate.now().plusDays(1)))

  private val supervisionStrategy: Attributes = ActorAttributes.supervisionStrategy {
    case FilterFlowItemNotActiveException => Supervision.resume
    case _ =>
      logger.error("[supervisionStrategy] An unexpected exception happened when trying to parse the Json")
      Supervision.resume
  }

  def flow: Flow[JsObject, JsObject, NotUsed] =
    Flow[JsObject]
      .map(
        jsObject =>
          if (isActiveRecord(jsObject))
            jsObject
          else
            throw FilterFlowItemNotActiveException
      )
      .addAttributes(supervisionStrategy)
}
