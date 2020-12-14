package repositories

import java.time.Instant

import org.scalatest.{BeforeAndAfterEach, OptionValues}
import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.test.Helpers.running

import scala.concurrent.ExecutionContext.Implicits.global

class DataImportRepositorySpec
  extends AnyFreeSpec
    with Matchers
    with MongoSuite
    with ScalaFutures
    with IntegrationPatience
    with OptionValues
    with BeforeAndAfterEach {

  override def beforeEach(): Unit = {
    database.flatMap(_.drop).futureValue
    super.beforeEach()
  }

  "Data Import Repository" - {

    "must insert a new data import and retrieve it" in {

      val app = new GuiceApplicationBuilder().build()

      running(app) {

        val dataImport = DataImport(ImportId(1), ImportStatus.Started, Instant.now, None)

        val repo = app.injector.instanceOf[DataImportRepository]

        val insertResult = repo.insert(dataImport).futureValue
        val getResult    = repo.get(dataImport.importId).futureValue

        insertResult mustEqual true
        getResult.value mustEqual dataImport
      }
    }

    "must mark a record as finished" in {

      val app = new GuiceApplicationBuilder().build()

      running(app) {

        val dataImport = DataImport(ImportId(1), ImportStatus.Started, Instant.now, None)

        val repo = app.injector.instanceOf[DataImportRepository]

        repo.insert(dataImport).futureValue
        val updateResult = repo.markFinished(ImportId(1), ImportStatus.Complete).futureValue
        val getResult    = repo.get(dataImport.importId).futureValue.value

        updateResult mustEqual true
        getResult.status mustEqual ImportStatus.Complete
        getResult.finished mustBe defined
      }
    }
  }
}
