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

package repositories

import java.time.Clock
import java.time.Instant

import javax.inject.Inject
import models.ReferenceDataList
import play.api.Logging
import play.api.libs.json.JsObject
import play.api.libs.json.Json
import play.api.libs.json.OFormat
import play.modules.reactivemongo.ReactiveMongoApi
import reactivemongo.api.indexes.IndexType
import reactivemongo.play.json.collection.JSONCollection
import reactivemongo.play.json.ImplicitBSONHandlers._

import scala.concurrent.ExecutionContext
import scala.concurrent.Future

class DataImportRepository @Inject() (mongo: ReactiveMongoApi, clock: Clock)(implicit ec: ExecutionContext) extends Logging {

  val collectionName: String = "data-imports"

  implicit private val dataImportFormat: OFormat[DataImport] = DataImport.mongoFormat

  private val importIdIndex =
    IndexUtils.index(
      key = Seq("importId" -> IndexType.Ascending),
      name = Some("import-id-index"),
      unique = true
    )

  private def collection: Future[JSONCollection] =
    for {
      coll <- mongo.database.map(_.collection[JSONCollection](collectionName))
      _    <- coll.indexesManager.ensure(importIdIndex)
    } yield coll

  // TODO: Introduce e.g. InsertResult rather than Boolean?
  def insert(dataImport: DataImport): Future[Boolean] =
    collection.flatMap {
      _.insert(ordered = false)
        .one(dataImport)
        .map(_ => true)
    } recover {
      case e: Throwable =>
        logger.error("Error creating a DataImport record", e)
        false
    }

  def get(importId: ImportId): Future[Option[DataImport]] = {

    val selector = Json.obj("importId" -> importId)

    collection.flatMap {
      _.find[JsObject, DataImport](selector, projection = None)
        .one[DataImport]
    }
  }

  def markFinished(importId: ImportId, status: ImportStatus): Future[DataImport] = {

    import MongoInstantFormats._

    val selector = Json.obj("importId" -> Json.toJson(importId))

    val update = Json.obj(
      "$set" -> Json.obj(
        "status"   -> Json.toJson(status),
        "finished" -> Json.toJson(Instant.now(clock))
      )
    )

    collection.flatMap {
      _.findAndUpdate(selector, update, upsert = false, fetchNewObject = true)
        .map {
          _.result[DataImport].getOrElse(throw new Exception(s"Unable to mark import ${importId.value} as finished"))
        }
    }
  }

  def currentImportId(list: ReferenceDataList): Future[Option[ImportId]] = {

    val selector = Json.obj(
      "list"   -> list.listName,
      "status" -> Json.toJson(ImportStatus.Complete)
    )

    val byMostRecent = Json.obj("importId" -> -1)

    collection.flatMap {
      _.find[JsObject, DataImport](selector, projection = None)
        .sort(byMostRecent)
        .one[DataImport]
        .map(_.map(_.importId))
    }
  }
}
