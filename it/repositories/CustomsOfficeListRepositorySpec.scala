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

import models.CustomsOfficesList
import org.scalatest.OptionValues
import org.scalatest.concurrent.IntegrationPatience
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers
import play.api.libs.json.JsObject
import play.api.libs.json.Json
import repositories.ListRepository.ListRepositoryProvider
import repositories.Selector.ByCountry
import repositories.Selector.ById
import repositories.Selector.OptionallyByRole
import uk.gov.hmrc.mongo.test.DefaultPlayMongoRepositorySupport

import scala.concurrent.ExecutionContext.Implicits.global

class CustomsOfficeListRepositorySpec
    extends AnyFreeSpec
    with Matchers
    with ScalaFutures
    with IntegrationPatience
    with OptionValues
    with DefaultPlayMongoRepositorySupport[JsObject] {

  override protected def repository: ListRepository = new ListRepositoryProvider(mongoComponent).apply(CustomsOfficesList)

  class Setup {

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
      "importId"    -> 12345,
      "roles"       -> Json.arr(genRole("TRA"), genRole("DEP"), genRole("DES"))
    )

    val officeTwo: JsObject = Json.obj(
      "phoneNumber" -> "12345",
      "importId"    -> 12345,
      "name"        -> "AB0002",
      "roles"       -> Json.arr(genRole("TRA")),
      "id"          -> "AB00002",
      "countryId"   -> "AB"
    )

    val officeThree: JsObject = Json.obj(
      "phoneNumber" -> "12345",
      "importId"    -> 12345,
      "name"        -> "AB0003",
      "roles"       -> Json.arr(genRole("TRA"), genRole("DEP"), genRole("DES")),
      "id"          -> "AB00003",
      "countryId"   -> "AC"
    )

    val officeFour: JsObject = Json.obj(
      "phoneNumber" -> "12345",
      "importId"    -> 12345,
      "name"        -> "AB0004",
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

    repository.insert(ImportId(12345), customsOffices).futureValue
  }

  "customsOfficeListRepository" - {
    "fetch all offices when roles is empty" in new Setup {
      val returnedOffices: Seq[JsObject] = repository
        .many(OptionallyByRole(Nil))
        .futureValue
        .map(_ - "_id")

      returnedOffices mustEqual customsOffices
    }

    "fetch offices with specific roles if roles is not empty" in new Setup {
      val returnedOffices: Seq[JsObject] = repository.many(OptionallyByRole(Seq("TRA"))).futureValue.map(_ - "_id")

      returnedOffices mustEqual Seq(
        officeOne,
        officeTwo,
        officeThree
      )
    }

    "fetch offices with specific roles if roles has multiple roles" in new Setup {
      val returnedOffices: Seq[JsObject] = repository.many(OptionallyByRole(Seq("TRA", "DEP"))).futureValue.map(_ - "_id")

      returnedOffices mustEqual Seq(
        officeOne,
        officeThree
      )
    }

    "fetch all offices by country when roles is empty" in new Setup {
      val returnedOffices: Seq[JsObject] = repository
        .many(ByCountry("AB") and OptionallyByRole(Nil))
        .futureValue
        .map(_ - "_id")

      returnedOffices mustEqual Seq(
        officeOne,
        officeTwo,
        officeFour
      )
    }

    "fetch offices by country with specific roles if roles is not empty" in new Setup {
      val returnedOffices: Seq[JsObject] = repository.many(OptionallyByRole(Seq("NPM"))).futureValue.map(_ - "_id")

      returnedOffices mustEqual Seq(
        officeFour
      )
    }

    "fetch office by id" in new Setup {
      val returnedOffices: JsObject = repository.one(ById("AB00002")).futureValue.map(_ - "_id").value

      returnedOffices mustEqual officeTwo
    }
  }
}
