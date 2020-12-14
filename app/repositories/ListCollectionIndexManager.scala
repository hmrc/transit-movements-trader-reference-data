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

import IndexUtils.index
import javax.inject.Inject
import models.CountryCodesCommonTransitList
import models.CountryCodesFullList
import models.CustomsOfficesList
import models.ReferenceDataList
import reactivemongo.api.bson.collection.BSONSerializationPack
import reactivemongo.api.indexes.Index.Aux
import reactivemongo.api.indexes.IndexType

import scala.concurrent.ExecutionContext
import scala.concurrent.Future

class ListCollectionIndexManager @Inject() (listRepository: ListRepository)(implicit ec: ExecutionContext) {

  case class IndexOnList(list: ReferenceDataList, index: Aux[BSONSerializationPack.type])

  val indexes: List[IndexOnList] = List(
    IndexOnList(CustomsOfficesList, index(Seq("id" -> IndexType.Ascending), Some("id-index"))),
    IndexOnList(CustomsOfficesList, index(Seq("countryId" -> IndexType.Ascending), Some("country-id-index"))),
    IndexOnList(CountryCodesCommonTransitList, index(Seq("code" -> IndexType.Ascending), Some("code-index"))),
    IndexOnList(CountryCodesFullList, index(Seq("code" -> IndexType.Ascending), Some("code-index")))
  )

  val started: Future[List[Boolean]] =
    Future.sequence(
      indexes.map {
        indexOnList =>
          listRepository
            .collection(indexOnList.list)
            .flatMap(
              _.indexesManager.ensure(indexOnList.index)
            )
      }
    )
}
