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

package api.services

import javax.inject.Inject
import play.api.Configuration

class ResourceConfig @Inject() (config: Configuration) {

  val additionalInformation: String =
    config.get[String]("resourceFiles.additionalInformation")

  val kindsOfPackage: String =
    config.get[String]("resourceFiles.kindsOfPackage")

  val documentTypes: String =
    config.get[String]("resourceFiles.documentTypes")

  val transportModes: String =
    config.get[String]("resourceFiles.transportModes")

  val officeOfTransit: String =
    config.get[String]("resourceFiles.officesOfTransit")

  val previousDocumentTypes: String =
    config.get[String]("resourceFiles.previousDocumentTypes")

  val specialMention: String =
    config.get[String]("resourceFiles.specialMention")

  val methodOfPayment: String =
    config.get[String]("resourceFiles.methodOfPayment")

  val dangerousGoodsCode: String =
    config.get[String]("resourceFiles.dangerousGoodsCode")

  val circumstanceIndicators: String =
    config.get[String]("resourceFiles.circumstanceIndicators")

  val customsOffice: String =
    config.get[String]("resourceFiles.customsOffices")

  val countryCodes: String =
    config.get[String]("resourceFiles.countryCodesFullList")

  val transitCountryCodes: String =
    config.get[String]("resourceFiles.transitCountryCodesFullList")
}
