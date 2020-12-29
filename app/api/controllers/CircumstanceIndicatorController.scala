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

import api.services.ReferenceDataService
import data.DataRetrieval
import javax.inject.Inject
import logging.Logging
import models.ReferenceDataList.Constants.SpecificCountryCodesFullListFieldNames
import models.SpecificCircumstanceIndicatorList
import play.api.libs.json.Json
import play.api.mvc.Action
import play.api.mvc.AnyContent
import play.api.mvc.ControllerComponents
import repositories.Selector
import uk.gov.hmrc.play.bootstrap.backend.controller.BackendController

import scala.concurrent.ExecutionContext

trait CircumstanceIndicatorController {
  def circumstanceIndicators(): Action[AnyContent]
  def getCircumstanceIndicator(code: String): Action[AnyContent]
}

class CircumstanceIndicatorControllerMongo @Inject() (
  cc: ControllerComponents,
  referenceDataService: ReferenceDataService
)(implicit ec: ExecutionContext)
    extends BackendController(cc)
    with CircumstanceIndicatorController
    with Logging {

  def circumstanceIndicators(): Action[AnyContent] =
    Action.async {
      referenceDataService.many(SpecificCircumstanceIndicatorList, Selector.All()).map {
        case data if data.nonEmpty =>
          Ok(Json.toJson(data))
        case _ =>
          logger.error(s"No data found for ${SpecificCircumstanceIndicatorList.listName}")
          NotFound
      }
    }

  def getCircumstanceIndicator(code: String): Action[AnyContent] =
    Action.async {
      referenceDataService
        .one(SpecificCircumstanceIndicatorList, Selector.ByCode(code))
        .map {
          case Some(data) =>
            Ok(Json.toJson(data))
          case _ =>
            logger.info(s"Could not find ${SpecificCircumstanceIndicatorList.listName} with code $code")
            NotFound
        }
    }
}

class CircumstanceIndicatorControllerRemote @Inject() (
  cc: ControllerComponents,
  dataRetrieval: DataRetrieval
)(implicit ec: ExecutionContext)
    extends BackendController(cc)
    with CircumstanceIndicatorController
    with Logging {

  def circumstanceIndicators(): Action[AnyContent] =
    Action.async {
      dataRetrieval.getList(SpecificCircumstanceIndicatorList).map {
        case data if data.nonEmpty => Ok(Json.toJson(data))
        case _ =>
          logger.error(s"No data found for ${SpecificCircumstanceIndicatorList.listName}")
          NotFound
      }

    }

  def getCircumstanceIndicator(code: String): Action[AnyContent] =
    Action.async {
      dataRetrieval
        .getList(SpecificCircumstanceIndicatorList)
        .map(
          _.find(
            json => (json \ SpecificCountryCodesFullListFieldNames.code).as[String] == code
          )
        )
        .map {
          case Some(data) => Ok(Json.toJson(data))
          case _ =>
            NotFound
        }
    }
}
