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

package data.transform

import akka.actor.ActorSystem
import akka.stream.scaladsl.Keep
import akka.stream.testkit.scaladsl.TestSink
import akka.stream.testkit.scaladsl.TestSource
import base.SpecBaseWithAppPerSuite
import models.CountryCodesFullList
import play.api.libs.json.JsObject
import play.api.libs.json.Json

class TransformationFlowSpec extends SpecBaseWithAppPerSuite {

  import CountryCodesFullListTransfomSpec._

  implicit lazy val actorSystem: ActorSystem = ActorSystem()

  "flow returns a Flow that" - {

    val testData       = Json.parse(validData).as[JsObject]
    val expectedObject = Json.parse(expected).as[JsObject]

    val invaildData = Json.parse(invalidDataWithMissingState).as[JsObject]

    "transforms the JsObject if it is valid for the transformation" in {

      val sut = TransformationFlow(CountryCodesFullList, Transformation(CountryCodesFullList))

      val (pub, sub) =
        TestSource
          .probe[JsObject]
          .via(sut.flow)
          .toMat(TestSink.probe[JsObject])(Keep.both)
          .run()

      sub.request(2)
      pub.sendNext(testData)
      pub.sendNext(testData)
      sub.expectNextN(List(expectedObject, expectedObject))

    }

    "drops the JsObject if it is valid for the transformation" in {

      val sut = TransformationFlow(CountryCodesFullList, Transformation(CountryCodesFullList))

      val (pub, sub) =
        TestSource
          .probe[JsObject]
          .via(sut.flow)
          .toMat(TestSink.probe[JsObject])(Keep.both)
          .run()

      val testData = Json.parse(validData).as[JsObject]

      val expectedObject = Json.parse(expected).as[JsObject]

      sub.request(3)
      pub.sendNext(testData)
      pub.sendNext(invaildData)
      pub.sendNext(testData)
      sub.expectNextN(List(expectedObject, expectedObject))

    }

  }

}
