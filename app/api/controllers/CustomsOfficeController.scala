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

import javax.inject.Inject
import play.api.libs.json.Json
import play.api.mvc.Action
import play.api.mvc.AnyContent
import play.api.mvc.ControllerComponents
import data.DataRetrieval
import logging.Logging
import models.CustomsOfficesList
import uk.gov.hmrc.play.bootstrap.controller.BackendController

import scala.concurrent.ExecutionContext

class CustomsOfficeController @Inject() (
  cc: ControllerComponents,
  dataRetrieval: DataRetrieval
)(implicit ec: ExecutionContext)
    extends BackendController(cc)
    with Logging {

  def customsOffices(): Action[AnyContent] =
    Action.async {
      dataRetrieval
        .getList(CustomsOfficesList)
        .map {
          case data if data.nonEmpty => Ok(Json.toJson(data))
          case _ =>
            logger.error(s"No data found for ${CustomsOfficesList.listName}")
            InternalServerError
        }
    }

  def customsOfficesOfTheCountry(countryCode: String): Action[AnyContent] =
    Action.async {
      dataRetrieval
        .getList(CustomsOfficesList)
        .map {
          data =>
            val response = data.filter(
              json => (json \ "countryId").as[String] == countryCode
            )

            if (data.nonEmpty)
              Ok(Json.toJson(response))
            else
              NotFound
        }

    }

  def getCustomsOffice(officeId: String): Action[AnyContent] =
    Action.async {
      dataRetrieval
        .getList(CustomsOfficesList)
        .map {
          data =>
            val response = data
              .find(
                json => (json \ "id").as[String] == officeId
              )

            response match {
              case Some(value) => Ok(Json.toJson(value))
              case None        => NotFound
            }

        }

    }
}
