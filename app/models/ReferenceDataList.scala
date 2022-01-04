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

package models

import cats.data.NonEmptyList
import play.api.libs.json.JsError
import play.api.libs.json.JsString
import play.api.libs.json.JsSuccess
import play.api.libs.json.Reads
import play.api.libs.json.Writes
import play.api.mvc.PathBindable

sealed abstract class ReferenceDataList(val listName: String)

object CountryCodesFullList                          extends ReferenceDataList("CountryCodesFullList")
object CountryCodesCommonTransitList                 extends ReferenceDataList("CountryCodesCommonTransit")
object CountryCodesCommunityList                     extends ReferenceDataList("CountryCodesCommunity")
object CustomsOfficesList                            extends ReferenceDataList("CustomsOffices")
object DocumentTypeCommonList                        extends ReferenceDataList("DocumentTypeCommon")
object PreviousDocumentTypeCommonList                extends ReferenceDataList("PreviousDocumentTypeCommon")
object KindOfPackagesList                            extends ReferenceDataList("KindOfPackages")
object TransportModeList                             extends ReferenceDataList("TransportMode")
object AdditionalInformationIdCommonList             extends ReferenceDataList("AdditionalInformationIdCommon")
object SpecificCircumstanceIndicatorList             extends ReferenceDataList("SpecificCircumstanceIndicator")
object UnDangerousGoodsCodeList                      extends ReferenceDataList("UnDangerousGoodsCode")
object TransportChargesMethodOfPaymentList           extends ReferenceDataList("TransportChargesMethodOfPayment")
object ControlResultList                             extends ReferenceDataList("ControlResult")
object CountryCodesCommonTransitOutsideCommunityList extends ReferenceDataList("CountryCodesCommonTransitOutsideCommunity")
object CountryCodesCustomsOfficeLists                extends ReferenceDataList("CountryCodesCustomsOfficeLists")

object ReferenceDataList {

  val values: NonEmptyList[ReferenceDataList] =
    NonEmptyList.of(
      CountryCodesFullList,
      CountryCodesCommonTransitList,
      CountryCodesCommunityList,
      CustomsOfficesList,
      DocumentTypeCommonList,
      PreviousDocumentTypeCommonList,
      KindOfPackagesList,
      TransportModeList,
      AdditionalInformationIdCommonList,
      SpecificCircumstanceIndicatorList,
      UnDangerousGoodsCodeList,
      TransportChargesMethodOfPaymentList,
      ControlResultList,
      CountryCodesCommonTransitOutsideCommunityList,
      CountryCodesCustomsOfficeLists
    )

  implicit val writes: Writes[ReferenceDataList] = Writes {
    list =>
      JsString(list.listName)
  }

  implicit val reads: Reads[ReferenceDataList] = Reads {
    case JsString(s) =>
      mappings
        .get(s)
        .map(JsSuccess(_))
        .getOrElse(JsError("Unknown reference data list"))

    case _ =>
      JsError("Expected a JsString")
  }

  val mappings: Map[String, ReferenceDataList] =
    values
      .map(
        x => x.listName -> x
      )
      .toList
      .toMap

  implicit val pathBindable: PathBindable[ReferenceDataList] = new PathBindable[ReferenceDataList] {

    override def bind(key: String, value: String): Either[String, ReferenceDataList] =
      mappings.get(value).toRight(s"Unknown reference data list name : $value")

    override def unbind(key: String, value: ReferenceDataList): String = value.listName
  }

  object Constants {

    object Common {
      val activeFrom        = "activeFrom"
      val state             = "state"
      val description       = "description"
      val en                = "en"
      val valid             = "valid"
      val name              = "name"
      val countryRegimeCode = "countryRegimeCode"
    }

    object CountryCodesCustomsOfficeListsFieldNames {
      val code = "code"
    }

    object ControlResultsListFieldNames {
      val code = "code"
    }

    object CountryCodesFullListFieldNames {
      val code = "code"
    }

    object CountryCodesCommonTransitListFieldNames {
      val code = "code"
    }

    object CustomsOfficesListFieldNames {
      val id          = "id"
      val name        = "name"
      val countryId   = "countryId"
      val phoneNumber = "phoneNumber"
      val roles       = "roles"
    }

    object DocumentTypeCommonListFieldNames {
      val code              = "code"
      val transportDocument = "transportDocument"
    }

    object PreviousDocumentTypeCommonListFieldNames {
      val code = "code"
    }

    object TransportModeListFieldNames {
      val code = "code"
    }

    object AdditionalInformationIdCommonListFieldNames {
      val code = "code"
    }

    object SpecificCountryCodesFullListFieldNames {
      val code = "code"
    }

    object CountryCodesCommonTransitOutsideCommunityListFieldNames {
      val code = "code"
    }
  }
}
