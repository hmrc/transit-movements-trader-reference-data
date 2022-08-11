package controllers.testOnly.testmodels

import play.api.libs.json.Format
import play.api.libs.json.Json

/** UN/LOCODE (United Nations Code for Trade and Transport Locations) */
case class UnLocode(
  unLocodeExtendedCode: String,
  name: String,
  subdivision: Option[String],
  function: String,
  status: String,
  date: String,
  coordinates: Option[String],
  comment: Option[String]
)

object UnLocode {
  implicit val format: Format[UnLocode] = Json.format[UnLocode]
}
