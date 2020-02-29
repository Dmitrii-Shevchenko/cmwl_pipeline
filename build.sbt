import Dependencies._

ThisBuild / version := "0.1"
ThisBuild / scalaVersion := "2.12.9"
ThisBuild / scalacOptions := Seq(
  "-encoding",
  "utf8",
  "-deprecation",
  "-Xfatal-warnings"
)

lazy val IntegrationTest = config("it").extend(Test)

lazy val formatAll = taskKey[Unit]("Format all the source code which includes src, test, and build files")
lazy val checkFormat = taskKey[Unit]("Check all the source code which includes src, test, and build files")

lazy val commonSettings = Seq(
  formatAll := {
    (scalafmt in Compile).value
    (scalafmt in Test).value
  },
  checkFormat := {
    (scalafmtCheck in Compile).value
    (scalafmtCheck in Test).value
  },
  compile in Compile := (compile in Compile).dependsOn(checkFormat).value,
  test in Test := (test in Test).dependsOn(checkFormat).value,
  testOptions in Test += Tests.Argument(TestFrameworks.ScalaTest, "-h", "target/test-reports"),
  testOptions in IntegrationTest += Tests.Argument(TestFrameworks.ScalaTest, "-h", "target/test-reports")
)

addCommandAlias("fmt", "all scalafmtSbt scalafmt test:scalafmt")
addCommandAlias("check", "all scalafmtSbtCheck scalafmtCheck test:scalafmtCheck")

lazy val root = (project in file("."))
  .settings(
    name := "Cromwell pipeline",
    commonSettings
  )
  .aggregate(portal, datasource)

lazy val datasource = project
  .settings(
    name := "Datasource",
    commonSettings,
    libraryDependencies ++= dbDependencies
  )
  .dependsOn(utils)

lazy val portal = project
  .configs(IntegrationTest)
  .settings(
    name := "Portal",
    commonSettings,
    Seq(parallelExecution in Test := false),
    libraryDependencies ++= akkaDependencies ++ jsonDependencies,
    Defaults.itSettings,
    //TODO need to check out parallel execution
    addCommandAlias("testAll", "; test ; it:test")
  )
  .aggregate(repositories, services, controllers, utils)
  .dependsOn(
    datasource,
    repositories % "compile->compile;test->test",
    services,
    controllers,
    utils % "compile->compile;test->test"
  )

lazy val utils =
  (project in file("utils")).settings(libraryDependencies ++= jsonDependencies ++ testContainers)

lazy val repositories =
  (project in file("repositories"))
    .settings(
      libraryDependencies ++= akkaDependencies ++ testDependencies ++ jsonDependencies :+ cats,
      Seq(parallelExecution in Test := false)
    )
    .configs(IntegrationTest)
    .dependsOn(datasource, utils % "compile->compile;test->test")

lazy val services =
  (project in file("services"))
    .settings(libraryDependencies ++= jsonDependencies :+ cats)
    .dependsOn(repositories % "compile->compile;test->test", utils % "compile->compile;test->test")

lazy val controllers =
  (project in file("controllers"))
    .settings(
      libraryDependencies ++= akkaDependencies ++ jsonDependencies :+ cats
    )
    .dependsOn(services, utils, repositories % "test->test")
