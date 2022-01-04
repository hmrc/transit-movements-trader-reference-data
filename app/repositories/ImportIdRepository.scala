/*
 * Copyright 2022 HM Revenue & Customs
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
import javax.inject.Singleton
import play.api.libs.json.Json
import play.api.libs.json.Reads
import play.api.libs.json.__
import play.modules.reactivemongo.ReactiveMongoApi
import reactivemongo.api.commands.LastError
import reactivemongo.play.json.collection.Helpers.idWrites
import reactivemongo.play.json.collection.JSONCollection

import scala.concurrent.ExecutionContext
import scala.concurrent.Future

@Singleton
class ImportIdRepository @Inject() (mongo: ReactiveMongoApi)(implicit ec: ExecutionContext) {

  val recordId: String  = "last-id"
  val fieldName: String = "import-id"
  val startingSeed: Int = 0

  implicit private val importIdReads: Reads[ImportId] =
    (__ \ fieldName)
      .read[Int]
      .map(
        x => ImportId(x)
      )

  private def collection: Future[JSONCollection] =
    for {
      _    <- seed
      coll <- mongo.database.map(_.collection[JSONCollection](ImportIdRepository.collectionName))
    } yield coll

  private val seed: Future[Boolean] = {

    val document       = Json.obj("_id" -> recordId, fieldName -> startingSeed)
    val documentExists = 11000

    def coll: Future[JSONCollection] =
      mongo.database.map(_.collection[JSONCollection](ImportIdRepository.collectionName))

    coll.flatMap {
      _.insert(ordered = false)
        .one(document)
        .map(
          _ => true
        )
    } recover {
      case e: LastError if e.code contains documentExists =>
        true
    }
  }

  def nextId: Future[ImportId] = {

    val selector = Json.obj("_id" -> recordId)

    val update = Json.obj("$inc" -> Json.obj(fieldName -> 1))

    collection.flatMap {
      _.findAndUpdate(selector, update, fetchNewObject = true)
        .map {
          _.result[ImportId]
            .getOrElse(throw new Exception("Unable to generate the next import id"))
        }
    }
  }
}

object ImportIdRepository {
  val collectionName: String = "import-ids"
}
