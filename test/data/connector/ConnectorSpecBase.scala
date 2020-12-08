package data.connector
import base.SpecBaseWithAppPerSuite
import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig
import org.scalatest.BeforeAndAfterAll
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.inject.guice.GuiceableModule

trait ConnectorSpecBase extends SpecBaseWithAppPerSuite with BeforeAndAfterAll {

  protected val server: WireMockServer = new WireMockServer(wireMockConfig().dynamicPort())

  /**
    *
    * @return The name of the config key for the external service
    */
  protected def portConfigKey: String

  /**
    * An overrideable hook that allows for overriding the configuration
    * of  guice module in the test suite
    *
    * @return Seq of modules binding that will be used by [[org.scalatestplus.play.guice.GuiceOneAppPerSuite]]
    */
  protected def bindings: Seq[GuiceableModule] = Seq.empty

  override def guiceApplicationBuilder: GuiceApplicationBuilder =
    super.guiceApplicationBuilder
      .configure(
        portConfigKey -> server.port().toString
      )
      .overrides(bindings: _*)

  override def beforeAll(): Unit = {
    server.start()
    super.beforeAll()
  }

  override def beforeEachBlocks: Seq[() => Unit] =
    super.beforeEachBlocks ++ Seq(
      () => server.resetAll()
    )

  override def afterAll(): Unit = {
    super.afterAll()
    server.stop()
  }

}
