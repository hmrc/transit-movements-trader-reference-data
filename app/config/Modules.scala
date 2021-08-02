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

import java.time.Clock
import java.time.ZoneOffset

import repositories.ListCollectionIndexManager
import api.models.schemas.CountryCodesCustomsOfficeListsSchema
import org.leadpony.justify.api.JsonValidationService
import play.api.inject._

class Modules
    extends SimpleModule(
      (_, _) => {
        val jsonValidationService: JsonValidationService = JsonValidationService.newInstance()

        /*
         *These modules are all bound eagerly so that the application will fail on application start
         *if they are misconfigured or if key resources are not available
         */
        val eagerModules = Seq(
          bind[AppConfig].toSelf,
          bind[ListCollectionIndexManager].toSelf,
          bind[CountryCodesCustomsOfficeListsSchema].toSelf,
          bind[JsonValidationService].toInstance(jsonValidationService)
        ).map(_.eagerly())

        val regularModules = Seq(
          bind[Clock].toInstance(Clock.systemDefaultZone.withZone(ZoneOffset.UTC))
        )

        eagerModules ++ regularModules
      }
    )
