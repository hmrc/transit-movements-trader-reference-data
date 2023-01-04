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

package controllers.consumption

import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers
import play.api.libs.json.Json

class CountryFilterSpec extends AnyFreeSpec with Matchers {

  import CountryFilter._

  val country1 = Json.obj("state" -> "active", "code" -> "AA", "description" -> "aa aa aa aa")
  val country2 = Json.obj("state" -> "active", "code" -> "AB", "description" -> "aa aa aa ab")
  val country3 = Json.obj("state" -> "active", "code" -> "ac", "description" -> "aa aa aa ac")
  val country4 = Json.obj("state" -> "active", "code" -> "AD", "description" -> "aa aa aa ad")
  val country5 = Json.obj("state" -> "active", "code" -> "Ae", "description" -> "aa aa aa ae")

  val countries = Seq(
    country1,
    country2,
    country3,
    country4,
    country5
  )

  "excludeCountries" - {
    "return the entire list if an empty exclude list is present" in {
      countries.excludeCountries(Nil) must contain allElementsOf countries
    }
    "return the entire list if exclude country doesnt exist in the list" in {
      countries.excludeCountries(List("GG")) must contain allElementsOf countries
    }
    "return the filtered list if a list with one exclude item is present" in {
      countries.excludeCountries(List("AA")) must contain allElementsOf Seq(country2, country3, country4, country5)
    }
    "return the filtered list if a list with one duplicated exclude item is present" in {
      countries.excludeCountries(List("AA", "AA")) must contain allElementsOf Seq(country2, country3, country4, country5)
    }
    "return the filtered list if a list with multiple exclude items are present" in {
      countries.excludeCountries(List("AA", "AC", "AD")) must contain allElementsOf Seq(country2, country5)
    }
    "return the filtered list if codes aren't the same case" in {
      countries.excludeCountries(List("aa")) must contain allElementsOf Seq(country2, country3, country4, country5)
    }
    "return an empty list if all countries are excluded" in {
      countries.excludeCountries(List("aa", "Ab", "aC", "AD", "aE")) mustBe Nil
    }
  }
}
