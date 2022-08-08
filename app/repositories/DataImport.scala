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

import models.ReferenceDataList
import play.api.libs.json._
import uk.gov.hmrc.mongo.play.json.formats.MongoJavatimeFormats

import java.time.Instant

case class DataImport(
  importId: ImportId,
  list: ReferenceDataList,
  records: Int,
  status: ImportStatus,
  started: Instant,
  finished: Option[Instant] = None
)

object DataImport {
  import play.api.libs.functional.syntax._

  implicit lazy val reads: Reads[DataImport] =
    (
      (__ \ "importId").read[ImportId] and
        (__ \ "list").read[ReferenceDataList] and
        (__ \ "records").read[Int] and
        (__ \ "status").read[ImportStatus] and
        (__ \ "started").read(MongoJavatimeFormats.instantReads) and
        (__ \ "finished").readNullable(MongoJavatimeFormats.instantReads)
    )(DataImport.apply _)

  implicit lazy val writes: Writes[DataImport] =
    (
      (__ \ "importId").write[ImportId] and
        (__ \ "list").write[ReferenceDataList] and
        (__ \ "records").write[Int] and
        (__ \ "status").write[ImportStatus] and
        (__ \ "started").write(MongoJavatimeFormats.instantWrites) and
        (__ \ "finished").writeNullable(MongoJavatimeFormats.instantWrites)
    )(unlift(DataImport.unapply))

  implicit val format: Format[DataImport] = Format(reads, writes)
}
