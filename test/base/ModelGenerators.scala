/*
 * Copyright 2023 HM Revenue & Customs
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

package base

import controllers.testOnly.testmodels.ControlType
import controllers.testOnly.testmodels.Country
import controllers.testOnly.testmodels.CustomsOffice
import controllers.testOnly.testmodels.DangerousGoodsCode
import controllers.testOnly.testmodels.OfficeOfTransit
import org.scalacheck.Arbitrary
import org.scalacheck.Gen

trait ModelGenerators {

  implicit lazy val arbitraryCustomsOffice: Arbitrary[CustomsOffice] =
    Arbitrary {
      for {
        id        <- Gen.alphaNumStr
        name      <- Gen.alphaNumStr
        countryId <- Gen.alphaNumStr
        telephone <- Gen.option(Gen.alphaNumStr)
        roles     <- Gen.listOf(Gen.alphaNumStr)
      } yield CustomsOffice(id, name, countryId, telephone, roles)
    }

  implicit lazy val arbitraryOfficeOfTransit: Arbitrary[OfficeOfTransit] =
    Arbitrary {
      for {
        id   <- Gen.alphaNumStr
        name <- Gen.alphaNumStr
      } yield OfficeOfTransit(id, name)
    }

  implicit lazy val arbitraryControlType: Arbitrary[ControlType] =
    Arbitrary {
      for {
        code        <- Gen.alphaNumStr
        description <- Gen.alphaNumStr
      } yield ControlType(code, description)
    }

  implicit lazy val arbitraryCountry: Arbitrary[Country] =
    Arbitrary {
      for {
        state       <- Gen.alphaNumStr
        code        <- Gen.alphaNumStr
        description <- Gen.alphaNumStr
      } yield Country(state, code, description)
    }

  implicit lazy val arbitraryDangerousGoodsCode: Arbitrary[DangerousGoodsCode] =
    Arbitrary {
      for {
        code        <- Gen.alphaNumStr
        description <- Gen.alphaNumStr
      } yield DangerousGoodsCode(code, description)
    }
}
