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

package api.models

import play.api.libs.functional.syntax._
import play.api.libs.json.Json
import play.api.libs.json.Reads
import play.api.libs.json.Writes
import play.api.libs.json.__

final case class DangerousGoodsCode(code: String, description: String)

object DangerousGoodsCode {

  implicit def reads: Reads[DangerousGoodsCode] =
    ((__ \ "code").read[String] and
      (__ \ "description").read[String])(DangerousGoodsCode.apply _)

  implicit def writes: Writes[DangerousGoodsCode] = Json.writes[DangerousGoodsCode]
}
