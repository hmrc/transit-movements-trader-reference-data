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

package api.models.requests

import base.SpecBase
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import models._
import repositories.Selector
import play.api.libs.json.Json
import play.api.libs.json.JsObject
import org.scalacheck.Arbitrary

class CountryQueryFilterSpec extends SpecBase with ScalaCheckPropertyChecks {
  import CountryQueryFilter.FilterKeys._

  "QueryStringBindable" - {

    "when binding data from url queries" - {
      "when customs office is" - {
        "true, then the customs office filter flag should be true" in {
          val query = Map(customsOffice -> Seq("true"))

          val result = Binders.bind[CountryQueryFilter](customsOffice, query).value.value.right.value

          result.customsOffices mustEqual true
        }

        "false, then the customs office filter flag should be false" in {
          val query = Map(customsOffice -> Seq("false"))

          val result = Binders.bind[CountryQueryFilter](customsOffice, query).value.value.right.value

          result.customsOffices mustEqual false
        }

        "has a value that isn't valid, then it should fail to bind" - {
          "then it should sucessfully bind from query with false for customs office" in {
            val query  = Map(customsOffice -> Seq("invalid_query_paramter"))
            val result = Binders.bind[CountryQueryFilter](customsOffice, query).value.value

            result.left.value must include("Cannot parse parameter")
          }
        }

        "is missing, then customs office filter defaults to false" in {
          val query = Map.empty[String, Seq[String]]

          val result = Binders.bind[CountryQueryFilter](customsOffice, query).value.value.right.value

          result.customsOffices mustEqual false
        }
      }

      "when excludeCountries" - {
        "is present once, then the excluded countries should be that value" in {
          val query = Map(excludeCountries -> Seq("asdf"))

          val result = Binders.bind[CountryQueryFilter](customsOffice, query).value.value.right.value

          result.excludeCountries mustEqual Seq("asdf")
        }

        "has multiple values, then the excluded countries should contain all the values" in {
          val excludeCountriesValues = Seq("asdf", "qwer", "zxcv")
          val query                  = Map(excludeCountries -> excludeCountriesValues)

          val result = Binders.bind[CountryQueryFilter](customsOffice, query).value.value.right.value

          result.excludeCountries must contain theSameElementsAs excludeCountriesValues
        }

        "is missing, then the excluded countries must be empty" in {
          val query = Map.empty[String, Seq[String]]

          val result = Binders.bind[CountryQueryFilter](customsOffice, query).value.value.right.value

          result.excludeCountries mustEqual Seq.empty
        }
      }

      "when there are no filters" - {
        "then it should sucessfully bind from query with false for customs office" in {
          val query = Map.empty[String, Seq[String]]

          val result = Binders.bind[CountryQueryFilter](customsOffice, query).value.value.right.value

          result mustEqual CountryQueryFilter(false, Seq.empty)
        }
      }

    }

    "when unbinding to a url fragment" - {
      "customs office must unbind to value for customsOffice" in {
        val countryQueryFilter = CountryQueryFilter(false, Seq.empty)

        Binders.unbind(customsOffice, countryQueryFilter) mustEqual "customsOffice=false"
      }

      "excluded countries, then each contry is added as a query paramter " in {
        val countryQueryFilter = CountryQueryFilter(false, Seq("aaa", "bbb", "ccc"))

        Binders.unbind(excludeCountries, countryQueryFilter) mustEqual "excludeCountries=aaa&excludeCountries=bbb&excludeCountries=ccc"
      }
    }
  }

  "queryParameters" - {

    "when customs office is false" - {
      "list name must be CountryCodesFullList" in {
        CountryQueryFilter(false, Seq.empty).queryParamters.value._1 mustEqual CountryCodesFullList
      }

      "query must be for all elements" in {
        CountryQueryFilter(false, Seq.empty).queryParamters.value._2 mustEqual Selector.All()
      }

      "projection must be None" in {
        CountryQueryFilter(false, Seq.empty).queryParamters.value._3 mustEqual None
      }
    }

    "when customs office is true" - {
      "list name must be CountryCodesFullList" in {
        CountryQueryFilter(true, Seq.empty).queryParamters.value._1 mustEqual CountryCodesCustomsOfficeLists
      }

      "query must be for all elements" in {
        CountryQueryFilter(true, Seq.empty).queryParamters.value._2 mustEqual Selector.All()
      }
    }

    "when excludedCoutries is nonempty query must return a query that excludes those countries" in {
      forAll(Arbitrary.arbitrary[Boolean]) {
        booleanVal =>
          val sut: JsObject = CountryQueryFilter(booleanVal, Seq("one", "two")).queryParamters.value._2.expression

          val result = (sut \ ReferenceDataList.Constants.CountryCodesCustomsOfficeLists.code).as[JsObject]

          result mustEqual Json.obj("$nin" -> Seq("one", "two"))

      }
    }
  }
}
