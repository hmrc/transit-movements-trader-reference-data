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

package api.controllers

import base.SpecBaseWithAppPerSuite
import data.DataRetrieval
import models.DocumentTypeCommonList
import org.mockito.ArgumentMatchers.any
import org.mockito.ArgumentMatchers.{eq => eqTo}
import org.mockito.Mockito.when
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.libs.json.Json
import play.api.test.FakeRequest
import play.api.test.Helpers.GET
import play.api.test.Helpers.contentAsJson
import play.api.test.Helpers.route
import play.api.test.Helpers.status
import play.api.test.Helpers._

import scala.concurrent.Future

class DocumentTypeControllerRemoteSpec extends SpecBaseWithAppPerSuite {
  private val mockDataRetrieval = mock[DataRetrieval]

  override val mocks: Seq[_] = super.mocks ++ Seq(mockDataRetrieval)

  override def guiceApplicationBuilder: GuiceApplicationBuilder =
    super.guiceApplicationBuilder
      .overrides(
        bind[DocumentTypeController].to[DocumentTypeControllerRemote],
        bind[DataRetrieval].toInstance(mockDataRetrieval)
      )

  "getAll" - {
    "must fetch all document type data" in {

      val data = Seq(Json.obj("key" -> "value"))
      when(mockDataRetrieval.getList(eqTo(DocumentTypeCommonList))(any())).thenReturn(Future.successful(data))

      val request = FakeRequest(
        GET,
        routes.DocumentTypeController.getAll().url
      )
      val result = route(app, request).value

      status(result) mustBe OK
      contentAsJson(result) mustBe Json.toJson(data)
    }

    "returns a 404 when no data is present" in {

      when(mockDataRetrieval.getList(eqTo(DocumentTypeCommonList))(any())).thenReturn(Future.successful(Seq.empty))

      val request = FakeRequest(
        GET,
        routes.DocumentTypeController.getAll().url
      )
      val result = route(app, request).value

      status(result) mustBe NOT_FOUND
    }
  }

}
