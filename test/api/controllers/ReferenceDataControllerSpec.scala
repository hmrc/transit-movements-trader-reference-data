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

import api.models.AdditionalInformation
import api.models.DocumentType
import api.models.KindOfPackage
import api.models.SpecialMention
import api.models.MethodOfPayment
import base.SpecBase
import org.mockito.Mockito.when
import org.scalatest.MustMatchers
import org.scalatestplus.mockito.MockitoSugar
import play.api.inject.bind
import play.api.libs.json.Json
import play.api.test.FakeRequest
import play.api.test.Helpers.route
import play.api.test.Helpers.status
import play.api.test.Helpers._
import api.services._

class ReferenceDataControllerSpec extends SpecBase with MustMatchers with MockitoSugar {

  private val additionalInformation = Seq(AdditionalInformation("abc", "info description"))

  private val kindsOfPackage = Seq(KindOfPackage("name", "description"))

  private val documentTypes = Seq(DocumentType("code", "description", transportDocument = true))

  private val specialMention = Seq(SpecialMention("code", "description"))

  private val methodOfPayment = Seq(MethodOfPayment("code", "description"))

  private val additionalInformationService = mock[AdditionalInformationService]
  private val kindOfPackageService         = mock[KindOfPackageService]
  private val documentTypeService          = mock[DocumentTypeService]
  private val specialMentionService        = mock[SpecialMentionService]
  private val methodOfPaymentService       = mock[MethodOfPaymentService]

  when(additionalInformationService.additionalInformation).thenReturn(additionalInformation)
  when(kindOfPackageService.kindsOfPackage).thenReturn(kindsOfPackage)
  when(documentTypeService.documentTypes).thenReturn(documentTypes)
  when(specialMentionService.specialMention).thenReturn(specialMention)
  when(methodOfPaymentService.methodOfPayment).thenReturn(methodOfPayment)

  private def appBuilder =
    applicationBuilder()
      .overrides(
        bind[AdditionalInformationService].toInstance(additionalInformationService),
        bind[KindOfPackageService].toInstance(kindOfPackageService),
        bind[DocumentTypeService].toInstance(documentTypeService),
        bind[SpecialMentionService].toInstance(specialMentionService),
        bind[MethodOfPaymentService].toInstance(methodOfPaymentService)
      )

  "ReferenceDataController" - {

    "must fetch additional information" in {

      lazy val app = appBuilder.build()

      val request = FakeRequest(
        GET,
        routes.ReferenceDataController.additionalInformation().url
      )
      val result = route(app, request).value

      status(result) mustBe OK
      contentAsJson(result) mustBe Json.toJson(additionalInformation)
      app.stop()
    }

    "must fetch kinds of package" in {

      lazy val app = appBuilder.build()

      val request = FakeRequest(
        GET,
        routes.ReferenceDataController.kindsOfPackage().url
      )
      val result = route(app, request).value

      status(result) mustBe OK
      contentAsJson(result) mustBe Json.toJson(kindsOfPackage)
      app.stop()
    }

    "must fetch document types" in {

      lazy val app = appBuilder.build()

      val request = FakeRequest(
        GET,
        routes.ReferenceDataController.documentTypes().url
      )
      val result = route(app, request).value

      status(result) mustBe OK
      contentAsJson(result) mustBe Json.toJson(documentTypes)
      app.stop()
    }

    "must fetch special mention" in {

      lazy val app = appBuilder.build()

      val request = FakeRequest(
        GET,
        routes.ReferenceDataController.specialMention().url
      )
      val result = route(app, request).value

      status(result) mustBe OK
      contentAsJson(result) mustBe Json.toJson(specialMention)
      app.stop()
    }

    "must fetch method of payment" in {

      lazy val app = appBuilder.build()

      val request = FakeRequest(
        GET,
        routes.ReferenceDataController.methodOfPayment().url
      )
      val result = route(app, request).value

      status(result) mustBe OK
      contentAsJson(result) mustBe Json.toJson(methodOfPayment)
      app.stop()
    }
  }
}
