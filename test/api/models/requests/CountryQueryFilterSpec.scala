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

class CountryQueryFilterSpec extends SpecBase with ScalaCheckPropertyChecks {
  import CountryQueryFilter.FilterKeys._

  "when binding data from url queries" - {
    "when customs-office is" - {
      "true, then the customs office filter flag should be true" in {
        val query = Map(customsOffice -> Seq("true"))

        val result = Binders.bind[CountryQueryFilter](customsOffice, query).value.value.right.value

        result mustEqual CountryQueryFilter(true)
      }

      "false, then the customs office filter flag should be false" in {
        val query = Map(customsOffice -> Seq("false"))

        val result = Binders.bind[CountryQueryFilter](customsOffice, query).value.value.right.value

        result mustEqual CountryQueryFilter(false)
      }

      "has a value that isn't valid, then it should fail to bind" - {
        "then it should sucessfully bind from query with false for customs office" in {
          val query  = Map(customsOffice -> Seq("invalid_query_paramter"))
          val result = Binders.bind[CountryQueryFilter](customsOffice, query).value.value

          result.left.value must include("Cannot parse parameter customs-office as Boolean")
        }
      }

      "is missing, then customs office filter defaults to false" - {
        val query = Map.empty[String, Seq[String]]

        val result = Binders.bind[CountryQueryFilter](customsOffice, query).value.value.right.value

        result mustEqual CountryQueryFilter(false)
      }
    }

    "when there are no filters" - {
      "then it should sucessfully bind from query with false for customs office" in {
        val query = Map.empty[String, Seq[String]]

        val result = Binders.bind[CountryQueryFilter](customsOffice, query).value.value.right.value

        result mustEqual CountryQueryFilter(false)
      }
    }

  }
}
