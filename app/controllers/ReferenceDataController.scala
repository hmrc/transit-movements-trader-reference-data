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

package controllers

import javax.inject.Inject
import play.api.libs.json.Json
import play.api.mvc.Action
import play.api.mvc.AnyContent
import play.api.mvc.ControllerComponents
import services._
import uk.gov.hmrc.play.bootstrap.controller.BackendController

class ReferenceDataController @Inject()(
  cc: ControllerComponents,
  countryCodesService: CountryCodesService,
  customsOfficesService: CustomsOfficesService,
  transitCountryCodesService: TransitCountryCodesService,
  additionalInformationService: AdditionalInformationService,
  kindOfPackagesService: KindOfPackageService,
  documentTypeService: DocumentTypeService
) extends BackendController(cc) {

  def customsOffices(): Action[AnyContent] = Action {

    Ok(Json.toJson(customsOfficesService.customsOffices))
  }

  def countryCodeFullList(): Action[AnyContent] = Action {
    Ok(Json.toJson(countryCodesService.countryCodes))
  }

  def transitCountryCodeList(): Action[AnyContent] = Action {
    Ok(Json.toJson(transitCountryCodesService.transitCountryCodes))
  }

  def additionalInformation(): Action[AnyContent] = Action {
    Ok(Json.toJson(additionalInformationService.additionalInformation))
  }

  def kindsOfPackage(): Action[AnyContent] = Action {
    Ok(Json.toJson(kindOfPackagesService.kindsOfPackage))
  }

  def documentTypes(): Action[AnyContent] = Action {
    Ok(Json.toJson(documentTypeService.documentTypes))
  }
}
