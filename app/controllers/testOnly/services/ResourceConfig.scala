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

package controllers.testOnly.services

import play.api.Configuration
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
private[testOnly] class ResourceConfig @Inject() (config: Configuration) {

  val additionalInformation: String =
    config.get[String]("resourceFiles.additionalInformation")

  val additionalInformationP5: String =
    config.get[String]("resourceFiles.additionalInformationP5")

  val additionalReference: String =
    config.get[String]("resourceFiles.additionalReference")

  val kindsOfPackage: String =
    config.get[String]("resourceFiles.kindsOfPackage")

  val kindsOfPackageP5: String =
    config.get[String]("resourceFiles.kindsOfPackageP5")

  val documentTypes: String =
    config.get[String]("resourceFiles.documentTypes")

  val documentTypesP5: String =
    config.get[String]("resourceFiles.documentTypesP5")

  val supportingDocumentTypesP5: String =
    config.get[String]("resourceFiles.supportingDocumentTypesP5")

  val transportDocumentTypesP5: String =
    config.get[String]("resourceFiles.transportDocumentTypesP5")

  val transportModes: String =
    config.get[String]("resourceFiles.transportModes")

  val nationalities: String =
    config.get[String]("resourceFiles.nationalities")

  val officeOfTransit: String =
    config.get[String]("resourceFiles.officesOfTransit")

  val previousDocumentTypes: String =
    config.get[String]("resourceFiles.previousDocumentTypes")

  val previousDocumentTypesP5: String =
    config.get[String]("resourceFiles.previousDocumentTypesP5")

  val methodOfPayment: String =
    config.get[String]("resourceFiles.methodOfPayment")

  val controlTypes: String =
    config.get[String]("resourceFiles.controlTypes")

  val functionalErrors: String =
    config.get[String]("resourceFiles.functionalErrors")

  val dangerousGoodsCode: String =
    config.get[String]("resourceFiles.dangerousGoodsCode")

  val circumstanceIndicators: String =
    config.get[String]("resourceFiles.circumstanceIndicators")

  val customsOffice: String =
    config.get[String]("resourceFiles.customsOffices")

  val customsOfficeP5: String =
    config.get[String]("resourceFiles.customsOfficesP5")

  val countryCodes: String =
    config.get[String]("resourceFiles.countryCodesFullList")

  val countryCodesV2: String =
    config.get[String]("resourceFiles.countryCodesFullListV2")

  val countryCodesCommonTransitList: String =
    config.get[String]("resourceFiles.countryCodesCommonTransitList")

  val countryCodesCommunityList: String =
    config.get[String]("resourceFiles.countryCodesCommunityList")

  val countryCodesCommonTransitOutsideCommunityList: String =
    config.get[String]("resourceFiles.countryCodesCommonTransitOutsideCommunityList")

  val countryCodesWithCustomsOffices: String =
    config.get[String]("resourceFiles.countryCodesCustomsOfficeLists")

  val nonEuCountryList: String =
    config.get[String]("resourceFiles.nonEuCountryList")

  val controlResult: String =
    config.get[String]("resourceFiles.controlResult")

  val transitCountryCodes: String =
    config.get[String]("resourceFiles.transitCountryCodesFullList")

  val countryCustomsOfficeSecurityAgreementArea: String =
    config.get[String]("resourceFiles.countryCustomsOfficeSecurityAgreementArea")

  val countryAddressPostcodeBased: String =
    config.get[String]("resourceFiles.countryAddressPostcodeBased")

  val unLocodes: String =
    config.get[String]("resourceFiles.unLocodes")

  val qualifierOfIdentificationIncident: String =
    config.get[String]("resourceFiles.qualifierOfIdentificationIncident")

  val countryWithoutZip: String =
    config.get[String]("resourceFiles.countryWithoutZip")

  val countryCodesCTC: String =
    config.get[String]("resourceFiles.countryCodesCTC")

  val currencyCodes: String =
    config.get[String]("resourceFiles.currencyCodes")

  val metrics: String =
    config.get[String]("resourceFiles.metrics")
}
