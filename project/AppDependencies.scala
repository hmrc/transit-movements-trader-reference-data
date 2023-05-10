
import play.core.PlayVersion.current
import sbt._

object AppDependencies {

  private val bootstrapVersion = "7.15.0"
  private val catsVersion = "2.8.0"
  private val mongoVersion = "1.2.0"

  val compile: Seq[ModuleID] = Seq(
    "uk.gov.hmrc"        %% "bootstrap-backend-play-28"          % bootstrapVersion,
    "uk.gov.hmrc.mongo"  %% "hmrc-mongo-play-28"                 % mongoVersion,
    "org.typelevel"      %% "cats-core"                          % catsVersion
  )

  val test: Seq[ModuleID] = Seq(
    "uk.gov.hmrc.mongo"      %% "hmrc-mongo-test-play-28"  % mongoVersion,
    "org.scalatest"          %% "scalatest"                % "3.2.14",
    "com.typesafe.play"      %% "play-test"                % current,
    "uk.gov.hmrc"            %% "bootstrap-test-play-28"   % bootstrapVersion,
    "org.mockito"             % "mockito-core"             % "4.8.0",
    "org.scalatestplus"      %% "mockito-4-6"              % "3.2.14.0",
    "org.scalacheck"         %% "scalacheck"               % "1.17.0",
    "org.scalatestplus"      %% "scalacheck-1-17"          % "3.2.14.0",
    "com.github.tomakehurst"  % "wiremock-standalone"      % "2.27.2",
    "com.vladsch.flexmark"    % "flexmark-all"             % "0.62.2"
  ).map(_ % "test, it")

}
