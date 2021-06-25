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

import models.ReferenceDataList.Constants.CountryCodesFullListFieldNames
import play.api.libs.json.JsObject

object CountryFilter {

  implicit class CountryFilter(countries: Seq[JsObject]) {

    def excludeCountries(excludedCountries: Seq[String]): Seq[JsObject] =
      excludedCountries match {
        case Nil => countries
        case _ =>
          countries
            .filterNot(jsObj => excludedCountries.map(_.toUpperCase).toSet((jsObj \ CountryCodesFullListFieldNames.code).as[String].toUpperCase))
      }
  }
}
