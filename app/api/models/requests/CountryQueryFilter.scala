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

import play.api.mvc.QueryStringBindable
import cats.data._
import cats.implicits._

final case class CountryQueryFilter(
  customsOffices: Boolean
)

object CountryQueryFilter {

  object FilterKeys {

    val customsOffice: String = "customs-office"

    val all: Seq[String] = Seq(customsOffice)

  }

  val noFilters: CountryQueryFilter = CountryQueryFilter(false)

  implicit def asdf: QueryStringBindable[CountryQueryFilter] =
    new QueryStringBindable[CountryQueryFilter] {

      def bind(key: String, params: Map[String, Seq[String]]): Option[Either[String, CountryQueryFilter]] =
        Binders
          .bind[Option[Boolean]](FilterKeys.customsOffice, params)
          .flatMap {
            case Some(x) => EitherT.right[String](Option(CountryQueryFilter(x)))
            case None    => EitherT.right[String](Option(CountryQueryFilter(false)))
          }
          .value

      def unbind(key: String, value: CountryQueryFilter): String = ???
    }

}
