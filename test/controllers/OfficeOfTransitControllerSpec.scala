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
import models.OfficeOfTransit
import org.mockito.Mockito.reset
import org.mockito.Mockito.when
import org.mockito.Matchers.any
import org.mockito.Matchers.{eq => eqTo}
import org.scalatest.BeforeAndAfterAll
import org.scalatest.BeforeAndAfterEach
import org.scalatest.MustMatchers
import org.scalatestplus.mockito.MockitoSugar
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.inject.bind
import play.api.libs.json.Json
import play.api.mvc.Result
import play.api.test.FakeRequest
import play.api.test.Helpers._
import services.OfficeOfTransitService

import scala.concurrent.Future

class OfficeOfTransitControllerSpec extends SpecBase with MustMatchers with MockitoSugar with BeforeAndAfterEach with BeforeAndAfterAll {

  val officeId = "AD000001"

  private val officesOfTransit = Seq(
    OfficeOfTransit(officeId, "SANT JULIÀ DE LÒRIA, CUSTOMS OFFICE SANT JULIÀ DE LÒRIA"),
    OfficeOfTransit("AD000002", "Central Community Transit Office")
  )

  private val invalidValue = "123345"

  private val mockOfficeOfTransitService = mock[OfficeOfTransitService]

  private def application: GuiceApplicationBuilder =
    applicationBuilder()
      .overrides(
        bind[OfficeOfTransitService].toInstance(mockOfficeOfTransitService)
      )

  "OfficeOfTransitController" - {

    "must return ok and fetch some offices of transit" in {

      when(mockOfficeOfTransitService.getOfficeOfTransit(eqTo(officeId))).thenReturn(Some(officesOfTransit.head))

      lazy val app = application.build()

      val officeOfTransit = officesOfTransit.head

      val request = FakeRequest(GET, routes.OfficeOfTransitController.getOfficeOfTransit(officeId).url)

      val result: Future[Result] = route(app, request).value

      status(result) mustEqual OK

      contentAsJson(result) mustBe Json.toJson(officeOfTransit)
    }

    "must return NotFound when no office of transit is found" in {

      when(mockOfficeOfTransitService.getOfficeOfTransit(any())).thenReturn(None)

      lazy val app = application.build()
      val request  = FakeRequest(GET, routes.OfficeOfTransitController.getOfficeOfTransit(invalidValue).url)

      val result: Future[Result] = route(app, request).value
      status(result) mustEqual NOT_FOUND
    }

    "must fetch offices of transit" in {

      when(mockOfficeOfTransitService.officesOfTransit).thenReturn(officesOfTransit)

      lazy val app = application.build()
      val request =
        FakeRequest(GET, routes.OfficeOfTransitController.officesOfTransit().url)
      val result: Future[Result] = route(app, request).value

      status(result) mustBe OK
      contentAsJson(result) mustBe Json.toJson(officesOfTransit)
    }

  }

  override protected def afterEach(): Unit = {
    super.afterEach()
    reset(mockOfficeOfTransitService)
  }
}
