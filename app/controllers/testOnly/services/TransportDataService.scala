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

package controllers.testOnly.services

import controllers.testOnly.testmodels.{Nationality, TransportAggregateData}
import play.api.Environment

import javax.inject.Inject
import javax.inject.Singleton
import scala.concurrent.ExecutionContext
import scala.concurrent.Future

@Singleton
class TransportDataService @Inject() (override val env: Environment)(implicit ec: ExecutionContext) extends ResourceService {


  val nationalities: Seq[Nationality] =
    getData[Nationality]("resources/nationalities.json")


  def aggregateData: TransportAggregateData = {
    TransportAggregateData(nationalities.toList)
  }

  def getTransportAggregate: Future[TransportAggregateData] = Future {
    aggregateData
  }

}
