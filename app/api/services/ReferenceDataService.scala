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

package repositories.services

import javax.inject.Inject
import models.ReferenceDataList
import play.api.libs.json.JsObject
import repositories.DataImportRepository
import repositories.ListRepository
import repositories.Selector

import scala.concurrent.ExecutionContext
import scala.concurrent.Future

class ReferenceDataService @Inject() (
  dataImportRepository: DataImportRepository,
  listRepository: ListRepository
)(implicit ec: ExecutionContext) {

  def one[A <: ReferenceDataList](list: A, selector: Selector[A]): Future[Option[JsObject]] =
    dataImportRepository.currentImportId(list).flatMap {
      case Some(importId) =>
        listRepository.one(list, selector.forImport(importId))
      case None =>
        Future.successful(None)
    }

  def many[A <: ReferenceDataList](list: A, selector: Selector[A]): Future[Seq[JsObject]] =
    dataImportRepository.currentImportId(list).flatMap {
      case Some(importId) =>
        listRepository.many(list, selector.forImport(importId))
      case None =>
        Future.successful(Nil)
    }
}
