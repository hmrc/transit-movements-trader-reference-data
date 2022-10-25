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

package controllers.testOnly.services

import play.api.Configuration
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
private[testOnly] class ResourceConfig @Inject() (config: Configuration) {

  val additionalInformation: String =
    config.get[String]("resourceFiles.additionalInformation")

  val kindsOfPackage: String =
    config.get[String]("resourceFiles.kindsOfPackage")

  val documentTypes: String =
    config.get[String]("resourceFiles.documentTypes")

  val transportModes: String =
    config.get[String]("resourceFiles.transportModes")

  val transportMeans: String =
    config.get[String]("resourceFiles.transportMeans")

  val nationalities: String =
    config.get[String]("resourceFiles.nationalities")

  val officeOfTransit: String =
    config.get[String]("resourceFiles.officesOfTransit")

  val previousDocumentTypes: String =
    config.get[String]("resourceFiles.previousDocumentTypes")

  val methodOfPayment: String =
    config.get[String]("resourceFiles.methodOfPayment")

  val dangerousGoodsCode: String =
    config.get[String]("resourceFiles.dangerousGoodsCode")

  val circumstanceIndicators: String =
    config.get[String]("resourceFiles.circumstanceIndicators")

  val customsOffice: String =
    config.get[String]("resourceFiles.customsOffices")

  val customsOfficeDeparture: String =
    config.get[String]("resourceFiles.customsOfficeDeparture")

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

  val customsOfficeTransit: String =
    config.get[String]("resourceFiles.customsOfficeTransit")

  val customsOfficeDestination: String =
    config.get[String]("resourceFiles.customsOfficeDestination")

  val customsOfficeExit: String =
    config.get[String]("resourceFiles.customsOfficeExit")

  val customsOfficeTransitExit: String =
    config.get[String]("resourceFiles.customsOfficeTransitExit")

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

}
