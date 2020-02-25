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

package controllers

import base.SpecBase
import models.AdditionalInformation
import models.Country
import models.CustomsOffice
import models.DocumentType
import models.KindOfPackage
import org.scalatest.MustMatchers
import org.scalatestplus.mockito.MockitoSugar
import org.mockito.Mockito.when
import play.api.test.FakeRequest
import play.api.inject.bind
import play.api.libs.json.Json
import play.api.test.Helpers.route
import play.api.test.Helpers.status
import play.api.test.Helpers._
import services._

class ReferenceDataControllerSpec extends SpecBase with MustMatchers with MockitoSugar {

  private val customsOffices = Seq(
    CustomsOffice(
      "GB000001",
      "Central Community Transit Office",
      Some("+44 (0)3000 999 982"),
      List("TRA", "DEP", "DES")
    ),
    CustomsOffice(
      "GB000002",
      "Central Community Transit Office",
      None,
      List("TRA", "DEP", "DES")
    )
  )

  private val customsOfficeId = customsOffices.head.id
  private val countries       = Seq(Country("valid", "GB", "United Kingdom"))

  private val additionalInformation = Seq(AdditionalInformation("abc", "info description"))

  private val kindsOfPackage = Seq(KindOfPackage("name", "description"))

  private val documentTypes = Seq(DocumentType("code", "description", transportDocument = true))

  private val countryService               = mock[CountryService]
  private val customsOfficesService        = mock[CustomsOfficesService]
  private val transitCountryService        = mock[TransitCountryService]
  private val additionalInformationService = mock[AdditionalInformationService]
  private val kindOfPackageService         = mock[KindOfPackageService]
  private val documentTypeService          = mock[DocumentTypeService]

  when(customsOfficesService.customsOffices).thenReturn(customsOffices)
  when(customsOfficesService.getCustomsOffice(customsOfficeId)).thenReturn(Some(customsOffices.head))
  when(countryService.countries).thenReturn(countries)
  when(transitCountryService.transitCountryCodes).thenReturn(countries)
  when(additionalInformationService.additionalInformation).thenReturn(additionalInformation)
  when(kindOfPackageService.kindsOfPackage).thenReturn(kindsOfPackage)
  when(documentTypeService.documentTypes).thenReturn(documentTypes)

  private def appBuilder =
    applicationBuilder()
      .overrides(
        bind[CountryService].toInstance(countryService),
        bind[CustomsOfficesService].toInstance(customsOfficesService),
        bind[TransitCountryService].toInstance(transitCountryService),
        bind[AdditionalInformationService].toInstance(additionalInformationService),
        bind[KindOfPackageService].toInstance(kindOfPackageService),
        bind[DocumentTypeService].toInstance(documentTypeService)
      )

  "ReferenceDataController" - {

    "getCustomsOffice" - {

      "must fetch some customs office" in {

        lazy val app = appBuilder.build()

        val customsOffice = customsOffices.head

        val request = FakeRequest(GET, routes.ReferenceDataController.getCustomsOffice(customsOffice.id).url)
        val result  = route(app, request).value

        status(result) mustBe OK
        contentAsJson(result) mustBe Json.toJson(customsOffice)
        app.stop()
      }
    }

    "must fetch customs offices" in {

      lazy val app = appBuilder.build()

      val request =
        FakeRequest(GET, routes.ReferenceDataController.customsOffices().url)
      val result = route(app, request).value

      status(result) mustBe OK
      contentAsJson(result) mustBe Json.toJson(customsOffices)
      app.stop()
    }

    "must fetch country full list" in {

      lazy val app = appBuilder.build()

      val request = FakeRequest(
        GET,
        routes.ReferenceDataController.countriesFullList().url
      )
      val result = route(app, request).value

      status(result) mustBe OK
      contentAsJson(result) mustBe Json.toJson(countries)
      app.stop()
    }

    "must fetch transit countries" in {

      lazy val app = appBuilder.build()

      val request = FakeRequest(
        GET,
        routes.ReferenceDataController.transitCountries().url
      )
      val result = route(app, request).value

      status(result) mustBe OK
      contentAsJson(result) mustBe Json.toJson(countries)
      app.stop()
    }

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
  }
}
