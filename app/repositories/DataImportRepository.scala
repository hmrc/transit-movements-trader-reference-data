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
import play.api.libs.json.JsObject
import play.api.libs.json.Json
import play.api.libs.json.OFormat
import play.modules.reactivemongo.ReactiveMongoApi
import reactivemongo.api.commands.LastError
import reactivemongo.play.json.collection.JSONCollection
import reactivemongo.play.json.ImplicitBSONHandlers._

import scala.concurrent.ExecutionContext
import scala.concurrent.Future

class DataImportRepository @Inject() (mongo: ReactiveMongoApi)(implicit ec: ExecutionContext) {

  val collectionName: String = "imports"
  private val duplicateError = 11000

  implicit private val dataImportFormat: OFormat[DataImport] = DataImport.mongoFormat

  // TODO: Add index on importId
  private def collection: Future[JSONCollection] =
    for {
      coll <- mongo.database.map(_.collection[JSONCollection](collectionName))
    } yield coll

  // TODO: Introduce e.g. InsertResult rather than Boolean?
  def insert(dataImport: DataImport): Future[Boolean] =
    collection.flatMap {
      _.insert(ordered = false)
        .one(dataImport)
        .map(_ => true)
    } recover {
      case e: LastError if e.code contains duplicateError => true
      case _                                              => false // TODO: Log
    }

  def get(importId: ImportId): Future[Option[DataImport]] = {

    val selector = Json.obj("importId" -> importId)

    collection.flatMap {
      _.find[JsObject, DataImport](selector, projection = None)
        .one[DataImport]
    }
  }

  def update(dataImport: DataImport): Future[Boolean] = {

    val selector = Json.obj("importId" -> dataImport.importId)
    val update   = Json.obj("$set" -> dataImport)

    collection.flatMap {
      _.findAndUpdate(selector, update, upsert = false)
        .map(_ => true) // TODO: Check update result and log if no record was updated
    }                   // TODO: Log exceptions
  }
}
