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

import controllers.testOnly.testmodels.CircumstanceIndicator
import javax.inject.Inject
import play.api.Environment

private[testOnly] class CircumstanceIndicatorService @Inject() (override val env: Environment, config: ResourceConfig) extends ResourceService {

  val circumstanceIndicators: Seq[CircumstanceIndicator] =
    getData[CircumstanceIndicator](config.circumstanceIndicators)

  def getCircumstanceIndicator(code: String): Option[CircumstanceIndicator] =
    circumstanceIndicators.find(_.code == code)
}
