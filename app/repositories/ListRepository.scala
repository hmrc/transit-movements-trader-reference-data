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
      _.find(selector.expression, projection = None)
        .one[JsObject]
    }

  def many[A <: ReferenceDataList](list: A, selector: Selector[A]): Future[Seq[JsObject]] =
    collection(list).flatMap {
      _.find(selector.expression, projection = None)
        .cursor[JsObject]()
        .collect[Seq](-1, Cursor.FailOnError())
    }

  def insert(list: ReferenceDataList, importId: ImportId, values: Seq[JsObject]): Future[Boolean] = {

    val enrichedValues = values.map(_ ++ Json.obj("importId" -> Json.toJson(importId)))

    collection(list).flatMap {
      _.insert(ordered = false)
        .many[JsObject](enrichedValues)
        .map(_ => true)
        .recover {
          case e: Throwable =>
            logger.error(s"Error inserting s${list.listName}", e)
            throw e
        }
    }
  }

  def deleteOldImports(list: ReferenceDataList, currentImportId: ImportId): Future[Boolean] = {

    val selector = Json.obj("importId" -> Json.obj("$lt" -> Json.toJson(currentImportId)))

    collection(list).flatMap {
      _.remove(selector)
        .map {
          result =>
            logger.info(s"Deleted ${result.n} ${list.listName} records with import ids less than $currentImportId")
            true
        }
    } recover {
      case e: Exception =>
        logger.error(s"Error trying to delete ${list.listName} data with import ids less than ${currentImportId.value}", e)
        false
    }
  }
}
