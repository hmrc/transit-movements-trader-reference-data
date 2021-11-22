
import play.core.PlayVersion.akkaVersion
import play.core.PlayVersion.current
import sbt._

object AppDependencies {
  private val catsVersion = "2.1.1"

  val compile = Seq(
    "uk.gov.hmrc"        %% "bootstrap-backend-play-28"          % "5.16.0",
    "org.reactivemongo"  %% "play2-reactivemongo"                % "0.20.13-play28",
    "com.typesafe.play"  %% "play-iteratees"                     % "2.6.1",
    "com.typesafe.play"  %% "play-iteratees-reactive-streams"    % "2.6.1",
    "org.typelevel"      %% "cats-core"                          % catsVersion,
    "com.lightbend.akka" %% "akka-stream-alpakka-json-streaming" % "2.0.2"
  )

  val test = Seq(
    "org.scalatest"          %% "scalatest"                % "3.2.9",
    "com.typesafe.play"      %% "play-test"                % current,
    "org.scalatestplus.play" %% "scalatestplus-play"       % "5.1.0",
    "org.mockito"            % "mockito-core"              % "3.3.3",
    "org.scalatestplus"      %% "mockito-3-2"              % "3.1.2.0",
    "org.scalatestplus"      %% "scalatestplus-scalacheck" % "3.1.0.0-RC2",
    "org.scalacheck"         %% "scalacheck"               % "1.14.3",
    "com.typesafe.akka"      %% "akka-stream-testkit"      % akkaVersion,
    "com.typesafe.akka"      %% "akka-slf4j"               % akkaVersion,
    "com.vladsch.flexmark"    % "flexmark-all"             % "0.35.10",
    "com.github.tomakehurst"  % "wiremock-standalone"      % "2.27.2"
  ).map(_ % "test, it")

}
