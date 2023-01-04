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

package repositories

import models.CustomsOfficesList
import models.ReferenceDataList
import org.bson.conversions.Bson
import org.mongodb.scala.model.Projections

trait Projection[A] {
  self =>

  val expression: Bson

  def and[B <: A](other: Projection[B]): Projection[B] =
    new Projection[B] {
      val expression: Bson = Projections.fields(self.expression, other.expression)
    }

  def toOption: Option[Projection[A]] = Some(self)
}

object Projection {

  case object SuppressId extends Projection[ReferenceDataList] {
    override val expression: Bson = Projections.excludeId()
  }

  case object SuppressRoles extends Projection[CustomsOfficesList.type] {
    override val expression: Bson = Projections.exclude("roles")
  }
}
