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

package data.transform

import java.time.format.DateTimeFormatter

import base.SpecBase
import models.CountryCodesFullList
import play.api.libs.json.Json
import play.api.libs.json._

class CountryCodesFullListTransfomSpec extends SpecBase {
  import CountryCodesFullListTransfomSpec._

  val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

  "transform" - {
    "transforms data as a JsSuccess JsObject and keeps the English description" in {

      val expected =
        """
          |{
          |  "code": "AD",
          |  "state": "valid",
          |  "description": "Andorra"
          |}
          |""".stripMargin

      val result = Transformation(CountryCodesFullList).transform.reads(Json.parse(validData))

      result.get mustEqual Json.parse(expected)

    }

    "returns a JsError parse error if the json doesn't match the expected schema" - {

      "with missing state" in {

        val result =
          Transformation(CountryCodesFullList).transform
            .reads(Json.parse(invalidDataWithMissingState))
            .asEither
            .left
            .value

        result.length mustEqual 1
        val (errorPath, _) = result.head
        errorPath mustEqual (__ \ "state")

      }

      "with missing activeFrom" in {
        val data =
          """ 
            |{
            |  "state": "valid",
            |  "countryCode": "AD",
            |  "tccEntryDate": "19000101",
            |  "nctsEntryDate": "19000101",
            |  "geoNomenclatureCode": "043",
            |  "countryRegimeCode": "TOC",
            |  "description": {
            |    "en": "Andorra",
            |    "tt": "Andorra"
            |  }
            |}
            |""".stripMargin

        val result =
          Transformation(CountryCodesFullList).transform
            .reads(Json.parse(data))
            .asEither
            .left
            .value

        result.length mustEqual 1
        val (errorPath, _) = result.head
        errorPath mustEqual (__ \ "activeFrom")

      }

      "with missing countryCode" in {

        val data =
          """ 
            |{
            |  "state": "valid",
            |  "activeFrom": "2020-01-23",
            |  "tccEntryDate": "19000101",
            |  "nctsEntryDate": "19000101",
            |  "geoNomenclatureCode": "043",
            |  "countryRegimeCode": "TOC",
            |  "description": {
            |    "en": "Andorra",
            |    "tt": "Andorra"
            |  }
            |}
            |""".stripMargin

        val result =
          Transformation(CountryCodesFullList).transform
            .reads(Json.parse(data))
            .asEither
            .left
            .value

        result.length mustEqual 1
        val (errorPath, _) = result.head
        errorPath mustEqual (__ \ "countryCode")

      }

      "with missing #/description/en field" in {
        val data =
          """ 
            |{
            |  "state": "valid",
            |  "activeFrom": "2020-01-23",
            |  "countryCode": "AD",
            |  "tccEntryDate": "19000101",
            |  "nctsEntryDate": "19000101",
            |  "geoNomenclatureCode": "043",
            |  "countryRegimeCode": "TOC",
            |  "description": {
            |    "tt": "Andorra"
            |  }
            |}
            |""".stripMargin

        val result =
          Transformation(CountryCodesFullList).transform
            .reads(Json.parse(data))
            .asEither
            .left
            .value

        result.length mustEqual 1
        val (errorPath, _) = result.head
        errorPath mustEqual (__ \ "description" \ "en")
      }

    }

  }

  "filter" ignore {

    "returns a JsSuccess of None if the country is invalid" ignore {}

    "returns a JsSuccess of None if the country is in the future" ignore {}

  }

}

object CountryCodesFullListTransfomSpec {

  val validData =
    """ 
      |{
      |  "state": "valid",
      |  "activeFrom": "2020-01-23",
      |  "countryCode": "AD",
      |  "tccEntryDate": "19000101",
      |  "nctsEntryDate": "19000101",
      |  "geoNomenclatureCode": "043",
      |  "countryRegimeCode": "TOC",
      |  "description": {
      |    "en": "Andorra",
      |    "tt": "Andorra"
      |  }
      |}
      |""".stripMargin

  val expected =
    """
      |{
      |  "code": "AD",
      |  "state": "valid",
      |  "description": "Andorra"
      |}
      |""".stripMargin

  val invalidDataWithMissingState =
    """ 
      |{
      |  "activeFrom": "2020-01-23",
      |  "countryCode": "AD",
      |  "tccEntryDate": "19000101",
      |  "nctsEntryDate": "19000101",
      |  "geoNomenclatureCode": "043",
      |  "countryRegimeCode": "TOC",
      |  "description": {
      |    "en": "Andorra",
      |    "tt": "Andorra"
      |  }
      |}
      |""".stripMargin

}
