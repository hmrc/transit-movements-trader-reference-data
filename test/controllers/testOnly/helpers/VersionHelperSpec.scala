/*
 * Copyright 2023 HM Revenue & Customs
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

import base.SpecBase
import play.api.test.FakeRequest
import play.api.test.Helpers.ACCEPT
import play.api.test.Helpers.GET

class VersionHelperSpec extends SpecBase {

  "GetVersion" - {

    "handle None" in {

      val request = FakeRequest(GET, "/foo")
      val version = VersionHelper.getVersion(request)

      version mustBe None

    }

    "handle P4 (1.0)" in {

      val request = FakeRequest(GET, "/foo").withHeaders(ACCEPT -> "application/vnd.hmrc.1.0+json")
      val version = VersionHelper.getVersion(request)

      version mustBe Some(P4)

    }

    "handle P5 (2.0)" in {

      val request = FakeRequest(GET, "/foo").withHeaders(ACCEPT -> "application/vnd.hmrc.2.0+json")
      val version = VersionHelper.getVersion(request)

      version mustBe Some(P5)

    }

    "handle default (*/*)" in {

      val request = FakeRequest(GET, "/foo").withHeaders(ACCEPT -> "*/*")
      val version = VersionHelper.getVersion(request)

      version mustBe None

    }

  }

}
