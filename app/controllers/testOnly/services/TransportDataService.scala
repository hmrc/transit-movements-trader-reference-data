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

import controllers.testOnly.testmodels.TransportAggregateData

import javax.inject.Inject
import javax.inject.Singleton
import scala.concurrent.ExecutionContext
import scala.concurrent.Future

@Singleton
class TransportDataService @Inject() (transportModeService: TransportModeService)(implicit ec: ExecutionContext) {

  def aggregateData: TransportAggregateData = {
    val nationalities = transportModeService.nationalities
    val means         = transportModeService.transportMeans
    val modes         = transportModeService.transportModes

    TransportAggregateData(modes.toList, means.toList, nationalities.toList)
  }

  def getTransportAggregate: Future[TransportAggregateData] = Future {
    aggregateData
  }

}
