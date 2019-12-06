/*
 * Copyright 2019 HM Revenue & Customs
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

package config

import javax.inject.Inject
import play.api.Configuration
import uk.gov.hmrc.play.bootstrap.config.ServicesConfig

class ResourceConfig @Inject()(config: Configuration) {

  val customsOffice: String =
    config.get[String]("resourceFiles.customsOffices")

  val countryCodes: String =
    config.get[String]("resourceFiles.countryCodesFullList")

  val transitCountryCodes: String =
    config.get[String]("resourceFiles.transitCountryCodesFullList")

  val additionalInformation: String =
    config.get[String]("resourceFiles.additionalInformation")

  val kindsOfPackage: String =
    config.get[String]("resourceFiles.kindsOfPackage")

  val documentTypes: String =
    config.get[String]("resourceFiles.documentTypes")
}
