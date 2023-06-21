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

package controllers.testOnly.services

import controllers.testOnly.helpers.P5
import controllers.testOnly.helpers.Version
import controllers.testOnly.testmodels.DocumentType
import controllers.testOnly.testmodels.PreviousDocumentType
import controllers.testOnly.testmodels.SupportingDocumentType
import controllers.testOnly.testmodels.TransportDocumentType
import play.api.Environment

import javax.inject.Inject

private[testOnly] class DocumentTypeService @Inject() (override val env: Environment, config: ResourceConfig) extends ResourceService {

  def documentTypes(version: Option[Version]): Seq[DocumentType] = {
    val resource = version match {
      case Some(P5) => config.documentTypesP5
      case _        => config.documentTypes
    }
    getData[DocumentType](resource)
  }

  def previousDocumentTypes(version: Option[Version] = None): Seq[PreviousDocumentType] = {
    val resource = version match {
      case Some(P5) => config.previousDocumentTypesP5
      case _        => config.previousDocumentTypes
    }
    getData[PreviousDocumentType](resource)
  }

  def getPreviousDocumentTypeByCode(code: String): Option[PreviousDocumentType] =
    previousDocumentTypes().find(_.code == code)

  def supportingDocumentTypes(): Seq[SupportingDocumentType] =
    getData[SupportingDocumentType](config.supportingDocumentTypesP5)

  def transportDocumentTypes(): Seq[TransportDocumentType] =
    getData[TransportDocumentType](config.transportDocumentTypesP5)
}
