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

package services

import config.ResourceConfig
import javax.inject.Inject
import models.OfficeOfTransit
import play.api.Environment

class OfficeOfTransitService @Inject()(override val env: Environment, config: ResourceConfig) extends ResourceService {

  val officesOfTransit: Seq[OfficeOfTransit] =
    getData[OfficeOfTransit](config.officeOfTransit).sortBy(_.value)

  def getOfficeOfTransit(officeValue: String): Option[OfficeOfTransit] =
    getData[OfficeOfTransit](config.officeOfTransit).find(_.value == officeValue)

  def getOfficesOfTransitByText(officeOfTransitText: String): Option[OfficeOfTransit] =
    getData[OfficeOfTransit](config.officeOfTransit).find(_.text == officeOfTransitText)

}
