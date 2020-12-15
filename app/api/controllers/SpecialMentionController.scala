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

package api.controllers

import data.DataRetrieval
import javax.inject.Inject
import logging.Logging
import models.UnDangerousGoodsCodeList
import play.api.libs.json.Json
import play.api.mvc.Action
import play.api.mvc.AnyContent
import play.api.mvc.ControllerComponents
import uk.gov.hmrc.play.bootstrap.controller.BackendController

import scala.concurrent.ExecutionContext

class SpecialMentionController @Inject() (
  cc: ControllerComponents,
  dataRetrieval: DataRetrieval
)(implicit ec: ExecutionContext)
    extends BackendController(cc)
    with Logging {

  def getAll(): Action[AnyContent] =
    Action {
      NotImplemented
    }

}
