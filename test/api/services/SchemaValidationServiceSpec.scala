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

package api.services

import base.SpecBase
import javax.inject.Inject
import api.models.schemas.SchemaValidationError
import org.leadpony.justify.api.JsonValidationService
import org.scalatest.EitherValues
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.Environment
import play.api.inject.SimpleModule
import play.api.inject.bind
import play.api.libs.json.Json
import api.models.schemas.JsonSchemaProvider
import java.io.FileNotFoundException

private[this] class TestJsonSchema @Inject() (env: Environment, jsonValidationService: JsonValidationService) extends JsonSchemaProvider {

  val path = "test.schema.json"

  val schema =
    env
      .resourceAsStream(path)
      .fold(throw new FileNotFoundException(s"File for schem: ${getClass.getSimpleName} not find file at path $path"))(jsonValidationService.readSchema)

}

private[this] class TestModule
    extends SimpleModule(
      (_, _) => Seq(bind[TestJsonSchema].toSelf.eagerly())
    )

class SchemaValidationServiceSpec extends SpecBase with GuiceOneAppPerSuite with EitherValues {

  "validate" - {
    "returns true when the json matches the schema specifications" in {
      val json = Json.obj(
        "firstName" -> "firstName_value",
        "lastName"  -> "lastName_value",
        "age"       -> 21
      )

      val service        = app.injector.instanceOf[SchemaValidationService]
      val testJsonSchema = app.injector.instanceOf[TestJsonSchema]

      service.validate(testJsonSchema, json).right.value mustEqual json
    }

    "returns SchemaValidationError with description of the problem when the json does not match the schema specifications" in {

      val json = Json.obj(
        "firstName" -> "firstName_value",
        "lastName"  -> "lastName_value",
        "age"       -> "INVALID_VALUE",
        "level1" -> Json.obj(
          "level1_arr" -> Json.arr(1)
        )
      )

      val service        = app.injector.instanceOf[SchemaValidationService]
      val testJsonSchema = app.injector.instanceOf[TestJsonSchema]

      service.validate(testJsonSchema, json).left.value mustBe a[SchemaValidationError]
    }

  }

}
