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

package controllers.consumption

import services.ReferenceDataService
import javax.inject.Inject
import logging.Logging
import models.UnDangerousGoodsCodeList
import play.api.libs.json.Json
import play.api.mvc.Action
import play.api.mvc.AnyContent
import play.api.mvc.ControllerComponents
import repositories.Selector
import uk.gov.hmrc.play.bootstrap.backend.controller.BackendController

import scala.concurrent.ExecutionContext

trait DangerousGoodsCodesController {
  def dangerousGoodsCodes(): Action[AnyContent]
  def getDangerousGoodsCode(code: String): Action[AnyContent]
}

class DangerousGoodsCodesControllerMongo @Inject() (
  cc: ControllerComponents,
  referenceDataService: ReferenceDataService
)(implicit ec: ExecutionContext)
    extends BackendController(cc)
    with Logging
    with DangerousGoodsCodesController {

  def dangerousGoodsCodes(): Action[AnyContent] =
    Action.async {
      referenceDataService.many(UnDangerousGoodsCodeList, Selector.All()).map {
        case data if data.nonEmpty =>
          Ok(Json.toJson(data))
        case _ =>
          logger.error(s"No data found for ${UnDangerousGoodsCodeList.listName}")
          NotFound
      }
    }

  def getDangerousGoodsCode(code: String): Action[AnyContent] =
    Action.async {
      referenceDataService
        .one(UnDangerousGoodsCodeList, Selector.ByCode(code))
        .map {
          case Some(data) =>
            Ok(Json.toJson(data))
          case _ =>
            logger.info(s"No ${UnDangerousGoodsCodeList.listName} data found for code $code")
            NotFound
        }
    }
}
