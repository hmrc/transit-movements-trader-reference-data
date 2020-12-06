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
import akka.util.ByteString
import data.connector.RefDataConnector
import javax.inject.Inject
import models.ListName
import play.api.libs.json.JsObject

import scala.concurrent.ExecutionContext
import scala.concurrent.Future

private[data] class RefDataSource @Inject() (
  refDataConnector: RefDataConnector,
  referenceDataJsonProjection: ReferenceDataJsonProjection
)(implicit ec: ExecutionContext) {

  private def jsonByteStringToDataElements(jsonByteString: ByteString): Source[JsObject, NotUsed] =
    Source
      .single(jsonByteString)
      .via(referenceDataJsonProjection.dataElements)

  def apply(listName: ListName): Future[Option[Source[JsObject, NotUsed]]] =
    refDataConnector
      .get(listName)
      .map(_.map(jsonByteStringToDataElements))

}
