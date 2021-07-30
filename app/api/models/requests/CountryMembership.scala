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

import cats.implicits._
import play.api.mvc.QueryStringBindable

sealed abstract class CountryMembership(val urlQueryValue: String, val dbValue: String)

object CountryMembership {

  case object CtcMember extends CountryMembership("ctc", "TOC")
  case object EuMember  extends CountryMembership("eu", "EEC")

  val values: Seq[CountryMembership] =
    Seq(CtcMember, EuMember)

  val mapping: Map[String, CountryMembership] =
    values
      .map(
        x => (x.urlQueryValue, x)
      )
      .toMap

  implicit val CustomOfficeRole: QueryStringBindable[CountryMembership] =
    new QueryStringBindable[CountryMembership] {

      def bind(key: String, params: Map[String, Seq[String]]): Option[Either[String, CountryMembership]] =
        Binders
          .bind[String](CountryQueryFilter.FilterKeys.membership, params)
          .flatMap[String, CountryMembership](
            x =>
              mapping.get(x) match {
                case None    => Binders.failed(s"Cannot parse parameter for ${CountryQueryFilter.FilterKeys.membership}")
                case Some(x) => Binders.successful(x)
              }
          )
          .value

      def unbind(key: String, value: CountryMembership): String =
        Binders.unbind(CountryQueryFilter.FilterKeys.membership, value.urlQueryValue)

    }
}
