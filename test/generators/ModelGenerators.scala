/*
 * Copyright 2019 HM Revenue & Customs
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

package generators

import models._
import org.scalacheck.Arbitrary
import org.scalacheck.Gen
import org.scalacheck.Arbitrary.arbitrary

trait ModelGenerators {

  implicit lazy val arbitraryCustomsOffice: Arbitrary[CustomsOffice] =
    Arbitrary {
      for {
        id    <- Gen.alphaNumStr
        name  <- Gen.alphaNumStr
        roles <- Gen.listOf(Gen.alphaNumStr)
      } yield CustomsOffice(id, name, roles)
    }

  implicit lazy val arbitraryCountryCode: Arbitrary[CountryCode] =
    Arbitrary {
      for {
        state       <- Gen.alphaNumStr
        code        <- Gen.alphaNumStr
        description <- Gen.alphaNumStr
      } yield CountryCode(state, code, description)
    }
}
