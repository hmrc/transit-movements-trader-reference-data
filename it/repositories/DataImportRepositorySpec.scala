package repositories

import java.time.{Clock, Instant, ZoneId}

import org.scalatest.{BeforeAndAfterEach, OptionValues}
import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers
import play.api.inject.bind
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

  private val instant = Instant.now
  private val stubClock: Clock = Clock.fixed(instant, ZoneId.systemDefault)

  private val appBuilder: GuiceApplicationBuilder =
    new GuiceApplicationBuilder()
      .overrides(bind[Clock].toInstance(stubClock))

  "Data Import Repository" - {

    "must insert a new data import and retrieve it" in {

      val app = appBuilder.build()

      running(app) {

        val dataImport = DataImport(ImportId(1), ImportStatus.Started, Instant.now(stubClock), None)

        val repo = app.injector.instanceOf[DataImportRepository]

        val insertResult = repo.insert(dataImport).futureValue
        val getResult    = repo.get(dataImport.importId).futureValue

        insertResult mustEqual true
        getResult.value mustEqual dataImport
      }
    }

    "must mark a record as finished" in {

      val app = appBuilder.build()

      running(app) {

        val dataImport = DataImport(ImportId(1), ImportStatus.Started, Instant.now(stubClock), None)

        val repo = app.injector.instanceOf[DataImportRepository]

        repo.insert(dataImport).futureValue
        val updateResult = repo.markFinished(ImportId(1), ImportStatus.Complete).futureValue
        val getResult    = repo.get(dataImport.importId).futureValue.value

        updateResult mustEqual DataImport(ImportId(1), ImportStatus.Complete, Instant.now(stubClock), Some(Instant.now(stubClock)))
      }
    }
  }
}
