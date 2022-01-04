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

package repositories

import play.api.libs.json.Reads
import play.api.libs.json.Writes

sealed trait ImportStatus

object ImportStatus {
  case object Started  extends ImportStatus
  case object Complete extends ImportStatus
  case object Failed   extends ImportStatus

  val fromString: PartialFunction[String, ImportStatus] = {
    case "Started"  => Started
    case "Complete" => Complete
    case "Failed"   => Failed
  }

  implicit val writes: Writes[ImportStatus] =
    implicitly[Writes[String]]
      .contramap(_.toString)

  implicit val reads: Reads[ImportStatus] =
    implicitly[Reads[String]]
      .map(fromString.lift)
      .map(_.get)
}
