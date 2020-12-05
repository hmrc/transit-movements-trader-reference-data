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

import api.models.OfficeOfTransit
import api.services.OfficeOfTransitService
import base.SpecBaseWithAppPerSuite
import org.mockito.Mockito.when
import org.mockito.ArgumentMatchers.{eq => eqTo, _}
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.libs.json.Json
import play.api.mvc.Result
import play.api.test.FakeRequest
import play.api.test.Helpers._

import scala.concurrent.Future

class OfficeOfTransitControllerSpec extends SpecBaseWithAppPerSuite {

  val officeId = "AD000001"

  private val officesOfTransit = Seq(
    OfficeOfTransit(officeId, "SANT JULIÀ DE LÒRIA, CUSTOMS OFFICE SANT JULIÀ DE LÒRIA"),
    OfficeOfTransit("AD000002", "Central Community Transit Office")
  )

  private val invalidValue = "123345"

  private val mockOfficeOfTransitService = mock[OfficeOfTransitService]

  override val mocks: Seq[_] = super.mocks :+ mockOfficeOfTransitService

  override def guiceApplicationBuilder: GuiceApplicationBuilder =
    super.guiceApplicationBuilder
      .overrides(
        bind[OfficeOfTransitService].toInstance(mockOfficeOfTransitService)
      )

  "OfficeOfTransitController" - {

    "must return ok and fetch some offices of transit" in {

      when(mockOfficeOfTransitService.getOfficeOfTransit(eqTo(officeId))).thenReturn(Some(officesOfTransit.head))

      val officeOfTransit = officesOfTransit.head

      val request = FakeRequest(GET, routes.OfficeOfTransitController.getOfficeOfTransit(officeId).url)

      val result: Future[Result] = route(app, request).value

      status(result) mustEqual OK

      contentAsJson(result) mustBe Json.toJson(officeOfTransit)
    }

    "must return NotFound when no office of transit is found" in {

      when(mockOfficeOfTransitService.getOfficeOfTransit(any())).thenReturn(None)

      val request = FakeRequest(GET, routes.OfficeOfTransitController.getOfficeOfTransit(invalidValue).url)

      val result: Future[Result] = route(app, request).value
      status(result) mustEqual NOT_FOUND
    }

    "must fetch offices of transit" in {

      when(mockOfficeOfTransitService.officesOfTransit).thenReturn(officesOfTransit)

      val request =
        FakeRequest(GET, routes.OfficeOfTransitController.officesOfTransit().url)
      val result: Future[Result] = route(app, request).value

      status(result) mustBe OK
      contentAsJson(result) mustBe Json.toJson(officesOfTransit)
    }

  }
}
