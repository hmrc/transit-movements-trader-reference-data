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

import IndexUtils.index
import javax.inject.Inject
import models._
import reactivemongo.api.bson.collection.BSONSerializationPack
import reactivemongo.api.indexes.Index.Aux
import reactivemongo.api.indexes.IndexType
import ReferenceDataList.Constants._

import scala.concurrent.ExecutionContext
import scala.concurrent.Future

class ListCollectionIndexManager @Inject() (listRepository: ListRepository)(implicit ec: ExecutionContext) {

  case class IndexOnList(list: ReferenceDataList, index: Aux[BSONSerializationPack.type])

  val indexes: List[IndexOnList] = List(
    IndexOnList(CountryCodesFullList, index(Seq("code" -> IndexType.Ascending), Some("code-index"))),
    IndexOnList(CountryCodesFullList, index(Seq("importId" -> IndexType.Ascending), Some("import-id-index"))),
    IndexOnList(
      CountryCodesFullList,
      index(Seq(Common.countryRegimeCode -> IndexType.Descending, "code" -> IndexType.Ascending), Some("countryRegimeCode-code-index"))
    ),
    IndexOnList(CountryCodesCommonTransitList, index(Seq("code" -> IndexType.Ascending), Some("code-index"))),
    IndexOnList(CountryCodesCommonTransitList, index(Seq("importId" -> IndexType.Ascending), Some("import-id-index"))),
    IndexOnList(CountryCodesCommunityList, index(Seq("code" -> IndexType.Ascending), Some("code-index"))),
    IndexOnList(CountryCodesCommunityList, index(Seq("importId" -> IndexType.Ascending), Some("import-id-index"))),
    IndexOnList(CustomsOfficesList, index(Seq("id" -> IndexType.Ascending), Some("id-index"))),
    IndexOnList(CustomsOfficesList, index(Seq("countryId" -> IndexType.Ascending), Some("country-id-index"))),
    IndexOnList(CustomsOfficesList, index(Seq("importId" -> IndexType.Ascending), Some("import-id-index"))),
    IndexOnList(CustomsOfficesList, index(Seq("roles.role" -> IndexType.Ascending), Some("customs-role-index"))),
    IndexOnList(DocumentTypeCommonList, index(Seq("code" -> IndexType.Ascending), Some("code-index"))),
    IndexOnList(DocumentTypeCommonList, index(Seq("importId" -> IndexType.Ascending), Some("import-id-index"))),
    IndexOnList(PreviousDocumentTypeCommonList, index(Seq("code" -> IndexType.Ascending), Some("code-index"))),
    IndexOnList(PreviousDocumentTypeCommonList, index(Seq("importId" -> IndexType.Ascending), Some("import-id-index"))),
    IndexOnList(KindOfPackagesList, index(Seq("code" -> IndexType.Ascending), Some("code-index"))),
    IndexOnList(KindOfPackagesList, index(Seq("importId" -> IndexType.Ascending), Some("import-id-index"))),
    IndexOnList(TransportModeList, index(Seq("code" -> IndexType.Ascending), Some("code-index"))),
    IndexOnList(TransportModeList, index(Seq("importId" -> IndexType.Ascending), Some("import-id-index"))),
    IndexOnList(AdditionalInformationIdCommonList, index(Seq("code" -> IndexType.Ascending), Some("code-index"))),
    IndexOnList(AdditionalInformationIdCommonList, index(Seq("importId" -> IndexType.Ascending), Some("import-id-index"))),
    IndexOnList(SpecificCircumstanceIndicatorList, index(Seq("code" -> IndexType.Ascending), Some("code-index"))),
    IndexOnList(SpecificCircumstanceIndicatorList, index(Seq("importId" -> IndexType.Ascending), Some("import-id-index"))),
    IndexOnList(UnDangerousGoodsCodeList, index(Seq("code" -> IndexType.Ascending), Some("code-index"))),
    IndexOnList(UnDangerousGoodsCodeList, index(Seq("importId" -> IndexType.Ascending), Some("import-id-index"))),
    IndexOnList(TransportChargesMethodOfPaymentList, index(Seq("code" -> IndexType.Ascending), Some("code-index"))),
    IndexOnList(TransportChargesMethodOfPaymentList, index(Seq("importId" -> IndexType.Ascending), Some("import-id-index"))),
    IndexOnList(ControlResultList, index(Seq("code" -> IndexType.Ascending), Some("code-index"))),
    IndexOnList(ControlResultList, index(Seq("importId" -> IndexType.Ascending), Some("import-id-index"))),
    IndexOnList(CountryCodesCommonTransitOutsideCommunityList, index(Seq("code" -> IndexType.Ascending), Some("code-index"))),
    IndexOnList(CountryCodesCommonTransitOutsideCommunityList, index(Seq("importId" -> IndexType.Ascending), Some("import-id-index"))),
    IndexOnList(CountryCodesCustomsOfficeLists, index(Seq("code" -> IndexType.Ascending), Some("code-index"))),
    IndexOnList(CountryCodesCustomsOfficeLists, index(Seq("importId" -> IndexType.Ascending), Some("import-id-index"))),
    IndexOnList(
      CountryCodesCustomsOfficeLists,
      index(Seq(Common.countryRegimeCode -> IndexType.Descending, "code" -> IndexType.Ascending), Some("countryRegimeCode-code-index"))
    )
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
