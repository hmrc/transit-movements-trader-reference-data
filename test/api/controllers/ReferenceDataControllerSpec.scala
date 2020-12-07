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

package api.controllers

import api.models._
import api.services._
import base.SpecBaseWithAppPerSuite
import org.mockito.ArgumentMatchers._
import org.mockito.Mockito.when
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.libs.json.Json
import play.api.test.FakeRequest
import play.api.test.Helpers.route
import play.api.test.Helpers.status
import play.api.test.Helpers._

class ReferenceDataControllerSpec extends SpecBaseWithAppPerSuite {

  private val additionalInformation = Seq(AdditionalInformation("abc", "info description"))

  private val kindsOfPackage = Seq(KindOfPackage("name", "description"))

  private val documentTypes = Seq(DocumentType("code", "description", transportDocument = true))

  private val specialMention = Seq(SpecialMention("code", "description"))

  private val methodOfPayment = Seq(MethodOfPayment("code", "description"))

  private def dangerousGoodsCode = DangerousGoodsCode("code", "description")

  private val dangerousGoodsCodes = Seq(dangerousGoodsCode)

  private val additionalInformationService = mock[AdditionalInformationService]
  private val kindOfPackageService         = mock[KindOfPackageService]
  private val documentTypeService          = mock[DocumentTypeService]
  private val specialMentionService        = mock[SpecialMentionService]
  private val methodOfPaymentService       = mock[MethodOfPaymentService]
  private val dangerousGoodsCodeService    = mock[DangerousGoodsCodeService]

  override val mocks: Seq[_] = super.mocks ++ Seq(
    additionalInformationService,
    kindOfPackageService,
    documentTypeService,
    specialMentionService,
    methodOfPaymentService,
    dangerousGoodsCodeService
  )

  override def guiceApplicationBuilder: GuiceApplicationBuilder =
    super.guiceApplicationBuilder
      .overrides(
        bind[AdditionalInformationService].toInstance(additionalInformationService),
        bind[KindOfPackageService].toInstance(kindOfPackageService),
        bind[DocumentTypeService].toInstance(documentTypeService),
        bind[SpecialMentionService].toInstance(specialMentionService),
        bind[MethodOfPaymentService].toInstance(methodOfPaymentService),
        bind[DangerousGoodsCodeService].toInstance(dangerousGoodsCodeService)
      )

  "ReferenceDataController" - {

    "must fetch additional information" in {
      when(additionalInformationService.additionalInformation).thenReturn(additionalInformation)

      val request = FakeRequest(
        GET,
        routes.ReferenceDataController.additionalInformation().url
      )
      val result = route(app, request).value

      status(result) mustBe OK
      contentAsJson(result) mustBe Json.toJson(additionalInformation)

    }

    "must fetch kinds of package" in {
      when(kindOfPackageService.kindsOfPackage).thenReturn(kindsOfPackage)

      val request = FakeRequest(
        GET,
        routes.ReferenceDataController.kindsOfPackage().url
      )
      val result = route(app, request).value

      status(result) mustBe OK
      contentAsJson(result) mustBe Json.toJson(kindsOfPackage)

    }

    "must fetch document types" in {
      when(documentTypeService.documentTypes).thenReturn(documentTypes)

      val request = FakeRequest(
        GET,
        routes.ReferenceDataController.documentTypes().url
      )
      val result = route(app, request).value

      status(result) mustBe OK
      contentAsJson(result) mustBe Json.toJson(documentTypes)

    }

    "must fetch special mention" in {
      when(specialMentionService.specialMention).thenReturn(specialMention)

      val request = FakeRequest(
        GET,
        routes.ReferenceDataController.specialMention().url
      )
      val result = route(app, request).value

      status(result) mustBe OK
      contentAsJson(result) mustBe Json.toJson(specialMention)

    }

    "must fetch method of payment" in {
      when(methodOfPaymentService.methodOfPayment).thenReturn(methodOfPayment)

      val request = FakeRequest(
        GET,
        routes.ReferenceDataController.methodOfPayment().url
      )
      val result = route(app, request).value

      status(result) mustBe OK
      contentAsJson(result) mustBe Json.toJson(methodOfPayment)

    }

    "must fetch dangerous goods code" in {
      when(dangerousGoodsCodeService.dangerousGoodsCodes).thenReturn(dangerousGoodsCodes)

      val request = FakeRequest(
        GET,
        routes.ReferenceDataController.dangerousGoodsCodes().url
      )
      val result = route(app, request).value

      status(result) mustBe OK
      contentAsJson(result) mustBe Json.toJson(dangerousGoodsCodes)

    }

    "getDangerousGoodsCodeByCode" - {
      "must dangerous goods code and return Ok" in {
        val code = "0004"

        when(dangerousGoodsCodeService.getDangerousGoodsCodeByCode(any())).thenReturn(Some(dangerousGoodsCode))

        val request = FakeRequest(
          GET,
          routes.ReferenceDataController.getDangerousGoodsCode(code).url
        )
        val result = route(app, request).value

        status(result) mustBe OK
        contentAsJson(result) mustBe Json.toJson(dangerousGoodsCode)

      }

      "must return NotFound when no dangerous goods code is found" in {
        when(dangerousGoodsCodeService.getDangerousGoodsCodeByCode(any())).thenReturn(None)

        val invalidCode = "Invalid"

        val request = FakeRequest(
          GET,
          routes.ReferenceDataController.getDangerousGoodsCode(invalidCode).url
        )
        val result = route(app, request).value

        status(result) mustBe NOT_FOUND

      }
    }
  }
}
