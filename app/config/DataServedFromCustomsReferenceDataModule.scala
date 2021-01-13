/*
 * Copyright 2021 HM Revenue & Customs
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

import api.controllers._
import com.google.inject.AbstractModule
import logging.Logging

class DataServedFromCustomsReferenceDataModule extends AbstractModule with Logging {

  override def configure(): Unit = {

    logger.info("Binding controllers to serve data from customs-reference-data")

    bind(classOf[AdditionalInformationController]).to(classOf[AdditionalInformationControllerRemote]).asEagerSingleton()
    bind(classOf[CircumstanceIndicatorController]).to(classOf[CircumstanceIndicatorControllerRemote]).asEagerSingleton()
    bind(classOf[CountryController]).to(classOf[CountryControllerRemote]).asEagerSingleton()
    bind(classOf[CustomsOfficeController]).to(classOf[CustomsOfficeControllerRemote]).asEagerSingleton()
    bind(classOf[DangerousGoodsCodesController]).to(classOf[DangerousGoodsCodesControllerRemote]).asEagerSingleton()
    bind(classOf[DocumentTypeController]).to(classOf[DocumentTypeControllerRemote]).asEagerSingleton()
    bind(classOf[KindsOfPackageController]).to(classOf[KindsOfPackageControllerRemote]).asEagerSingleton()
    bind(classOf[MethodOfPaymentController]).to(classOf[MethodOfPaymentControllerRemote]).asEagerSingleton()
    bind(classOf[PreviousDocumentTypeController]).to(classOf[PreviousDocumentTypeControllerRemote]).asEagerSingleton()
    bind(classOf[TransitCountriesController]).to(classOf[TransitCountriesControllerRemote]).asEagerSingleton()
    bind(classOf[TransportModeController]).to(classOf[TransportModeControllerRemote]).asEagerSingleton()
  }
}
