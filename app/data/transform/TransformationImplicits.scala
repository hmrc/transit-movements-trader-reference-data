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

import models.CountryCodesCommonTransitList
import models.CountryCodesFullList
import models.CustomsOfficesList
import models.ReferenceDataList.Constants._
import play.api.libs.functional.syntax._
import play.api.libs.json.Reads._
import play.api.libs.json._

trait TransformationImplicits {

  implicit val transformationCountryCodesFullList: Transformation[CountryCodesFullList.type] =
    Transformation
      .fromReads(
        (
          (__ \ CountryCodesFullListFieldNames.code).json.copyFrom((__ \ "countryCode").json.pick) and
            (__ \ Common.state).json.pickBranch and
            (__ \ Common.activeFrom).json.pickBranch and
            (__ \ CountryCodesFullListFieldNames.description).json.copyFrom(englishDescription)
        ).reduce
          .andThen(
            (__ \ Common.activeFrom).json.prune
          )
      )

  implicit val transformationCountryCodesCommonTransitList: Transformation[CountryCodesCommonTransitList.type] =
    Transformation
      .fromReads(
        (
          (__ \ CountryCodesCommonTransitListFieldNames.code).json.copyFrom((__ \ "countryCode").json.pick) and
            (__ \ Common.state).json.pickBranch and
            (__ \ Common.activeFrom).json.pickBranch and
            (__ \ CountryCodesCommonTransitListFieldNames.description).json.copyFrom(englishDescription)
        ).reduce
          .andThen(
            (__ \ Common.activeFrom).json.prune
          )
      )

  implicit val transformationCustomsOfficeList: Transformation[CustomsOfficesList.type] = {

    val customsOfficeDetailsEN: Reads[JsObject] = (__ \ "customsOfficeDetails").json.update(
      of[JsArray].flatMap[JsObject] {
        case JsArray(array) =>
          val englishDetails: Option[JsValue] = array.find(
            x => (x \ "languageCode").as[String].toLowerCase == "en"
          )
          englishDetails match {
            case Some(value: JsObject) => Reads.pure(value)
            case _                     => Reads.failed("Could not find element in array matching `en` language in path #/customsOfficeDetails")
          }

      }
    )

    Transformation
      .fromReads(
        (
          (__ \ CustomsOfficesListFieldNames.id).json.copyFrom((__ \ "referenceNumber").json.pick) and
            (__ \ Common.state).json.pickBranch and
            (__ \ Common.activeFrom).json.pickBranch and
            (__ \ "name").json.copyFrom(
              customsOfficeDetailsEN.andThen((__ \ "customsOfficeDetails" \ "customsOfficeUsualName").json.pick) orElse Reads.pure(JsNull)
            ) and
            (__ \ CustomsOfficesListFieldNames.countryId).json.copyFrom((__ \ "countryCode").json.pick) and
            (__ \ CustomsOfficesListFieldNames.phoneNumber).json.copyFrom((__ \ "phoneNumber").json.pick orElse Reads.pure(JsNull)) and
            (__ \ CustomsOfficesListFieldNames.roles).json.put(JsArray.empty)
        ).reduce
          .andThen((__ \ Common.activeFrom).json.prune)
          .andThen((__ \ Common.state).json.prune)
      )
  }

}
