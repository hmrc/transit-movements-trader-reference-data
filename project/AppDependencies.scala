
import play.core.PlayVersion.current
import sbt._

object AppDependencies {

  private val bootstrapVersion = "8.3.0"
  private val catsVersion = "2.10.0"
  private val mongoVersion = "1.6.0"

  val compile: Seq[ModuleID] = Seq(
    "uk.gov.hmrc"        %% "bootstrap-backend-play-29"          % bootstrapVersion,
    "uk.gov.hmrc.mongo"  %% "hmrc-mongo-play-29"                 % mongoVersion,
    "org.typelevel"      %% "cats-core"                          % catsVersion
  )

  val test: Seq[ModuleID] = Seq(
    "uk.gov.hmrc.mongo"      %% "hmrc-mongo-test-play-29"  % mongoVersion,
    "org.scalatest"          %% "scalatest"                % "3.2.17",
    "com.typesafe.play"      %% "play-test"                % current,
    "uk.gov.hmrc"            %% "bootstrap-test-play-29"   % bootstrapVersion,
    "org.mockito"             % "mockito-core"             % "5.2.0",
    "org.scalatestplus"      %% "mockito-4-11"             % "3.2.17.0",
    "org.scalacheck"         %% "scalacheck"               % "1.17.0",
    "org.scalatestplus"      %% "scalacheck-1-17"          % "3.2.17.0",
    "com.vladsch.flexmark"    % "flexmark-all"             % "0.62.2"
  ).map(_ % "test, it")

}
