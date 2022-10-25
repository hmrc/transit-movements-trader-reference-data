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

import controllers.testOnly.testmodels.{Nationality, TransportAggregateData, TransportMeans}

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class ReferenceDataService2 @Inject()(transportModeService: TransportModeService)(implicit ec:ExecutionContext)  {


  def aggregateData: TransportAggregateData = {
    val nationalities: List[Nationality] = List(
      Nationality("FR", "France"),
      Nationality("DE", "Germany")
    )
    val means: List[TransportMeans] = List(TransportMeans("Name of a sea-going vehicle"),TransportMeans("IATA flight number"),
                TransportMeans("Name of an inland waterways vehicle"), TransportMeans("IMO ship identification number"))
    val modes = transportModeService.transportModes

    TransportAggregateData(modes.toList, means, nationalities)
  }

  def getTransportAggregate: Future[TransportAggregateData] = Future {
    aggregateData
  }

}
