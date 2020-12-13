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

package repositories

import javax.inject.Inject
import logging.Logging
import models.ReferenceDataList
import play.api.libs.json.JsObject
import play.api.libs.json.Json
import play.modules.reactivemongo.ReactiveMongoApi
import reactivemongo.api.Cursor
import reactivemongo.api.commands.LastError
import reactivemongo.play.json.collection.Helpers.idWrites
import reactivemongo.play.json.collection.JSONCollection

import scala.concurrent.ExecutionContext
import scala.concurrent.Future

class ListRepository @Inject() (mongo: ReactiveMongoApi)(implicit ec: ExecutionContext) extends Logging {

  private val duplicateErrorCode = 11000

  def collection(list: ReferenceDataList): Future[JSONCollection] =
    mongo.database.map(_.collection[JSONCollection](list.listName))

  def one[A <: ReferenceDataList](list: A, selector: Selector[A]): Future[Option[JsObject]] =
    collection(list).flatMap {
      _.find(selector.fullExpression, projection = None)
        .one[JsObject]
    }

  def many[A <: ReferenceDataList](list: A, selector: Selector[A]): Future[Seq[JsObject]] =
    collection(list).flatMap {
      _.find(selector.fullExpression, projection = None)
        .cursor[JsObject]()
        .collect[Seq](-1, Cursor.FailOnError())
    }

  // TODO: Better logging
  def insert(list: ReferenceDataList, importId: ImportId, values: Seq[JsObject]): Future[Boolean] = {

    val enrichedValues = values.map(_ ++ Json.obj("importId" -> Json.toJson(importId)))

    collection(list).flatMap {
      _.insert(ordered = false)
        .many[JsObject](enrichedValues)
        .map(_ => true)
        .recover {
          case e: LastError if e.code contains duplicateErrorCode =>
            logger.warn(s"Tried to insert duplicate values for ${list.listName}")
            true
        }
    }
  }
}
