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

package models.requests

import base.SpecBase
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks

class CustomsOfficeRoleSpec extends SpecBase with ScalaCheckPropertyChecks {
  import CountryQueryFilter.FilterKeys.customsOfficeRole

  "QueryStringBindable" - {

    "when binding data from url queries" - {
      "query paramter for customs office role" - {
        "must pass when value is ANY, then the customs office role should be defined as CustomsOfficeRoleAny" in {
          val query = Map(customsOfficeRole -> Seq("ANY"))

          val result = Binders.bind[CustomsOfficeRole](customsOfficeRole, query).value

          result.value.value mustEqual CustomsOfficeRole.AnyCustomsOfficeRole
        }

        "must fail when there is a value other than ANY" in {
          val query  = Map(customsOfficeRole -> Seq("invalid_query_paramter"))
          val result = Binders.bind[CustomsOfficeRole](customsOfficeRole, query).value.value.left.value

          result must include("Cannot parse parameter")
        }

        "must fail when there is not query values for customs office role" in {
          val query = Map.empty[String, Seq[String]]

          val result = Binders.bind[Option[CustomsOfficeRole]](customsOfficeRole, query).value

          result.value.value must not be defined
        }
      }

    }

    "when unbinding to a url fragment" - {
      "customs office must unbind to value for customsOffice" in {
        val customsOfficeRoleValue: CustomsOfficeRole = CustomsOfficeRole.AnyCustomsOfficeRole

        Binders.unbind(customsOfficeRole, customsOfficeRoleValue) mustEqual "customsOfficeRole=ANY"
      }
    }
  }

}
