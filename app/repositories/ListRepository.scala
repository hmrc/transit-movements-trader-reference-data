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

import logging.Logging
import models.ReferenceDataList.Constants.Common
import models._
import org.mongodb.scala.bson.BsonDocument
import org.mongodb.scala.model.Filters
import org.mongodb.scala.model.IndexModel
import org.mongodb.scala.model.IndexOptions
import org.mongodb.scala.model.Indexes._
import play.api.libs.json.Format
import play.api.libs.json.JsObject
import play.api.libs.json.Json
import uk.gov.hmrc.mongo.MongoComponent
import uk.gov.hmrc.mongo.play.json.PlayMongoRepository

import javax.inject.Inject
import javax.inject.Singleton
import scala.concurrent.ExecutionContext
import scala.concurrent.Future

class ListRepository @Inject() (
  mongoComponent: MongoComponent,
  referenceDataList: ReferenceDataList,
  indexes: Seq[IndexModel]
)(implicit ec: ExecutionContext)
    extends PlayMongoRepository[JsObject](
      mongoComponent = mongoComponent,
      collectionName = referenceDataList.listName,
      indexes = indexes,
      domainFormat = implicitly[Format[JsObject]]
    )
    with Logging {

  override lazy val requiresTtlIndex = false

  def one[A <: ReferenceDataList, B <: A](selector: Selector[A], projection: Option[Projection[B]] = None): Future[Option[JsObject]] =
    collection
      .find(selector.expression)
      .projection(projection.map(_.expression).getOrElse(BsonDocument()))
      .headOption()

  def many[A <: ReferenceDataList, B <: A](selector: Selector[A], projection: Option[Projection[B]] = None): Future[Seq[JsObject]] =
    collection
      .find(selector.expression)
      .projection(projection.map(_.expression).getOrElse(BsonDocument()))
      .toFuture()

  def insert(importId: ImportId, values: Seq[JsObject]): Future[Boolean] = {
    val enrichedValues = values.map(_ ++ Json.obj("importId" -> Json.toJson(importId)))

    collection
      .insertMany(enrichedValues)
      .toFuture()
      .map(_.wasAcknowledged())
  }

  def deleteOldImports(currentImportId: ImportId): Future[Boolean] = {
    val filter = Filters.lt("importId", currentImportId.value)

    collection
      .deleteMany(filter)
      .toFuture()
      .map {
        result =>
          logger.info(s"Deleted ${result.getDeletedCount} $collectionName records with import ids less than $currentImportId")
          result.wasAcknowledged()
      }
  }
}

object ListRepository {

  class ListRepositoryProvider @Inject() (mongoComponent: MongoComponent)(implicit ec: ExecutionContext) {

    def apply(list: ReferenceDataList): ListRepository = list match {
      case CountryCodesFullList                          => new CountryCodesFullListRepository(mongoComponent)
      case CountryCodesCommonTransitList                 => new CountryCodesCommonTransitListRepository(mongoComponent)
      case CountryCodesCommunityList                     => new CountryCodesCommunityListRepository(mongoComponent)
      case CustomsOfficesList                            => new CustomsOfficesListRepository(mongoComponent)
      case DocumentTypeCommonList                        => new DocumentTypeCommonListRepository(mongoComponent)
      case PreviousDocumentTypeCommonList                => new PreviousDocumentTypeCommonListRepository(mongoComponent)
      case KindOfPackagesList                            => new KindOfPackagesListRepository(mongoComponent)
      case TransportModeList                             => new TransportModeListRepository(mongoComponent)
      case AdditionalInformationIdCommonList             => new AdditionalInformationIdCommonListRepository(mongoComponent)
      case SpecificCircumstanceIndicatorList             => new SpecificCircumstanceIndicatorListRepository(mongoComponent)
      case UnDangerousGoodsCodeList                      => new UnDangerousGoodsCodeListRepository(mongoComponent)
      case TransportChargesMethodOfPaymentList           => new TransportChargesMethodOfPaymentListRepository(mongoComponent)
      case ControlResultList                             => new ControlResultListRepository(mongoComponent)
      case CountryCodesCommonTransitOutsideCommunityList => new CountryCodesCommonTransitOutsideCommunityListRepository(mongoComponent)
      case CountryCodesCustomsOfficeLists                => new CountryCodesCustomsOfficeListRepository(mongoComponent)
    }
  }

  @Singleton
  private class CountryCodesFullListRepository @Inject() (mongoComponent: MongoComponent)(implicit ec: ExecutionContext)
      extends ListRepository(
        mongoComponent = mongoComponent,
        referenceDataList = CountryCodesFullList,
        indexes = Seq(
          IndexModel(ascending("code"), IndexOptions().name("code-index")),
          IndexModel(ascending("importId"), IndexOptions().name("import-id-index")),
          IndexModel(compoundIndex(descending(Common.countryRegimeCode), ascending("code")), IndexOptions().name("countryRegimeCode-code-index"))
        )
      )

  @Singleton
  private class CountryCodesCommonTransitListRepository @Inject() (mongoComponent: MongoComponent)(implicit ec: ExecutionContext)
      extends ListRepository(
        mongoComponent = mongoComponent,
        referenceDataList = CountryCodesCommonTransitList,
        indexes = Seq(
          IndexModel(ascending("code"), IndexOptions().name("code-index")),
          IndexModel(ascending("importId"), IndexOptions().name("import-id-index"))
        )
      )

  @Singleton
  private class CountryCodesCommunityListRepository @Inject() (mongoComponent: MongoComponent)(implicit ec: ExecutionContext)
      extends ListRepository(
        mongoComponent = mongoComponent,
        referenceDataList = CountryCodesCommunityList,
        indexes = Seq(
          IndexModel(ascending("code"), IndexOptions().name("code-index")),
          IndexModel(ascending("importId"), IndexOptions().name("import-id-index"))
        )
      )

  @Singleton
  private class CustomsOfficesListRepository @Inject() (mongoComponent: MongoComponent)(implicit ec: ExecutionContext)
      extends ListRepository(
        mongoComponent = mongoComponent,
        referenceDataList = CustomsOfficesList,
        indexes = Seq(
          IndexModel(ascending("id"), IndexOptions().name("id-index")),
          IndexModel(ascending("countryId"), IndexOptions().name("country-id-index")),
          IndexModel(ascending("importId"), IndexOptions().name("import-id-index")),
          IndexModel(ascending("roles.role"), IndexOptions().name("customs-role-index"))
        )
      )

  @Singleton
  private class DocumentTypeCommonListRepository @Inject() (mongoComponent: MongoComponent)(implicit ec: ExecutionContext)
      extends ListRepository(
        mongoComponent = mongoComponent,
        referenceDataList = DocumentTypeCommonList,
        indexes = Seq(
          IndexModel(ascending("code"), IndexOptions().name("code-index")),
          IndexModel(ascending("importId"), IndexOptions().name("import-id-index"))
        )
      )

  @Singleton
  private class PreviousDocumentTypeCommonListRepository @Inject() (mongoComponent: MongoComponent)(implicit ec: ExecutionContext)
      extends ListRepository(
        mongoComponent = mongoComponent,
        referenceDataList = PreviousDocumentTypeCommonList,
        indexes = Seq(
          IndexModel(ascending("code"), IndexOptions().name("code-index")),
          IndexModel(ascending("importId"), IndexOptions().name("import-id-index"))
        )
      )

  @Singleton
  private class KindOfPackagesListRepository @Inject() (mongoComponent: MongoComponent)(implicit ec: ExecutionContext)
      extends ListRepository(
        mongoComponent = mongoComponent,
        referenceDataList = KindOfPackagesList,
        indexes = Seq(
          IndexModel(ascending("code"), IndexOptions().name("code-index")),
          IndexModel(ascending("importId"), IndexOptions().name("import-id-index"))
        )
      )

  @Singleton
  private class TransportModeListRepository @Inject() (mongoComponent: MongoComponent)(implicit ec: ExecutionContext)
      extends ListRepository(
        mongoComponent = mongoComponent,
        referenceDataList = TransportModeList,
        indexes = Seq(
          IndexModel(ascending("code"), IndexOptions().name("code-index")),
          IndexModel(ascending("importId"), IndexOptions().name("import-id-index"))
        )
      )

  @Singleton
  private class AdditionalInformationIdCommonListRepository @Inject() (mongoComponent: MongoComponent)(implicit ec: ExecutionContext)
      extends ListRepository(
        mongoComponent = mongoComponent,
        referenceDataList = AdditionalInformationIdCommonList,
        indexes = Seq(
          IndexModel(ascending("code"), IndexOptions().name("code-index")),
          IndexModel(ascending("importId"), IndexOptions().name("import-id-index"))
        )
      )

  @Singleton
  private class SpecificCircumstanceIndicatorListRepository @Inject() (mongoComponent: MongoComponent)(implicit ec: ExecutionContext)
      extends ListRepository(
        mongoComponent = mongoComponent,
        referenceDataList = SpecificCircumstanceIndicatorList,
        indexes = Seq(
          IndexModel(ascending("code"), IndexOptions().name("code-index")),
          IndexModel(ascending("importId"), IndexOptions().name("import-id-index"))
        )
      )

  @Singleton
  private class UnDangerousGoodsCodeListRepository @Inject() (mongoComponent: MongoComponent)(implicit ec: ExecutionContext)
      extends ListRepository(
        mongoComponent = mongoComponent,
        referenceDataList = UnDangerousGoodsCodeList,
        indexes = Seq(
          IndexModel(ascending("code"), IndexOptions().name("code-index")),
          IndexModel(ascending("importId"), IndexOptions().name("import-id-index"))
        )
      )

  @Singleton
  private class TransportChargesMethodOfPaymentListRepository @Inject() (mongoComponent: MongoComponent)(implicit ec: ExecutionContext)
      extends ListRepository(
        mongoComponent = mongoComponent,
        referenceDataList = TransportChargesMethodOfPaymentList,
        indexes = Seq(
          IndexModel(ascending("code"), IndexOptions().name("code-index")),
          IndexModel(ascending("importId"), IndexOptions().name("import-id-index"))
        )
      )

  @Singleton
  private class ControlResultListRepository @Inject() (mongoComponent: MongoComponent)(implicit ec: ExecutionContext)
      extends ListRepository(
        mongoComponent = mongoComponent,
        referenceDataList = ControlResultList,
        indexes = Seq(
          IndexModel(ascending("code"), IndexOptions().name("code-index")),
          IndexModel(ascending("importId"), IndexOptions().name("import-id-index"))
        )
      )

  @Singleton
  private class RequestedDocumentTypeListRepository @Inject() (mongoComponent: MongoComponent)(implicit ec: ExecutionContext)
    extends ListRepository(
      mongoComponent = mongoComponent,
      referenceDataList = RequestedDocumentTypeList,
      indexes = Seq(
        IndexModel(ascending("code"), IndexOptions().name("code-index")),
        IndexModel(ascending("importId"), IndexOptions().name("import-id-index"))
      )
    )

  @Singleton
  private class CountryCodesCommonTransitOutsideCommunityListRepository @Inject() (mongoComponent: MongoComponent)(implicit ec: ExecutionContext)
      extends ListRepository(
        mongoComponent = mongoComponent,
        referenceDataList = CountryCodesCommonTransitOutsideCommunityList,
        indexes = Seq(
          IndexModel(ascending("code"), IndexOptions().name("code-index")),
          IndexModel(ascending("importId"), IndexOptions().name("import-id-index"))
        )
      )

  @Singleton
  private class CountryCodesCustomsOfficeListRepository @Inject() (mongoComponent: MongoComponent)(implicit ec: ExecutionContext)
      extends ListRepository(
        mongoComponent = mongoComponent,
        referenceDataList = CountryCodesCustomsOfficeLists,
        indexes = Seq(
          IndexModel(ascending("code"), IndexOptions().name("code-index")),
          IndexModel(ascending("importId"), IndexOptions().name("import-id-index")),
          IndexModel(compoundIndex(descending(Common.countryRegimeCode), ascending("code")), IndexOptions().name("countryRegimeCode-code-index"))
        )
      )

}
