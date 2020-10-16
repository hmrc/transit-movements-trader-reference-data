import play.core.PlayVersion.current
import play.sbt.PlayImport._
import sbt.Keys.libraryDependencies
import sbt._

object AppDependencies {

  val compile = Seq(
    "uk.gov.hmrc" %% "bootstrap-play-26"    % "1.16.0"
  )

  val test = Seq(
    "uk.gov.hmrc"            %% "bootstrap-play-26"  % "1.16.0"   % Test classifier "tests",
    "org.scalatest"          %% "scalatest"          % "3.0.8"   % "test",
    "com.typesafe.play"      %% "play-test"          % current   % "test",
    "org.pegdown"            % "pegdown"             % "1.6.0"   % "test, it",
    "org.mockito"            % "mockito-all"         % "1.10.19" % "test",
    "org.mockito"            % "mockito-core"        % "2.24.5"  % "test,it",
    "org.scalatestplus.play" %% "scalatestplus-play" % "3.1.2"   % "test, it",
    "org.scalacheck"         %% "scalacheck"         % "1.14.1"  % "test"
  )
}
