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

package scheduler

import java.time.Clock
import java.time.Instant

import data.DataRetrieval
import data.transform.Transformation
import javax.inject.Inject
import models.AdditionalInformationList
import models.CountryCodesCommonTransitList
import models.CountryCodesFullList
import models.CustomsOfficesList
import models.ReferenceDataList
import repositories.DataImport
import repositories.DataImportRepository
import repositories.ImportId
import repositories.ImportIdRepository
import repositories.ImportStatus
import repositories.ListRepository

import scala.concurrent.ExecutionContext
import scala.concurrent.Future

class DataImportService @Inject() (
  importIdRepository: ImportIdRepository,
  dataImportRepository: DataImportRepository,
  listRepository: ListRepository,
  dataRetrieval: DataRetrieval,
  clock: Clock
) {

  def importReferenceData()(implicit ec: ExecutionContext): Future[DataImport] =
    for {
      importId <- importIdRepository.nextId
      dataImport = DataImport(importId, ImportStatus.Started, Instant.now(clock))
      _             <- dataImportRepository.insert(dataImport)
      importResults <- importLists(importId)
      overallStatus = if (importResults.contains(false)) ImportStatus.Failed else ImportStatus.Complete
      updateResult <- dataImportRepository.markFinished(importId, overallStatus)
      // TODO: Delete old versions of data?  This would again need to hit multiple collections
    } yield updateResult

  // TODO: Can (and *should*) we source this list from elsewhere?  We need evidence of the transformation existing
  private def importLists(importId: ImportId)(implicit ec: ExecutionContext): Future[List[Boolean]] =
    Future.sequence(
      List(
        importList(CustomsOfficesList, importId),
        importList(CountryCodesFullList, importId),
        importList(CountryCodesCommonTransitList, importId)
      )
    )

  private def importList[A <: ReferenceDataList](list: A, importId: ImportId)(implicit ec: ExecutionContext, ev: Transformation[A]): Future[Boolean] =
    for {
      data   <- dataRetrieval.getList(list)
      result <- listRepository.insert(list, importId, data)
    } yield result
}
