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

package data.config

import akka.event.Logging
import akka.event.Logging.LogLevel
import javax.inject.Inject
import play.api.Configuration

class StreamLoggingConfig @Inject() (config: Configuration) {

  private val pathToConfig: String = "data.stream.logging"

  /**
    * Configuration for the logging level of AkkaStream components. This allows for global
    * application configuration, or stream component specific configuration.
    *
    * @param streamComponentName if provided, this will be used for this component over the
    *                            global value at `data.stream.logging.onElement.level`,
    *                            `data.stream.logging.onFinish.level` or
    *                            `data.stream.logging.onFailure.level`
    *
    * @return (onElement, onFinish, onFailure)
    */
  def loggingConfig(streamComponentName: Option[String] = None): (LogLevel, LogLevel, LogLevel) =
    (
      onElement(streamComponentName),
      onFinish(streamComponentName),
      onFailure(streamComponentName)
    )

  private def streamLoggingConfigPath(streamComponentName: Option[String]): String =
    streamComponentName.fold(pathToConfig) {
      name =>
        s"$pathToConfig.$name"
    }

  private def onElement(streamComponentName: Option[String]): LogLevel =
    config
      .getOptional[LogLevel](streamLoggingConfigPath(streamComponentName) + ".onElement")
      .getOrElse(Logging.levelFor("off").get)

  private def onFinish(streamComponentName: Option[String]): LogLevel =
    config
      .getOptional[LogLevel](streamLoggingConfigPath(streamComponentName) + ".onFinish")
      .getOrElse(Logging.levelFor("off").get)

  private def onFailure(streamComponentName: Option[String]): LogLevel =
    config
      .getOptional[LogLevel](streamLoggingConfigPath(streamComponentName) + ".onFailure")
      .getOrElse(Logging.levelFor("warn").get)

}
