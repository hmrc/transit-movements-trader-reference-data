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

package api.controllers

import api.services.ReferenceDataService
import data.DataRetrieval
import logging.Logging
import models.CustomsOfficesList
import play.api.libs.json.Json
import play.api.mvc.Action
import play.api.mvc.AnyContent
import play.api.mvc.ControllerComponents
import repositories.Projection.SuppressRoles
import repositories.Projection
import repositories.Selector
import repositories.Selector.OptionallyByRole
import uk.gov.hmrc.play.bootstrap.backend.controller.BackendController

import javax.inject.Inject
import scala.concurrent.ExecutionContext

trait CustomsOfficeController {
  def customsOffices(roles: Seq[String] = Nil): Action[AnyContent]
  def customsOfficesOfTheCountry(countryId: String, roles: Seq[String] = Nil): Action[AnyContent]
  def getCustomsOffice(officeId: String): Action[AnyContent]
}

class CustomsOfficeControllerMongo @Inject() (
  cc: ControllerComponents,
  referenceDataService: ReferenceDataService
)(implicit ec: ExecutionContext)
    extends BackendController(cc)
    with CustomsOfficeController
    with Logging {

  def customsOffices(roles: Seq[String]): Action[AnyContent] =
    Action.async {
      referenceDataService
        .many(CustomsOfficesList, Selector.All() and OptionallyByRole(roles), Projection.SuppressId and SuppressRoles toOption)
        .map {
          case data if data.nonEmpty =>
            Ok(Json.toJson(data))
          case _ =>
            logger.error(s"No data found for ${CustomsOfficesList.listName}")
            NotFound
        }
    }

  def customsOfficesOfTheCountry(countryId: String, roles: Seq[String]): Action[AnyContent] =
    Action.async {
      referenceDataService
        .many(CustomsOfficesList, Selector.ByCountry(countryId) and OptionallyByRole(roles), (Projection.SuppressId and SuppressRoles) toOption)
        .map {
          case data if data.nonEmpty =>
            Ok(Json.toJson(data))
          case _ =>
            logger.info(s"No ${CustomsOfficesList.listName} data found for country $countryId")
            NotFound
        }
    }

  def getCustomsOffice(officeId: String): Action[AnyContent] =
    Action.async {
      referenceDataService
        .one(CustomsOfficesList, Selector.ById(officeId), (Projection.SuppressId and SuppressRoles).toOption)
        .map {
          case Some(value) =>
            Ok(Json.toJson(value))
          case None =>
            logger.info(s"No ${CustomsOfficesList.listName} data found for id $officeId")
            NotFound
        }
    }
}

class CustomsOfficeControllerRemote @Inject() (
  cc: ControllerComponents,
  dataRetrieval: DataRetrieval
)(implicit ec: ExecutionContext)
    extends BackendController(cc)
    with Logging
    with CustomsOfficeController {

  def customsOffices(roles: Seq[String]): Action[AnyContent] =
    Action.async {
      dataRetrieval
        .getList(CustomsOfficesList)
        .map {
          case data if data.nonEmpty => Ok(Json.toJson(data))
          case _ =>
            logger.error(s"No data found for ${CustomsOfficesList.listName}")
            NotFound
        }
    }

  def customsOfficesOfTheCountry(countryCode: String, roles: Seq[String]): Action[AnyContent] =
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
