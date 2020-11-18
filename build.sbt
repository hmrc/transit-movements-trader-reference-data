import scoverage.ScoverageKeys
import uk.gov.hmrc.DefaultBuildSettings.integrationTestSettings
import uk.gov.hmrc.SbtArtifactory
import uk.gov.hmrc.sbtdistributables.SbtDistributablesPlugin.publishingSettings

val appName = "transit-movements-trader-reference-data"

val silencerVersion = "1.7.0"

lazy val microservice = Project(appName, file("."))
  .enablePlugins(
    play.sbt.PlayScala,
    SbtAutoBuildPlugin,
    SbtGitVersioning,
    SbtDistributablesPlugin,
    SbtArtifactory
  )
  .disablePlugins(JUnitXmlReportPlugin)
  .settings(
    majorVersion := 0,
    libraryDependencies ++= AppDependencies.compile ++ AppDependencies.test,
    scalafmtOnCompile in ThisBuild := true
  )
  .settings(scalaVersion := "2.12.12")
  .settings(publishingSettings: _*)
  .configs(IntegrationTest)
  .settings(inConfig(Test)(testSettings): _*)
  .settings(integrationTestSettings(): _*)
  .settings(resolvers += Resolver.jcenterRepo)
  .settings(PlayKeys.playDefaultPort := 9482)
  .settings(scoverageSettings: _*)
  .settings(
    // ***************
    // Use the silencer plugin to suppress warnings
    scalacOptions += "-P:silencer:pathFilters=routes",
    libraryDependencies ++= Seq(
      compilerPlugin(
        "com.github.ghik" % "silencer-plugin" % silencerVersion cross CrossVersion.full
      ),
      "com.github.ghik" % "silencer-lib" % silencerVersion % Provided cross CrossVersion.full
    )
    // ***************
  )

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
  fork := true,
  javaOptions ++= Seq(
    "-Dconfig.resource=test.application.conf"
  )
)
