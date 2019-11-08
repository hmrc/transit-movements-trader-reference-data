import scoverage.ScoverageKeys
import uk.gov.hmrc.DefaultBuildSettings.integrationTestSettings
import uk.gov.hmrc.SbtArtifactory
import uk.gov.hmrc.sbtdistributables.SbtDistributablesPlugin.publishingSettings

val appName = "transit-movements-trader-reference-data"

lazy val microservice = Project(appName, file("."))
  .enablePlugins(
    play.sbt.PlayScala,
    SbtAutoBuildPlugin,
    SbtGitVersioning,
    SbtDistributablesPlugin,
    SbtArtifactory
  )
  .settings(
    majorVersion := 0,
    libraryDependencies ++= AppDependencies.compile ++ AppDependencies.test
  )
  .settings(publishingSettings: _*)
  .configs(IntegrationTest)
  .settings(inConfig(Test)(testSettings): _*)
  .settings(integrationTestSettings(): _*)
  .settings(resolvers += Resolver.jcenterRepo)
  .settings(PlayKeys.playDefaultPort := 9482)
  .settings(scoverageSettings: _*)

lazy val scoverageSettings = {
  Seq(
    ScoverageKeys.coverageExcludedPackages := List(
      "<empty>",
      "Reverse.*",
      "config.*",
      ".*(BuildInfo|Routes).*"
    ).mkString(";"),
    ScoverageKeys.coverageMinimum := 85.00,
    ScoverageKeys.coverageExcludedFiles := "<empty>;.*javascript.*;.*Routes.*;",
    ScoverageKeys.coverageFailOnMinimum := true,
    ScoverageKeys.coverageHighlighting := true,
    parallelExecution in Test := false
  )
}

lazy val testSettings: Seq[Def.Setting[_]] = Seq(
  fork        := true,
  javaOptions ++= Seq(
    "-Dconfig.resource=test.application.conf"
  )
)
