package controllers

import javax.inject.Inject
import play.api.mvc.{Action, AnyContent, ControllerComponents}
import uk.gov.hmrc.play.bootstrap.controller.BackendController

class TransitOfficeController @Inject()
(cc: ControllerComponents) extends BackendController(cc) {

  def transitOffices(): Action[AnyContent] = ???
}



