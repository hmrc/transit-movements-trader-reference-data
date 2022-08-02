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
import models.requests.CountryMembership._
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks

class CountryMembershipSpec extends SpecBase with ScalaCheckPropertyChecks {
  import CountryQueryFilter.FilterKeys.membership

  "QueryStringBindable" - {

    "when binding data from url queries" - {
      "query paramter for country membership" - {
        "when value is ctc, must successfully bind and return CtcMembership" in {
          val query = Map(membership -> Seq("ctc"))

          val result = Binders.bind[CountryMembership](membership, query).value

          result mustEqual Some(Right(CtcMember))
        }

        "when value is eu, must successfully bind and return EuMembership" in {
          val query = Map(membership -> Seq("eu"))

          val result = Binders.bind[CountryMembership](membership, query).value

          result mustEqual Some(Right(EuMember))
        }

        "must fail when the value is not recognised" in {
          val query  = Map(membership -> Seq("invalid_query_paramter"))
          val result = Binders.bind[CountryMembership](membership, query).value.value.left.value

          result must include("Cannot parse parameter")
        }

        "must fail when there is not query values for customs office role" in {
          val query = Map.empty[String, Seq[String]]

          val result = Binders.bind[Option[CountryMembership]](membership, query).value

          result mustEqual Some(Right(None))
        }
      }

    }

    "when unbinding to a url fragment" - {
      "CtcMembership must unbind to value for membership" in {
        val sut = CtcMember

        Binders.unbind[CountryMembership](membership, sut) mustEqual "membership=ctc"
      }

      "EuMembership must unbind to value for membership" in {
        val sut = EuMember

        Binders.unbind[CountryMembership](membership, sut) mustEqual "membership=eu"
      }
    }
  }

}
