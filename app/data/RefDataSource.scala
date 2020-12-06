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

package data

import akka.NotUsed
import akka.stream.scaladsl.Source
import data.connector.RefDataConnector
import javax.inject.Inject
import models.ListName
import play.api.libs.json.JsObject

private[data] class RefDataSource @Inject() (refDataConnector: RefDataConnector, referenceDataJsonProjection: ReferenceDataJsonProjection)
    extends (ListName => Source[JsObject, NotUsed]) {

  def apply(listName: ListName): Source[JsObject, NotUsed] =
    Source
      .future(refDataConnector.get(listName))
      .via(referenceDataJsonProjection.dataElements)

}
