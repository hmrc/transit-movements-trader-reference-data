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
import api.models.requests.CustomsOfficeRole._
import api.models.requests.CountryMembership._
import ReferenceDataList.Constants._
import org.scalacheck.Gen

class CountryQueryFilterSpec extends SpecBase with ScalaCheckPropertyChecks {
  import CountryQueryFilter.FilterKeys._

  val singleCountryCodes = Seq("asdf")
  val multiCountryCodes  = Seq("asdf", "qwer", "zxcv")

  implicit val genCountryMembership: Gen[CountryMembership] = Gen.oneOf(CountryMembership.values)

  "QueryStringBindable" - {

    "binding data from url queries" - {

      "when there are no filters" in {
        val query = Map.empty[String, Seq[String]]

        val result = Binders.bind[CountryQueryFilter](customsOfficeRole, query).value.value.right.value

        result mustEqual CountryQueryFilter(None, Seq.empty, None)
      }

      "when there is one filter" - {
        "when there is a filter for customs office role" - {
          "must pass when value is ANY, then the customs office role should be defined as CustomsOfficeRoleAny" in {
            val query = Map(customsOfficeRole -> Seq("ANY"))

            val result = Binders.bind[CountryQueryFilter](customsOfficeRole, query).value.value.right.value

            result mustEqual CountryQueryFilter(Some(AnyCustomsOfficeRole), Seq.empty, None)
          }

          "must fail when there is a value other than ANY" in {
            val query  = Map(customsOfficeRole -> Seq("invalid_query_paramter"))
            val result = Binders.bind[CountryQueryFilter](customsOfficeRole, query).value.value

            result.left.value must include("Cannot parse parameter")
          }
        }

        "when there is a filter for exclude" - {
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

        "when there is a filter for membership" - {
          "and the value is for CTC" in {
            val query = Map(membership -> Seq("ctc"))

            val result = Binders.bind[CountryQueryFilter](customsOfficeRole, query).value.value.right.value

            result mustEqual CountryQueryFilter(None, Seq.empty, Some(CtcMember))
          }

          "and the value is for EU" in {
            val query = Map(membership -> Seq("eu"))

            val result = Binders.bind[CountryQueryFilter](customsOfficeRole, query).value.value.right.value

            result mustEqual CountryQueryFilter(None, Seq.empty, Some(EuMember))
          }

          "must fail when there is a value other than ANY" in {
            val query  = Map(membership -> Seq("invalid_query_paramter"))
            val result = Binders.bind[CountryQueryFilter](customsOfficeRole, query).value.value

            result.left.value must include("Cannot parse parameter")
          }
        }
      }

      "when there are two filter" - {
        "when there are filters for customs office role and exclude" in {
          val query = Map(
            exclude           -> Seq("asdf"),
            customsOfficeRole -> Seq("ANY")
          )

          val result = Binders.bind[CountryQueryFilter](customsOfficeRole, query).value.value.right.value

          result mustEqual CountryQueryFilter(Some(AnyCustomsOfficeRole), Seq("asdf"), None)
        }

        "when there are filters for customs office role and membership" - {
          "when membership is for CTC" in {
            val query = Map(
              customsOfficeRole -> Seq("ANY"),
              membership        -> Seq("ctc")
            )

            val result = Binders.bind[CountryQueryFilter](customsOfficeRole, query).value.value.right.value

            result mustEqual CountryQueryFilter(Some(AnyCustomsOfficeRole), Seq.empty, Some(CtcMember))
          }

          "when membership is for EU" in {
            val query = Map(
              customsOfficeRole -> Seq("ANY"),
              membership        -> Seq("eu")
            )

            val result = Binders.bind[CountryQueryFilter](customsOfficeRole, query).value.value.right.value

            result mustEqual CountryQueryFilter(Some(AnyCustomsOfficeRole), Seq.empty, Some(EuMember))
          }
        }

        "when there are filters for excluded countries and membership" - {
          "when membership is for CTC" in {
            val query = Map(
              exclude    -> Seq("asdf", "qwer", "zxcv"),
              membership -> Seq("ctc")
            )

            val result = Binders.bind[CountryQueryFilter](customsOfficeRole, query).value.value.right.value

            result mustEqual CountryQueryFilter(None, Seq("asdf", "qwer", "zxcv"), Some(CtcMember))
          }

          "when membership is for EU" in {
            val query = Map(
              exclude    -> Seq("asdf", "qwer", "zxcv"),
              membership -> Seq("eu")
            )

            val result = Binders.bind[CountryQueryFilter](customsOfficeRole, query).value.value.right.value

            result mustEqual CountryQueryFilter(None, Seq("asdf", "qwer", "zxcv"), Some(EuMember))
          }
        }
      }

      "when there are 3 filters" in {
        val query = Map(
          customsOfficeRole -> Seq("ANY"),
          exclude           -> Seq("asdf", "qwer", "zxcv"),
          membership        -> Seq("eu")
        )

        val result = Binders.bind[CountryQueryFilter](customsOfficeRole, query).value.value.right.value

        result mustEqual CountryQueryFilter(Some(AnyCustomsOfficeRole), Seq("asdf", "qwer", "zxcv"), Some(EuMember))
      }
    }

    "when unbinding to a url fragment" - {
      "when there are no filters" in {
        val countryQueryFilter = CountryQueryFilter(None, Seq.empty, None)

        Binders.unbind("", countryQueryFilter) mustEqual ""
      }

      "when there is one filter" - {
        "when there is a restriction on having customs offices with any role" in {
          val countryQueryFilter = CountryQueryFilter(Some(AnyCustomsOfficeRole), Seq.empty, None)

          Binders.unbind("", countryQueryFilter) mustEqual "customsOfficeRole=ANY"
        }

        "when there are excluded coutries" in {
          val countryQueryFilter = CountryQueryFilter(None, Seq("aaa", "bbb", "ccc"), None)

          Binders.unbind("", countryQueryFilter) mustEqual "exclude=aaa&exclude=bbb&exclude=ccc"
        }

        "when there is restriction on membership" in {
          forAll(genCountryMembership) {
            membership =>
              val countryQueryFilter = CountryQueryFilter(None, Seq.empty, Some(membership))

              val result = Binders.unbind("", countryQueryFilter)
              result mustEqual s"membership=${membership.urlQueryValue}"
          }
        }
      }

      "when there are two filters" - {
        "when there is a restriction on having customs offices with any role and when there are excluded coutries" in {
          val countryQueryFilter = CountryQueryFilter(Some(AnyCustomsOfficeRole), Seq("aaa", "bbb", "ccc"), None)

          Binders.unbind("", countryQueryFilter) mustEqual "customsOfficeRole=ANY&exclude=aaa&exclude=bbb&exclude=ccc"
        }

        "when there is a restriction on having customs offices with any role and there is a restriction on membership" in {
          forAll(genCountryMembership) {
            membership =>
              val countryQueryFilter = CountryQueryFilter(Some(AnyCustomsOfficeRole), Seq.empty, Some(membership))

              val result = Binders.unbind("", countryQueryFilter)
              result mustEqual s"customsOfficeRole=ANY&membership=${membership.urlQueryValue}"
          }
        }

        "when there are excluded coutries and there is a restriction on membership" in {
          forAll(genCountryMembership) {
            membership =>
              val countryQueryFilter = CountryQueryFilter(None, Seq("aaa", "bbb", "ccc"), Some(membership))

              val result = Binders.unbind("", countryQueryFilter)
              result mustEqual s"membership=${membership.urlQueryValue}&exclude=aaa&exclude=bbb&exclude=ccc"
          }
        }

      }

      "when there are three filters" - {
        "when there are is a customs office roles are excluded coutries and is a country membership filter" in {
          forAll(genCountryMembership) {
            membership =>
              val countryQueryFilter = CountryQueryFilter(Some(AnyCustomsOfficeRole), Seq("aaa", "bbb", "ccc"), Some(membership))

              val result = Binders.unbind("", countryQueryFilter)
              result mustEqual s"customsOfficeRole=ANY&membership=${membership.urlQueryValue}&exclude=aaa&exclude=bbb&exclude=ccc"
          }
        }
      }
    }
  }

  "queryParameters" - {

    "when there are no filters" in {
      val (listName, selector, projection) = CountryQueryFilter(None, Seq.empty, None).queryParamters

      listName mustEqual CountryCodesFullList
      selector mustEqual Selector.All()
      projection mustEqual None
    }

    "when there is one filter" - {
      "when there is a restriction on having customs offices with any role" in {
        val (listName, selector, projection) = CountryQueryFilter(Some(AnyCustomsOfficeRole), Seq.empty, None).queryParamters

        listName mustEqual CountryCodesCustomsOfficeLists
        selector mustEqual Selector.All()
        projection mustEqual None
      }
    }

    "when there are excluded coutries" in {
      val (listName, selector, projection) = CountryQueryFilter(None, Seq("one", "two"), None).queryParamters

      val selectorJson = (selector.expression \ CountryCodesCustomsOfficeListsFieldNames.code).as[JsObject]

      listName mustEqual CountryCodesFullList
      selectorJson mustEqual Json.obj("$nin" -> Seq("one", "two"))
      projection mustEqual None
    }

    "when there is restriction on membership when CTC" in {

      val (listName, selector, projection) = CountryQueryFilter(None, Seq.empty, Some(CtcMember)).queryParamters

      listName mustEqual CountryCodesCommonTransitList
      selector.expression mustEqual Json.obj()
      projection mustEqual None
    }

    "when there is restriction on membership when EU" in {

      val (listName, selector, projection) = CountryQueryFilter(None, Seq.empty, Some(EuMember)).queryParamters

      listName mustEqual CountryCodesCommunityList
      selector.expression mustEqual Json.obj()
      projection mustEqual None
    }

  }

  "when there are two filters" - {
    "when there is a restriction on having customs offices with any role and when there are excluded coutries" in {
      val (listName, selector, projection) = CountryQueryFilter(Some(AnyCustomsOfficeRole), Seq("one", "two"), None).queryParamters

      val selectorJson = (selector.expression \ CountryCodesCustomsOfficeListsFieldNames.code).as[JsObject]

      listName mustEqual CountryCodesCustomsOfficeLists
      selectorJson mustEqual Json.obj("$nin" -> Seq("one", "two"))
      projection mustEqual None

    }

    "when there is a restriction on having customs offices with any role and the membership being CTC" in {

      val (listName, selector, projection) = CountryQueryFilter(Some(AnyCustomsOfficeRole), Seq.empty, Some(CtcMember)).queryParamters

      listName mustEqual CountryCodesCustomsOfficeLists
      selector.expression mustEqual Json.obj(
        Common.countryRegimeCode -> Json.obj(
          "$in" -> Seq("TOC", "EEC")
        )
      )
      projection mustEqual None

    }

    "when there is a restriction on having customs offices with any role and the membership being EU" in {

      val (listName, selector, projection) = CountryQueryFilter(Some(AnyCustomsOfficeRole), Seq.empty, Some(EuMember)).queryParamters

      listName mustEqual CountryCodesCustomsOfficeLists
      selector.expression mustEqual Json.obj(
        Common.countryRegimeCode -> Json.obj(
          "$in" -> Seq("EEC")
        )
      )
      projection mustEqual None

    }

    "when there are excluded coutries and the membership is EU" in {

      val (listName, selector, projection) =
        CountryQueryFilter(None, Seq("aaa", "bbb", "ccc"), Some(EuMember)).queryParamters

      listName mustEqual CountryCodesCommunityList
      selector.expression mustEqual Json.obj(
        CountryCodesCustomsOfficeListsFieldNames.code -> Json.obj(
          "$nin" -> Seq("aaa", "bbb", "ccc")
        )
      )
      projection mustEqual None
    }

    "when there are excluded coutries and the membership is CTC" in {

      val (listName, selector, projection) =
        CountryQueryFilter(None, Seq("aaa", "bbb", "ccc"), Some(CtcMember)).queryParamters

      listName mustEqual CountryCodesCommonTransitList
      selector.expression mustEqual Json.obj(
        CountryCodesCustomsOfficeListsFieldNames.code -> Json.obj(
          "$nin" -> Seq("aaa", "bbb", "ccc")
        )
      )
      projection mustEqual None
    }
  }

  "when there are three filters" - {
    "when there are is a customs office roles are excluded coutries and there is country membership filter for CTC" in {

      val (listName, selector, projection) =
        CountryQueryFilter(Some(AnyCustomsOfficeRole), Seq("aaa", "bbb", "ccc"), Some(CtcMember)).queryParamters

      listName mustEqual CountryCodesCustomsOfficeLists
      selector.expression mustEqual Json.obj(
        Common.countryRegimeCode -> Json.obj(
          "$in" -> Seq("TOC", "EEC")
        ),
        CountryCodesCustomsOfficeListsFieldNames.code -> Json.obj(
          "$nin" -> Seq("aaa", "bbb", "ccc")
        )
      )
      projection mustEqual None
    }

    "when there are is a customs office roles are excluded coutries and there is country membership filter for EU" in {

      val (listName, selector, projection) =
        CountryQueryFilter(Some(AnyCustomsOfficeRole), Seq("aaa", "bbb", "ccc"), Some(EuMember)).queryParamters

      listName mustEqual CountryCodesCustomsOfficeLists
      selector.expression mustEqual Json.obj(
        Common.countryRegimeCode -> Json.obj(
          "$in" -> Seq("EEC")
        ),
        CountryCodesCustomsOfficeListsFieldNames.code -> Json.obj(
          "$nin" -> Seq("aaa", "bbb", "ccc")
        )
      )
      projection mustEqual None
    }
  }
}
