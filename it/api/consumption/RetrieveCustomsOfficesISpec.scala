package api.consumption

import org.scalatest.{BeforeAndAfterEach, OptionValues}
import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers
import repositories.MongoSuite

class RetrieveCustomsOfficesISpec extends AnyFreeSpec
  with Matchers
  with MongoSuite
  with BeforeAndAfterEach
  with ScalaFutures
  with IntegrationPatience
  with OptionValues {

}
