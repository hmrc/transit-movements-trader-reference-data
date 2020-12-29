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
import models.CountryCodesFullList
import models.ReferenceDataList.Constants.CountryCodesFullListFieldNames
import play.api.libs.json.Json
import play.api.mvc.Action
import play.api.mvc.AnyContent
import play.api.mvc.ControllerComponents
import repositories.Selector
import uk.gov.hmrc.play.bootstrap.backend.controller.BackendController

import scala.concurrent.ExecutionContext

trait CountryController {
  def countriesFullList(): Action[AnyContent]
  def getCountry(code: String): Action[AnyContent]
}

class CountryControllerMongo @Inject() (
  cc: ControllerComponents,
  referenceDataService: ReferenceDataService
)(implicit ec: ExecutionContext)
    extends BackendController(cc)
    with CountryController {

  def countriesFullList(): Action[AnyContent] =
    Action.async {
      referenceDataService
        .many(CountryCodesFullList, Selector.All())
        .map {
          case data if data.nonEmpty =>
            Ok(Json.toJson(data))
          case _ =>
            NotFound
        }
    }

  def getCountry(code: String): Action[AnyContent] =
    Action.async {
      referenceDataService
        .one(CountryCodesFullList, Selector.ByCode(code))
        .map {
          case Some(data) =>
            Ok(Json.toJson(data))
          case _ =>
            NotFound
        }
    }
}

class CountryControllerRemote @Inject() (
  cc: ControllerComponents,
  dataRetrieval: DataRetrieval
)(implicit ec: ExecutionContext)
    extends BackendController(cc)
    with CountryController {

  def countriesFullList(): Action[AnyContent] =
    Action.async {
      dataRetrieval
        .getList(CountryCodesFullList)
        .map(
          data => if (data.nonEmpty) Ok(Json.toJson(data)) else NotFound
        )
    }

  def getCountry(code: String): Action[AnyContent] =
    Action.async {
      dataRetrieval
        .getList(CountryCodesFullList)
        .map {
          data =>
            val response = data.find(
              json => (json \ CountryCodesFullListFieldNames.code).as[String] == code
            )

            response match {
              case Some(country) => Ok(Json.toJson(country))
              case None          => NotFound
            }
        }

    }
}
