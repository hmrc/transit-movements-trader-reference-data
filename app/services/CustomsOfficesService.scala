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

package services

import config.ResourceConfig
import javax.inject.Inject
import models.CustomsOffice
import play.api.Environment
import play.api.libs.json.Json

import scala.io.Source

class CustomsOfficesService @Inject()(env: Environment, config: ResourceConfig) {

  private val customsOfficeFile = config.customsOffice

  val customsOffices: Seq[CustomsOffice] =
    env
      .resourceAsStream(customsOfficeFile)
      .map {
        inputStream =>
          val rawCustomsOffices = Source.fromInputStream(inputStream).mkString

          Json.parse(rawCustomsOffices).as[List[CustomsOffice]]
      }
      .getOrElse(throw new Exception(s"File not found for $customsOfficeFile"))

}
