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

package repositories

import play.api.libs.json._

import scala.util.Failure
import scala.util.Success
import scala.util.Try

case class ImportId(value: Int)

object ImportId {

  implicit val writes: Writes[ImportId] = Writes {
    importId =>
      JsNumber(importId.value)
  }

  implicit val reads: Reads[ImportId] = {
    case JsNumber(num) =>
      Try(num.toIntExact) match {
        case Failure(_)     => JsError("Expected number to be an integer")
        case Success(value) => JsSuccess(ImportId(value))
      }
    case _ => JsError("Expected JSON number type")
  }

  val mongoFormat: Format[ImportId] = {
    val reads: Reads[ImportId] =
      (__ \ "import-id")
        .read[Int]
        .map(
          x => ImportId(x)
        )

    val writes: Writes[ImportId] = Writes {
      importId =>
        Json.obj(
          "_id"       -> "last-id",
          "import-id" -> importId.value
        )
    }

    Format(reads, writes)
  }
}
