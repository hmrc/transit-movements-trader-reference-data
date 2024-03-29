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

import controllers.testOnly.testmodels.DocumentType
import controllers.testOnly.testmodels.PreviousDocumentType
import play.api.Environment

import javax.inject.Inject

private[testOnly] class DocumentTypeService @Inject() (override val env: Environment, config: ResourceConfig) extends ResourceService {

  def documentTypes(): Seq[DocumentType] =
    getData[DocumentType](config.documentTypes)

  def previousDocumentTypes(): Seq[PreviousDocumentType] =
    getData[PreviousDocumentType](config.previousDocumentTypes)

  def getPreviousDocumentTypeByCode(code: String): Option[PreviousDocumentType] =
    previousDocumentTypes().find(_.code == code)
}
