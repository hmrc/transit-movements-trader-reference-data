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

import base.SpecBaseWithAppPerSuite
import models._
import repositories.ListRepository.ListRepositoryProvider
import uk.gov.hmrc.mongo.MongoComponent

import scala.concurrent.ExecutionContext.Implicits.global

class ListRepositorySpec extends SpecBaseWithAppPerSuite {

  private val mongoComponent = app.injector.instanceOf[MongoComponent]

  "provider" - {
    "when given CountryCodesFullList" - {
      "must return instance of CountryCodesFullListRepository" in {
        val repo = new ListRepositoryProvider(mongoComponent).apply(CountryCodesFullList)
        repo.collectionName mustBe "CountryCodesFullList"
        repo.indexes.size mustBe 3
      }
    }

    "when given CountryCodesCommonTransitList" - {
      "must return instance of CountryCodesCommonTransitListRepository" in {
        val repo = new ListRepositoryProvider(mongoComponent).apply(CountryCodesCommonTransitList)
        repo.collectionName mustBe "CountryCodesCommonTransit"
        repo.indexes.size mustBe 2
      }
    }

    "when given CountryCodesCommunityList" - {
      "must return instance of CountryCodesCommunityListRepository" in {
        val repo = new ListRepositoryProvider(mongoComponent).apply(CountryCodesCommunityList)
        repo.collectionName mustBe "CountryCodesCommunity"
        repo.indexes.size mustBe 2
      }
    }

    "when given CustomsOfficesList" - {
      "must return instance of CustomsOfficesListRepository" in {
        val repo = new ListRepositoryProvider(mongoComponent).apply(CustomsOfficesList)
        repo.collectionName mustBe "CustomsOffices"
        repo.indexes.size mustBe 4
      }
    }

    "when given DocumentTypeCommonList" - {
      "must return instance of DocumentTypeCommonListRepository" in {
        val repo = new ListRepositoryProvider(mongoComponent).apply(DocumentTypeCommonList)
        repo.collectionName mustBe "DocumentTypeCommon"
        repo.indexes.size mustBe 2
      }
    }

    "when given PreviousDocumentTypeCommonList" - {
      "must return instance of PreviousDocumentTypeCommonListRepository" in {
        val repo = new ListRepositoryProvider(mongoComponent).apply(PreviousDocumentTypeCommonList)
        repo.collectionName mustBe "PreviousDocumentTypeCommon"
        repo.indexes.size mustBe 2
      }
    }

    "when given KindOfPackagesList" - {
      "must return instance of KindOfPackagesListRepository" in {
        val repo = new ListRepositoryProvider(mongoComponent).apply(KindOfPackagesList)
        repo.collectionName mustBe "KindOfPackages"
        repo.indexes.size mustBe 2
      }
    }

    "when given TransportModeList" - {
      "must return instance of TransportModeListRepository" in {
        val repo = new ListRepositoryProvider(mongoComponent).apply(TransportModeList)
        repo.collectionName mustBe "TransportMode"
        repo.indexes.size mustBe 2
      }
    }

    "when given AdditionalInformationIdCommonList" - {
      "must return instance of AdditionalInformationIdCommonListRepository" in {
        val repo = new ListRepositoryProvider(mongoComponent).apply(AdditionalInformationIdCommonList)
        repo.collectionName mustBe "AdditionalInformationIdCommon"
        repo.indexes.size mustBe 2
      }
    }

    "when given SpecificCircumstanceIndicatorList" - {
      "must return instance of SpecificCircumstanceIndicatorListRepository" in {
        val repo = new ListRepositoryProvider(mongoComponent).apply(SpecificCircumstanceIndicatorList)
        repo.collectionName mustBe "SpecificCircumstanceIndicator"
        repo.indexes.size mustBe 2
      }
    }

    "when given UnDangerousGoodsCodeList" - {
      "must return instance of UnDangerousGoodsCodeListRepository" in {
        val repo = new ListRepositoryProvider(mongoComponent).apply(UnDangerousGoodsCodeList)
        repo.collectionName mustBe "UnDangerousGoodsCode"
        repo.indexes.size mustBe 2
      }
    }

    "when given TransportChargesMethodOfPaymentList" - {
      "must return instance of TransportChargesMethodOfPaymentListRepository" in {
        val repo = new ListRepositoryProvider(mongoComponent).apply(TransportChargesMethodOfPaymentList)
        repo.collectionName mustBe "TransportChargesMethodOfPayment"
        repo.indexes.size mustBe 2
      }
    }

    "when given ControlResultList" - {
      "must return instance of ControlResultListRepository" in {
        val repo = new ListRepositoryProvider(mongoComponent).apply(ControlResultList)
        repo.collectionName mustBe "ControlResult"
        repo.indexes.size mustBe 2
      }
    }

    "when given CountryCodesCommonTransitOutsideCommunityList" - {
      "must return instance of CountryCodesCommonTransitOutsideCommunityListRepository" in {
        val repo = new ListRepositoryProvider(mongoComponent).apply(CountryCodesCommonTransitOutsideCommunityList)
        repo.collectionName mustBe "CountryCodesCommonTransitOutsideCommunity"
        repo.indexes.size mustBe 2
      }
    }

    "when given CountryCodesCustomsOfficeLists" - {
      "must return instance of CountryCodesCustomsOfficeListRepository" in {
        val repo = new ListRepositoryProvider(mongoComponent).apply(CountryCodesCustomsOfficeLists)
        repo.collectionName mustBe "CountryCodesCustomsOfficeLists"
        repo.indexes.size mustBe 3
      }
    }
  }

}
