package repositories
import org.scalatest.{BeforeAndAfterEach, OptionValues}
import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.test.Helpers.running
import reactivemongo.play.json.collection.JSONCollection

import scala.concurrent.ExecutionContext.Implicits.global

class ListCollectionIndexManagerSpec
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

  "List Collection Index Manager" - {

    "must create all of its indexes on startup" in {

      val app = new GuiceApplicationBuilder().build()

      running(app) {
        val indexManager = app.injector.instanceOf[ListCollectionIndexManager]

        indexManager.started.futureValue

        indexManager.indexes.foreach {
          indexOnList =>
            val databaseIndexes =
              database.flatMap {
                _.collection[JSONCollection](indexOnList.list.listName)
                  .indexesManager.list
              }.futureValue

            databaseIndexes.exists(i => i.name == indexOnList.index.name && i.key == indexOnList.index.key) mustEqual true
        }
      }
    }
  }
}
