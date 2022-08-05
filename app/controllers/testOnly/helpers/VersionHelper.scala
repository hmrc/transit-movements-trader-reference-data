/*
 * Copyright 2022 HM Revenue & Customs
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

package controllers.testOnly.helpers

import controllers.testOnly.Version
import controllers.testOnly.Version1
import controllers.testOnly.Version2
import play.api.mvc.AnyContent
import play.api.mvc.Request

object VersionHelper {

  val acceptHeaderPattern = "^application/vnd[.]{1}hmrc[.]{1}(.*?)[+]{1}(.*)$".r

  def getVersion(request: Request[AnyContent]): Option[Version] =
    request.headers.get("Accept") match {
      case Some(value) =>
        value match {
          case acceptHeaderPattern(version, contentType) =>
            (version, contentType) match {
              case ("1.0", "json") => Some(Version1)
              case ("2.0", "json") => Some(Version2)
              case _               => None
            }
        }
      case None => None
    }

}
