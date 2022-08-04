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
import logging.Logging
import models.ReferenceDataList.Constants.Common
import models.{AdditionalInformationIdCommonList, ControlResultList, CountryCodesCommonTransitList, CountryCodesCommonTransitListVersion2, CountryCodesCommonTransitOutsideCommunityList, CountryCodesCommunityList, CountryCodesCustomsOfficeLists, CountryCodesFullList, CustomsOfficesList, DocumentTypeCommonList, KindOfPackagesList, PreviousDocumentTypeCommonList, ReferenceDataList, SpecificCircumstanceIndicatorList, TransportChargesMethodOfPaymentList, TransportModeList, UnDangerousGoodsCodeList}
import play.api.libs.json.JsObject
import play.api.libs.json.Json
import play.modules.reactivemongo.ReactiveMongoApi
import reactivemongo.api.Cursor
import reactivemongo.api.bson.collection.BSONSerializationPack
import reactivemongo.api.indexes.Index.Aux
import reactivemongo.api.indexes.IndexType
import reactivemongo.play.json.collection.Helpers.idWrites
import reactivemongo.play.json.collection.JSONCollection
import repositories.IndexUtils.index

import scala.concurrent.ExecutionContext
import scala.concurrent.Future

@Singleton
class ListRepository @Inject() (mongo: ReactiveMongoApi)(implicit ec: ExecutionContext) extends Logging {

  private def innerCollection(list: ReferenceDataList): Future[JSONCollection] = mongo.database.map(_.collection[JSONCollection](list.listName))

  def collection(list: ReferenceDataList): Future[JSONCollection] =
    started.flatMap {
      _ => innerCollection(list)
    }

  def one[A <: ReferenceDataList, B <: A](list: A, selector: Selector[A], projection: Option[Projection[B]] = None): Future[Option[JsObject]] =
    collection(list).flatMap {
      _.find(selector.expression, projection = projection)
        .one[JsObject]
    }

  def many[A <: ReferenceDataList, B <: A](list: A, selector: Selector[A], projection: Option[Projection[B]] = None): Future[Seq[JsObject]] =
    collection(list).flatMap {
      _.find(selector.expression, projection = projection)
        .cursor[JsObject]()
        .collect[Seq](-1, Cursor.FailOnError())
    }

  def insert(list: ReferenceDataList, importId: ImportId, values: Seq[JsObject]): Future[Boolean] = {

    val enrichedValues = values.map(_ ++ Json.obj("importId" -> Json.toJson(importId)))

    collection(list).flatMap {
      _.insert(ordered = false)
        .many[JsObject](enrichedValues)
        .map(
          _ => true
        )
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

  private lazy val started: Future[List[Boolean]] =
    Future.sequence(
      ListRepository.indexes.map {
        indexOnList =>
          innerCollection(indexOnList.list)
            .flatMap(
              _.indexesManager.ensure(indexOnList.index)
            )
      }
    )
}

object ListRepository {
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
    IndexOnList(CountryCodesCommonTransitListVersion2, index(Seq("code" -> IndexType.Ascending), Some("code-index"))),
    IndexOnList(CountryCodesCommonTransitListVersion2, index(Seq("importId" -> IndexType.Ascending), Some("import-id-index"))),
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
}
