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
import models.CustomsOfficesList
import org.scalacheck.Gen
import org.scalacheck.Shrink
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import play.api.libs.json.Json
import play.api.libs.json._

class CustomsOfficesListTransformSpec extends SpecBase with ScalaCheckPropertyChecks {

  implicit def dontShrink[A]: Shrink[A] = Shrink.shrinkAny

  val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

  val validData =
    Json
      .parse("""
      |{
      |  "state": "valid",
      |  "activeFrom": "2019-01-01",
      |  "referenceNumber": "AD000003",
      |  "referenceNumberMainOffice": "AD000003",
      |  "referenceNumberHigherAuthority": "AD000003",
      |  "referenceNumberCompetentAuthorityOfEnquiry": "AD000003",
      |  "referenceNumberCompetentAuthorityOfRecovery": "AD000003",
      |  "countryCode": "AD",
      |  "unLocodeId": "ALV",
      |  "nctsEntryDate": "20070614",
      |  "nearestOffice": "CH002621",
      |  "postalCode": "AD700",
      |  "phoneNumber": "+ (376) 879900",
      |  "faxNumber": "+ (376) 860360",
      |  "eMailAddress": "Helpdesk_ncts@andorra.ad",
      |  "geoInfoCode": "Q",
      |  "traderDedicated": 1,
      |  "regionCode": "BW",
      |  "telexNumber": "+358005671",
      |  "customsOfficeSpecificNotes": [
      |    {
      |      "specificNotesCode": "SN0002"
      |    },
      |    {
      |      "specificNotesCode": "SN0006"
      |    },
      |    {
      |      "specificNotesCode": "SN0022"
      |    }
      |  ],
      |  "dedicatedTrader": [
      |    {
      |      "languageCode": "EN",
      |      "name": "H M Revenue & Customs"
      |    }
      |  ],
      |  "customsOfficeDetails": [
      |    {
      |      "languageCode": "EN",
      |      "customsOfficeUsualName": "CENTRAL CUSTOMS OFFICE",
      |      "streetAndNumber": "AVINGUDA FITER I ROSSELL, 2",
      |      "city": "ESCALDES - ENGORDANY",
      |      "prefixSuffixFlag": 0,
      |      "prefixSuffixLevel": "E",
      |      "prefixSuffixName": "Tulli",
      |      "spaceToAdd": 0
      |    },
      |    {
      |      "languageCode": "ES",
      |      "customsOfficeUsualName": "DESPATCHO CENTRAL DE ADUANAS",
      |      "streetAndNumber": "AVINGUDA FITER I ROSSELL, 2",
      |      "city": "ESCALDES-ENGORDANY",
      |      "prefixSuffixFlag": 0,
      |      "spaceToAdd": 0
      |    },
      |    {
      |      "languageCode": "FR",
      |      "customsOfficeUsualName": "BUREAU CENTRAL DES DOUANES",
      |      "streetAndNumber": "AVINGUDA FITER I ROSSELL, 2",
      |      "city": "ESCALDES - ENGORDANY",
      |      "prefixSuffixFlag": 0,
      |      "spaceToAdd": 0
      |    }
      |  ],
      |  "customsOfficeTimetable": [
      |    {
      |      "seasonCode": 1,
      |      "seasonName": "ALL YEAR",
      |      "seasonStartDate": "20180101",
      |      "seasonEndDate": "20991231",
      |      "customsOfficeTimetableLine": [
      |        {
      |          "dayInTheWeekBeginDay": 1,
      |          "openingHoursTimeFirstPeriodFrom": "0800",
      |          "openingHoursTimeFirstPeriodTo": "1700",
      |          "openingHoursTimeSecondPeriodFrom": "1200",
      |          "openingHoursTimeSecondPeriodTo": "1800",
      |          "dayInTheWeekEndDay": 5,
      |          "customsOfficeRoleTrafficCompetence": [
      |            {
      |              "role": "AUT",
      |              "trafficType": "R"
      |            },
      |            {
      |              "role": "CAU",
      |              "trafficType": "R"
      |            },
      |            {
      |              "role": "DEP",
      |              "trafficType": "R"
      |            },
      |            {
      |              "role": "ENQ",
      |              "trafficType": "R"
      |            },
      |            {
      |              "role": "GUA",
      |              "trafficType": "R"
      |            },
      |            {
      |              "role": "REC",
      |              "trafficType": "R"
      |            }
      |          ]
      |        }
      |      ]
      |    }
      |  ]
      |}
      |""".stripMargin)
      .as[JsObject]

  "transform" - {
    "when the json matches the expected schema" - {
      "returns the transformed data as a JsSuccess" in {

        val expected =
          """
            |{
            | "CUST_OFF_ID": "AD000003",
            | "CUST_OFF_NAM": "CENTRAL CUSTOMS OFFICE",
            | "COUNTRY_ID": "AD",
            | "PHONE_NUMBER": "+ (376) 879900",
            | "CITY": "ESCALDES - ENGORDANY",
            | "CUSTOMS_OFFICE_ROLES": []
            |}
            |""".stripMargin

        val result = Transformation(CustomsOfficesList).runTransform(validData)

        result.get mustEqual Json.parse(expected)

      }
    }

    "when the json doesn't match the expected schema" - {

      "returns a JsError parse error" - {
        val mandatoryFields = Gen.oneOf(
          Seq(
            "referenceNumber",
            "state",
            "activeFrom",
            "countryCode",
            "phoneNumber"
          )
        )

        "when top level mandatory fields are missing" in {
          forAll(mandatoryFields) {
            mandatoryField =>
              val dataWithError = (__ \ mandatoryField).json.prune.reads(validData).get

              val result =
                Transformation(CustomsOfficesList).transform
                  .reads(dataWithError)
                  .asEither
                  .left
                  .value

              result.length mustEqual 1
              val (errorPath, _) = result.head
              errorPath mustEqual (__ \ mandatoryField)

          }

        }

        "when nested mandatory fields are missing" - {
          "when the english description is missing" in {

            val dataWithError = validData ++ Json.obj("customsOfficeDetails" -> JsArray.empty)

            val result = Transformation(CustomsOfficesList).transform
              .reads(dataWithError)
              .asEither
              .left
              .value

            result must not be empty

          }

          "asdf when the english description City is missing" in {

            val dataWithError = validData ++ Json
              .parse(
                """
                  |{ "customsOfficeDetails": [
                  |  {
                  |    "languageCode": "EN",
                  |    "customsOfficeUsualName": "CENTRAL CUSTOMS OFFICE",
                  |    "streetAndNumber": "AVINGUDA FITER I ROSSELL, 2",
                  |    "prefixSuffixFlag": 0,
                  |    "prefixSuffixLevel": "E",
                  |    "prefixSuffixName": "Tulli",
                  |    "spaceToAdd": 0
                  |  }
                  |]}
                  |""".stripMargin
              )
              .as[JsObject]

            val result =
              Transformation(CustomsOfficesList).transform
                .reads(dataWithError)
                .asEither
                .left
                .value

            result must not be empty
          }

        }
      }
    }

  }

  "filter" ignore {

    "returns a JsSuccess of None if the country is invalid" ignore {}

    "returns a JsSuccess of None if the activeFrom date is in the future" ignore {}

  }

}
