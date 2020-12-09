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

package data.connector

import java.util

import akka.util.ByteString
import com.github.tomakehurst.wiremock.client.WireMock._
import data.ReferenceDataJsonProjectionSpec
import models.ReferenceDataList
import org.scalacheck.Arbitrary
import org.scalacheck.Gen
import play.api.http.HeaderNames
import play.api.http.MimeTypes
import play.api.libs.json.JsObject
import play.api.libs.json.JsValue
import play.api.libs.json.Json
import play.api.libs.json.Writes

import scala.collection.JavaConverters._

class RefDataConnectorSpec extends ConnectorSpecBase {

  implicit val arbitraryReferenceDataList: Arbitrary[ReferenceDataList] =
    Arbitrary(Gen.oneOf(ReferenceDataList.values.toList))

  val pathToLists = "/customs-reference-data/lists"

  "get" - {
    "returns the reference data for a valid reference data list" in {

      val listName         = Arbitrary.arbitrary[ReferenceDataList].sample.value
      val pathToSingleList = s"$pathToLists/${listName.listName}"

      val listsResponse: JsObject =
        Json.obj(
          "_self"           -> Json.obj("href" -> pathToLists),
          listName.listName -> Json.obj("href" -> pathToSingleList)
        )

      val refDataList =
        ReferenceDataJsonProjectionSpec.formatAsReferenceDataJson(
          Seq(
            simpleJsObject(1),
            simpleJsObject(2),
            simpleJsObject(3)
          )
        )

      server.stubFor(
        get(urlEqualTo(pathToLists))
          .willReturn(
            ok()
              .withHeader(HeaderNames.CONTENT_ENCODING, MimeTypes.JSON)
              .withBody(listsResponse.toString())
          )
      )

      server.stubFor(
        get(urlEqualTo(pathToSingleList))
          .willReturn(
            ok()
              .withHeader(HeaderNames.CONTENT_ENCODING, MimeTypes.JSON)
              .withBody(refDataList.toString())
          )
      )

      val connector = app.injector.instanceOf[RefDataConnector]

      val result: ByteString = connector.get(listName).futureValue.value

      Json.parse(result.toArray) mustEqual refDataList
    }

    "return a none when the service doesn't have the list" in {

      val listName         = Arbitrary.arbitrary[ReferenceDataList].sample.value
      val pathToSingleList = s"$pathToLists/${listName.listName}"

      val listsResponse: JsObject =
        Json.obj(
          "_self" -> Json.obj("href" -> pathToLists)
        )

      val refDataList =
        ReferenceDataJsonProjectionSpec.formatAsReferenceDataJson(
          Seq(
            simpleJsObject(1),
            simpleJsObject(2),
            simpleJsObject(3)
          )
        )

      server.stubFor(
        get(urlEqualTo(pathToLists))
          .willReturn(
            ok()
              .withHeader(HeaderNames.CONTENT_ENCODING, MimeTypes.JSON)
              .withBody(listsResponse.toString())
          )
      )

      server.stubFor(
        get(urlEqualTo(pathToSingleList))
          .willReturn(
            ok()
              .withHeader(HeaderNames.CONTENT_ENCODING, MimeTypes.JSON)
              .withBody(refDataList.toString())
          )
      )

      val connector = app.injector.instanceOf[RefDataConnector]

      val result = connector.get(listName).futureValue

      result must not be defined
    }
  }

  /**
    * @return The name of the config key for the external service
    */
  override protected def portConfigKey: String = "microservice.services.customsReferenceData.port"

  def simpleJsObject[A: Writes](value: A): JsObject = Json.obj("key" -> value)

}
