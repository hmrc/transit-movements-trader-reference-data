package repositories

import java.time.{Clock, Instant, ZoneId}

import models.ReferenceDataList
import org.scalacheck.Gen
import org.scalatest.{BeforeAndAfterEach, OptionValues}
import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.test.Helpers.running
import reactivemongo.play.json.collection.JSONCollection

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
    dropDatabase()
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

        val list = Gen.oneOf(ReferenceDataList.values.toList).sample.value

        val dataImport = DataImport(ImportId(1), list, 1, ImportStatus.Started, Instant.now(stubClock), None)

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

        val list = Gen.oneOf(ReferenceDataList.values.toList).sample.value

        val dataImport = DataImport(ImportId(1), list, 1, ImportStatus.Started, Instant.now(stubClock), None)

        val repo = app.injector.instanceOf[DataImportRepository]

        repo.insert(dataImport).futureValue
        val updateResult = repo.markFinished(ImportId(1), ImportStatus.Complete).futureValue

        updateResult mustEqual DataImport(ImportId(1), list, 1, ImportStatus.Complete, Instant.now(stubClock), Some(Instant.now(stubClock)))
      }
    }
  }

  ".currentImportId" - {

    "must return the highest import Id that's in a Complete status" in {

      val app = appBuilder.build()

      running(app) {

        val list = Gen.oneOf(ReferenceDataList.values.toList).sample.value

        val import1 = DataImport(ImportId(1), list, 1, ImportStatus.Complete, Instant.now(stubClock), Some(Instant.now(stubClock)))
        val import2 = DataImport(ImportId(2), list, 1, ImportStatus.Complete, Instant.now(stubClock), Some(Instant.now(stubClock)))
        val import3 = DataImport(ImportId(3), list, 1, ImportStatus.Failed, Instant.now(stubClock), Some(Instant.now(stubClock)))
        val import4 = DataImport(ImportId(4), list, 1, ImportStatus.Started, Instant.now(stubClock), Some(Instant.now(stubClock)))

        val repo = app.injector.instanceOf[DataImportRepository]

        repo.insert(import1).futureValue
        repo.insert(import2).futureValue
        repo.insert(import3).futureValue
        repo.insert(import4).futureValue

        val result = repo.currentImportId(list).futureValue

        result.value mustEqual ImportId(2)
      }
    }
  }
}
