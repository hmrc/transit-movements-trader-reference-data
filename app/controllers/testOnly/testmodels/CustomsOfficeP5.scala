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

package controllers.testOnly.testmodels

import play.api.libs.functional.syntax._
import play.api.libs.json.Reads._
import play.api.libs.json._

case class CustomsOfficeP5(
  id: String,
  name: String,
  countryId: String
)

object CustomsOfficeP5 {

  implicit val writes: OWrites[CustomsOfficeP5] = Json.writes[CustomsOfficeP5]

  implicit val readFromFile: Reads[CustomsOfficeP5] =
    (
      (__ \ "CUST_OFF_ID").read[String] and
        (__ \ "CUST_OFF_NAM").read[String] and
        (__ \ "COUNTRY_ID").read[String]
    )(CustomsOfficeP5.apply _)

}
