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

sealed trait CustomsOfficeRole

object CustomsOfficeRole {

  object AnyCustomsOfficeRole extends CustomsOfficeRole {
    override def toString(): String = "ANY"
  }

  implicit val CustomOfficeRole: QueryStringBindable[CustomsOfficeRole] = new QueryStringBindable[CustomsOfficeRole] {

    val mapping: Map[String, CustomsOfficeRole] = Map(
      AnyCustomsOfficeRole.toString() -> AnyCustomsOfficeRole
    )

    def bind(key: String, params: Map[String, Seq[String]]): Option[Either[String, CustomsOfficeRole]] =
      Binders
        .bind[String](CountryQueryFilter.FilterKeys.customsOfficeRole, params)
        .flatMap[String, CustomsOfficeRole] {
          x =>
            mapping.get(x) match {
              case None        => EitherT.left[CustomsOfficeRole](Option(s"Cannot parse parameter for ${CountryQueryFilter.FilterKeys.customsOfficeRole}"))
              case x @ Some(_) => EitherT.right[String](x)
            }

        }
        .value

    def unbind(key: String, value: CustomsOfficeRole): String =
      Binders.unbind(CountryQueryFilter.FilterKeys.customsOfficeRole, value.toString())

  }
}
