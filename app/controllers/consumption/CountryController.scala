/*
 * Copyright 2022 HM Revenue & Customs
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
import models.CountryCodesFullList
import play.api.libs.json.Json
import play.api.mvc.Action
import play.api.mvc.AnyContent
import play.api.mvc.ControllerComponents
import repositories.Selector
import uk.gov.hmrc.play.bootstrap.backend.controller.BackendController
import models.requests.CountryQueryFilter

import scala.concurrent.ExecutionContext

trait CountryController {

  def get(countryQueryFilter: CountryQueryFilter): Action[AnyContent]

  def countriesFullList(): Action[AnyContent]

  def getCountry(code: String): Action[AnyContent]

}

class CountryControllerMongo @Inject() (
  cc: ControllerComponents,
  referenceDataService: ReferenceDataService
)(implicit ec: ExecutionContext)
    extends BackendController(cc)
    with CountryController {

  override def get(countryQueryFilter: CountryQueryFilter): Action[AnyContent] =
    Action.async {
      countryQueryFilter.queryParamters match {
        case (list, query, projection) =>
          referenceDataService
            .many(list, query, projection)
            .map {
              case Nil  => NotFound
              case data => Ok(Json.toJson(data))

            }
      }
    }

  override def countriesFullList(): Action[AnyContent] =
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

  override def getCountry(code: String): Action[AnyContent] =
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
