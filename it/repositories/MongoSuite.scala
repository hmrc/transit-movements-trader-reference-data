/*
 * Copyright 2020 HM Revenue & Customs
 *
 */

package repositories

import com.typesafe.config.ConfigFactory
import org.scalatest._
import org.scalatest.concurrent.ScalaFutures
import play.api.Configuration
import reactivemongo.api.MongoConnection.ParsedURI
import reactivemongo.api._
import reactivemongo.play.json.collection.JSONCollection

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future


object MongoSuite {

  private lazy val config = Configuration(ConfigFactory.load(System.getProperty("config.resource")))

  lazy val connection: Future[(ParsedURI, MongoConnection)] =
    for {
      parsedUri  <- MongoConnection.fromString(config.get[String]("mongodb.uri"))
      connection <- AsyncDriver().connect(parsedUri)
    } yield (parsedUri, connection)
}

trait MongoSuite extends ScalaFutures {
  self: TestSuite =>

  def database: Future[DefaultDB] =
    MongoSuite.connection.flatMap {
      case (uri, connection) =>
        connection.database(uri.db.get)
    }

  def dropDatabase(): Unit = {
    database.flatMap {
      db =>
        for {
          _ <- db.collection[JSONCollection](DataImportRepository.collectionName).drop(failIfNotFound = false)
          _ <- db.collection[JSONCollection](ImportIdRepository.collectionName).drop(failIfNotFound = false)
          _ <- Future.sequence(
            ListRepository.indexes.map {
            indexOnList =>
              db.collection[JSONCollection](indexOnList.list.listName).drop(failIfNotFound = false)
          })
        } yield ()
    }.futureValue
  }
}
