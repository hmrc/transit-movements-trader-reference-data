package repositories

import org.scalatest.{BeforeAndAfterEach, OptionValues}
import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.libs.json.Json
import play.api.test.Helpers.running
import reactivemongo.play.json.collection.Helpers.idWrites
import reactivemongo.play.json.collection.JSONCollection

import scala.concurrent.ExecutionContext.Implicits.global

class ImportIdRepositorySpec
  extends AnyFreeSpec
    with Matchers
    with MongoSuite
    with ScalaFutures
    with BeforeAndAfterEach
    with IntegrationPatience
    with OptionValues {

  override def beforeEach(): Unit = {
    dropDatabase()
    super.beforeEach()
  }

  "Import Id Repository" - {

    "must return sequential ids starting at 1" in {

      val app = new GuiceApplicationBuilder().build()

      running(app) {
        val repo = app.injector.instanceOf[ImportIdRepository]

        val first  = repo.nextId.futureValue
        val second = repo.nextId.futureValue

        first mustEqual ImportId(1)
        second mustEqual ImportId(2)
      }
    }

    "must not fail if the collection already has a document on startup" in {

      val initialRecord = Json.obj("_id" -> "last-id", "import-id" -> 123)

      database.flatMap {
        _.collection[JSONCollection]("import-ids")
          .insert(ordered = false)
          .one(initialRecord)
      }.futureValue

      val app = new GuiceApplicationBuilder().build()

      running(app) {
        val repo = app.injector.instanceOf[ImportIdRepository]
        repo.nextId.futureValue mustEqual ImportId(124)
      }
    }
  }
}
