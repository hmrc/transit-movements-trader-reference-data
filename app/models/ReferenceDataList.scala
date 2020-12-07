/*
 * Copyright 2020 HM Revenue & Customs
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

package models

import play.api.mvc.PathBindable

abstract class ReferenceDataList(val listName: String)

object ReferenceDataList {

  val values: Map[String, ReferenceDataList] =
    Seq(
      AdditionalInformationList,
      AdditionalInformationList
    ).map(x => x.listName -> x).toMap

  implicit val pathBindable: PathBindable[ReferenceDataList] = new PathBindable[ReferenceDataList] {

    override def bind(key: String, value: String): Either[String, ReferenceDataList] =
      values.get(value).toRight(s"Unknown reference data list name : $value")

    override def unbind(key: String, value: ReferenceDataList): String = value.listName
  }

}

object AdditionalInformationList extends ReferenceDataList("AdditionalInformationIdCommon")
object CircumstanceIndicatorList extends ReferenceDataList("SpecificCircumstanceIndicator")
