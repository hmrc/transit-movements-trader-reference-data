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

package api.ingestion

import org.scalatest.OptionValues
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers
import org.scalatestplus.play.guice.GuiceOneServerPerSuite
import play.api.libs.json.JsArray
import play.api.libs.json.JsObject
import play.api.libs.json.Json
import play.api.libs.ws.WSClient
import play.api.libs.ws.WSResponse
import repositories._

import scala.concurrent.duration.Duration
import scala.concurrent.Await
import scala.concurrent.Future

class IngestCustomsOfficesSpec extends AnyFreeSpec with Matchers with ScalaFutures with OptionValues with GuiceOneServerPerSuite {

  class Setup {

    val nextId: ImportId = ImportId(1)

    def genRole(role: String): JsObject = Json.obj(
      "seasonStartDate"                 -> "20180101",
      "role"                            -> role,
      "seasonCode"                      -> 1,
      "openingHoursTimeFirstPeriodFrom" -> "0800",
      "dayInTheWeekBeginDay"            -> 1,
      "openingHoursTimeFirstPeriodTo"   -> "2000",
      "seasonName"                      -> "Well",
      "trafficType"                     -> "N/A",
      "dayInTheWeekEndDay"              -> 5,
      "seasonEndDate"                   -> "21001212"
    )

    val officeOne: JsObject = Json.obj(
      "name"        -> "AB0001",
      "phoneNumber" -> "12345",
      "id"          -> "AB00001",
      "countryId"   -> "AB",
      "importId"    -> nextId.value,
      "roles"       -> Json.arr(genRole("TRA"), genRole("DEP"), genRole("DES"))
    )

    val officeTwo: JsObject = Json.obj(
      "phoneNumber" -> "12345",
      "importId"    -> nextId.value,
      "name"        -> "AB0002",
      "roles"       -> Json.arr(genRole("TRA")),
      "id"          -> "AB00002",
      "countryId"   -> "AB"
    )

    val officeThree: JsObject = Json.obj(
      "phoneNumber" -> "12345",
      "importId"    -> nextId.value,
      "name"        -> "AB0003",
      "roles"       -> Json.arr(genRole("TRA"), genRole("DEP"), genRole("DES")),
      "id"          -> "AB00003",
      "countryId"   -> "AC"
    )

    val officeFour: JsObject = Json.obj(
      "phoneNumber" -> "12345",
      "importId"    -> nextId.value,
      "name"        -> "AB0002",
      "roles"       -> Json.arr(genRole("DEP"), genRole("DES"), genRole("NPM")),
      "id"          -> "AB00004",
      "countryId"   -> "AB"
    )

    val customsOffices = Seq(
      officeOne,
      officeTwo,
      officeThree,
      officeFour
    )

    val ws: WSClient = app.injector.instanceOf[WSClient]
  }

  "POST /customs-offices" - {
    "return 200" in new Setup {
      val resultF: Future[WSResponse] = ws
        .url(s"http://localhost:$port/transit-movements-trader-reference-data/data/CustomsOffices")
        .post(JsArray(customsOffices))

      val result: WSResponse = Await.result(resultF, Duration.Inf)

      result.status mustBe 200
    }
  }
}
