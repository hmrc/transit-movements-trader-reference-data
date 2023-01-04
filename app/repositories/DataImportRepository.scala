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

import models.ReferenceDataList
import org.mongodb.scala.model.Indexes.ascending
import org.mongodb.scala.model._
import uk.gov.hmrc.mongo.MongoComponent
import uk.gov.hmrc.mongo.play.json.PlayMongoRepository

import java.time.Clock
import java.time.Instant
import javax.inject.Inject
import javax.inject.Singleton
import scala.concurrent.ExecutionContext
import scala.concurrent.Future

@Singleton
class DataImportRepository @Inject() (
  mongoComponent: MongoComponent,
  clock: Clock
)(implicit ec: ExecutionContext)
    extends PlayMongoRepository[DataImport](
      mongoComponent = mongoComponent,
      collectionName = DataImportRepository.collectionName,
      domainFormat = DataImport.format,
      indexes = DataImportRepository.indexes
    ) {

  def insert(dataImport: DataImport): Future[Boolean] =
    collection
      .insertOne(dataImport)
      .toFuture()
      .map(_.wasAcknowledged())

  def get(importId: ImportId): Future[Option[DataImport]] =
    collection
      .find(Filters.eq("importId", importId.value))
      .headOption()

  def markFinished(importId: ImportId, status: ImportStatus): Future[DataImport] = {
    val filter = Filters.eq("importId", importId.value)

    val update = Updates.combine(
      Updates.set("status", status.toString),
      Updates.set("finished", Instant.now(clock))
    )

    val options = FindOneAndUpdateOptions()
      .upsert(false)
      .returnDocument(ReturnDocument.AFTER)

    collection
      .findOneAndUpdate(filter, update, options)
      .toFuture()
  }

  def currentImportId(list: ReferenceDataList): Future[Option[ImportId]] = {
    val filter = Filters.and(
      Filters.eq("list", list.listName),
      Filters.eq("status", ImportStatus.Complete.toString)
    )

    collection
      .find(filter)
      .sort(Sorts.descending("importId"))
      .map(_.importId)
      .headOption()
  }
}

object DataImportRepository {
  val collectionName: String = "data-imports"

  val indexes: Seq[IndexModel] = {
    val importIdIndex: IndexModel =
      IndexModel(
        keys = ascending("importId"),
        indexOptions = IndexOptions().name("import-id-index").unique(true)
      )

    Seq(
      importIdIndex
    )
  }
}
