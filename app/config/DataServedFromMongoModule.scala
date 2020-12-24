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

package config

import api.controllers._
import com.google.inject.AbstractModule
import logging.Logging

class DataServedFromMongoModule extends AbstractModule with Logging {

  override def configure(): Unit = {

    logger.info("Binding controllers to serve data from local Mongo")

    bind(classOf[AdditionalInformationController]).to(classOf[AdditionalInformationControllerMongo]).asEagerSingleton()
    bind(classOf[CircumstanceIndicatorController]).to(classOf[CircumstanceIndicatorControllerMongo]).asEagerSingleton()
    bind(classOf[CountryController]).to(classOf[CountryControllerMongo]).asEagerSingleton()
    bind(classOf[CustomsOfficeController]).to(classOf[CustomsOfficeControllerMongo]).asEagerSingleton()
    bind(classOf[DangerousGoodsCodesController]).to(classOf[DangerousGoodsCodesControllerMongo]).asEagerSingleton()
    bind(classOf[DocumentTypeController]).to(classOf[DocumentTypeControllerMongo]).asEagerSingleton()
    bind(classOf[KindsOfPackageController]).to(classOf[KindsOfPackageControllerMongo]).asEagerSingleton()
    bind(classOf[MethodOfPaymentController]).to(classOf[MethodOfPaymentControllerMongo]).asEagerSingleton()
    bind(classOf[PreviousDocumentTypeController]).to(classOf[PreviousDocumentTypeControllerMongo]).asEagerSingleton()
    bind(classOf[TransitCountriesController]).to(classOf[TransitCountriesControllerMongo]).asEagerSingleton()
    bind(classOf[TransportModeController]).to(classOf[TransportModeControllerMongo]).asEagerSingleton()
  }
}
