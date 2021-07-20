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

class CountryQueryFilterSpec extends SpecBase with ScalaCheckPropertyChecks {
  import CountryQueryFilter.FilterKeys._

  "QueryStringBindable" - {

    "binding data from url queries" - {
      "when there is a query paramter for customs office role only" - {
        "must pass when value is ANY, then the customs office role should be defined as CustomsOfficeRoleAny" in {
          val query = Map(customsOfficeRole -> Seq("ANY"))

          val result = Binders.bind[CountryQueryFilter](customsOfficeRole, query).value.value.right.value

          result.customsOfficesRole mustEqual Some(CustomsOfficeRole.AnyCustomsOfficeRole)
        }

        "must fail when there is a value other than ANY" in {
          val query  = Map(customsOfficeRole -> Seq("invalid_query_paramter"))
          val result = Binders.bind[CountryQueryFilter](customsOfficeRole, query).value.value

          result.left.value must include("Cannot parse parameter")
        }

      }

      "when there is a query for exclude only" - {
        "is present once, then the excluded countries should be that value" in {
          val query = Map(exclude -> Seq("asdf"))

          val result = Binders.bind[CountryQueryFilter](customsOfficeRole, query).value.value.right.value

          result.excludeCountryCodes mustEqual Seq("asdf")
        }

        "has multiple values, then the excluded countries should contain all the values" in {
          val excludeValues = Seq("asdf", "qwer", "zxcv")
          val query         = Map(exclude -> excludeValues)

          val result = Binders.bind[CountryQueryFilter](customsOfficeRole, query).value.value.right.value

          result.excludeCountryCodes must contain theSameElementsAs excludeValues
        }
      }

      "when there are a query parameters for customs office role and exclude" in {
        val query = Map(
          exclude           -> Seq("asdf"),
          customsOfficeRole -> Seq("ANY")
        )

        val result = Binders.bind[CountryQueryFilter](customsOfficeRole, query).value.value.right.value

        result.excludeCountryCodes mustEqual Seq("asdf")

      }

      "when there are no filters" - {
        "then it should sucessfully bind from query with false for customs office" in {
          val query = Map.empty[String, Seq[String]]

          val result = Binders.bind[CountryQueryFilter](customsOfficeRole, query).value

          result mustEqual None
        }
      }

    }

    "when unbinding to a url fragment" - {
      "when customsOfficeRole is defined, must unbind to value for customsOfficeRole" in {
        val countryQueryFilter = CountryQueryFilter(Some(CustomsOfficeRole.AnyCustomsOfficeRole), Seq.empty)

        Binders.unbind(customsOfficeRole, countryQueryFilter) mustEqual "customsOfficeRole=ANY"
      }

      "when customsOfficeRole is not defined, must unbind to empty string" in {
        val countryQueryFilter = CountryQueryFilter(None, Seq.empty)

        Binders.unbind(customsOfficeRole, countryQueryFilter) mustEqual ""
      }

      "excluded countries, then each contry is added as a query paramter " in {
        val countryQueryFilter = CountryQueryFilter(None, Seq("aaa", "bbb", "ccc"))

        Binders.unbind(exclude, countryQueryFilter) mustEqual "exclude=aaa&exclude=bbb&exclude=ccc"
      }
    }
  }

  "queryParameters" - {
    "when there are no restrictions on customs office roles" - {
      "when there are no excluded countries codes, then list name must be CountryCodesFullList with no selectors for roles and no projection" in {
        val (listName, selector, projection) = CountryQueryFilter(None, Seq.empty).queryParamters

        listName mustEqual CountryCodesFullList
        selector mustEqual Selector.All()
        projection mustEqual None
      }

      "when there are are excluded coutries, then list name must be CountryCodeFullList with a query that excludes those countries and no projection" in {
        val (listName, selector, projection) = CountryQueryFilter(None, Seq("one", "two")).queryParamters

        val selectorJson = (selector.expression \ ReferenceDataList.Constants.CountryCodesCustomsOfficeLists.code).as[JsObject]

        listName mustEqual CountryCodesFullList
        selectorJson mustEqual Json.obj("$nin" -> Seq("one", "two"))
        projection mustEqual None
      }
    }

    "when there must be customs offices with any role" - {
      "when there are no excluded countries then list name must be CountryCodesCustomsOfficeLists with no selectors for roles" in {
        val (listName, selector, projection) = CountryQueryFilter(Some(CustomsOfficeRole.AnyCustomsOfficeRole), Seq.empty).queryParamters

        listName mustEqual CountryCodesCustomsOfficeLists
        selector mustEqual Selector.All()
        projection mustEqual None
      }

      "when there are are excluded coutries, then list name must be CountryCodesCustomsOfficeLists with a query that excludes those countries and no projection" in {
        val (listName, selector, projection) = CountryQueryFilter(Some(CustomsOfficeRole.AnyCustomsOfficeRole), Seq("one", "two")).queryParamters

        val selectorJson = (selector.expression \ ReferenceDataList.Constants.CountryCodesCustomsOfficeLists.code).as[JsObject]

        listName mustEqual CountryCodesCustomsOfficeLists
        selectorJson mustEqual Json.obj("$nin" -> Seq("one", "two"))
        projection mustEqual None

      }
    }
  }
}
