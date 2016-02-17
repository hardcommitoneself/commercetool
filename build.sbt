import java.text.SimpleDateFormat
import java.util.Date

import play.sbt.PlayImport
import complete.DefaultParsers._

import scala.util.{Success, Try}

import ReleaseTransformations._

name := "commercetools-sunrise"

organization := "io.commercetools.sunrise"

lazy val sunriseDesignVersion = "0.52.0"

lazy val jvmSdkVersion = "1.0.0-RC1"

lazy val jacksonVersion = "2.6.0"


/**
 * SUB-PROJECT DEFINITIONS
 */

lazy val commonWithTests: ClasspathDep[ProjectReference] = common % "compile;test->test;it->it;pt->pt"

lazy val `commercetools-sunrise` = (project in file("."))
  .enablePlugins(PlayJava, DockerPlugin).configs(IntegrationTest, PlayTest).settings(commonSettings ++ dockerSettings: _*)
  .dependsOn(commonWithTests, `product-catalog`, `shopping-cart`, `setup-widget`)
  .aggregate(common, `product-catalog`, `shopping-cart`, `setup-widget`, `move-to-sdk`)

lazy val common = project
  .enablePlugins(PlayJava).configs(IntegrationTest, PlayTest).settings(commonSettings ++ disableDockerPublish: _*)
  .dependsOn(`move-to-sdk`)

lazy val `product-catalog` = project
  .enablePlugins(PlayJava).configs(IntegrationTest, PlayTest).settings(commonSettings ++ disableDockerPublish: _*)
  .dependsOn(commonWithTests)

lazy val `shopping-cart` = project
  .enablePlugins(PlayJava).configs(IntegrationTest, PlayTest).settings(commonSettings ++ disableDockerPublish: _*)
  .dependsOn(commonWithTests)

lazy val `setup-widget` = project
  .enablePlugins(PlayJava).configs(IntegrationTest, PlayTest).settings(commonSettings ++ disableDockerPublish: _*)

lazy val `move-to-sdk` = project
  .enablePlugins(PlayJava).configs(IntegrationTest, PlayTest).settings(commonSettings ++ disableDockerPublish: _*)

/**
 * COMMON SETTINGS
 */

javaUnidocSettings

lazy val commonSettings = testSettings ++ releaseSettings ++ Seq (
  scalaVersion := "2.10.6",
  javacOptions ++= Seq("-source", "1.8", "-target", "1.8"),
  resolvers ++= Seq (
    Resolver.sonatypeRepo("releases"),
    Resolver.sonatypeRepo("snapshots"),
    Resolver.bintrayRepo("commercetools", "maven"),
    Resolver.mavenLocal
  ),
  libraryDependencies ++= Seq (
    filters,
    "io.commercetools" % "commercetools-sunrise-design" % sunriseDesignVersion,
    "com.commercetools.sdk.jvm.core" % "commercetools-models" % jvmSdkVersion,
    "com.commercetools.sdk.jvm.scala-add-ons" %% "commercetools-play-2_4-java-client" % jvmSdkVersion,
    "org.webjars" %% "webjars-play" % "2.4.0-1",
    "com.github.jknack" % "handlebars" % "2.3.2",
    "commons-beanutils" % "commons-beanutils" % "1.9.2",
    "commons-io" % "commons-io" % "2.4"
  ),
  dependencyOverrides ++= Set (
    "com.google.guava" % "guava" % "18.0",
    "commons-io" % "commons-io" % "2.4",
    "commons-logging" % "commons-logging" % "1.1.3",
    "io.netty" % "netty" % "3.10.4.Final",
    "com.fasterxml.jackson.core" % "jackson-annotations" % jacksonVersion,
    "com.fasterxml.jackson.core" % "jackson-core" % jacksonVersion,
    "com.fasterxml.jackson.core" % "jackson-databind" % jacksonVersion,
    "com.fasterxml.jackson.module" % "jackson-module-parameter-names" % jacksonVersion,
    "com.fasterxml.jackson.datatype" % "jackson-datatype-jsr310" % jacksonVersion,
    "org.scala-lang" % "scala-library" % "2.10.6",
    "org.scala-lang" % "scala-reflect" % "2.10.6"
  )
)

lazy val dockerSettings = Seq(
  version in Docker := "latest",
  packageName in Docker := "sunrise",
  dockerRepository := Some("sphereio"),
  dockerExposedPorts := Seq(9000),
  dockerCmd := Seq("-Dconfig.resource=prod.conf", "-Dlogger.resource=docker-logger.xml")
)

lazy val disableDockerPublish = Seq(
  publish in Docker := {},
  publishLocal in Docker := {}
)

/**
 * TEST SETTINGS
 */
lazy val PlayTest = config("pt") extend(Test)

lazy val testScopes = "test,it,pt"

lazy val testSettings = Defaults.itSettings ++ inConfig(PlayTest)(Defaults.testSettings) ++ testDirConfigs(IntegrationTest, "it") ++ testDirConfigs(PlayTest, "pt") ++ Seq (
  testOptions += Tests.Argument(TestFrameworks.JUnit, "-v"),
  unmanagedBase in Test := baseDirectory.value / "test" / "lib",
  libraryDependencies ++= Seq (
    javaWs % "pt",
    "org.assertj" % "assertj-core" % "3.0.0" % testScopes,
    PlayImport.component("play-test") % "it,pt"
  ),
  dependencyOverrides ++= Set (
    "junit" % "junit" % "4.12" % testScopes
  )
)

def testDirConfigs(config: Configuration, folderName: String) = Seq(
  javaSource in config := baseDirectory.value / folderName,
  scalaSource in config := baseDirectory.value / folderName,
  resourceDirectory in config := baseDirectory.value / s"$folderName/resources"
)

resourceGenerators in Compile += Def.task {
  val file = (resourceManaged in Compile).value / "internal" / "version.json"
  val date = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZZ").format(new Date)

  val gitCommit = Try(Process("git rev-parse HEAD").lines.head) match {
    case Success(sha) => sha
    case _ => "unknown"
  }
  val buildNumber = Option(System.getenv("BUILD_NUMBER")).getOrElse("unknown")
  val contents = s"""{
                     |  "version" : "${version.value}",
                     |  "build" : {
                     |    "date" : "$date",
                     |    "number" : "$buildNumber",
                     |    "gitCommit" : "$gitCommit"
                     |  }
                     |}""".stripMargin
  IO.write(file, contents)
  Seq(file)
}.taskValue

/**
 * RELEASE SETTINGS
 */
lazy val releaseSettings = Seq(
  releaseProcess := Seq[ReleaseStep](
    checkSnapshotDependencies,
    inquireVersions,
    runTest,
    setReleaseVersion,
    commitReleaseVersion,
    tagRelease,
    setNextVersion,
    commitNextVersion,
    pushChanges
  )
)

/**
 * TEMPLATE SETTINGS
 */

val copyTemplateFiles = inputKey[Unit]("Copies the provided template files into the project to enable editing, e.g.: 'copyTemplateFiles common/logo.hbs cart.hbs'")

copyTemplateFiles := Def.inputTaskDyn {
  val args: Seq[String] = spaceDelimited("<arg>").parsed
  val templatePaths: Seq[String] = args.map(filePath => "templates/" + filePath)
  val confFolder: String = (resourceDirectory in Compile).value.getPath
  runMainInCompile(confFolder, templatePaths)
}.evaluated

val copyI18nFiles = inputKey[Unit]("Copies the provided i18n files into the project to enable editing, e.g.: 'copyI18nFiles en/home.yaml de/home.yaml'")

copyI18nFiles := Def.inputTaskDyn {
  val args: Seq[String] = spaceDelimited("<arg>").parsed
  val i18nPaths: Seq[String] = args.map(filePath => "i18n/" + filePath)
  val confFolder: String = (resourceDirectory in Compile).value.getPath
  runMainInCompile(confFolder, i18nPaths)
}.evaluated

def runMainInCompile(dest: String, args: Seq[String]) = Def.taskDyn {
  (runMain in Compile).toTask(s" tasks.WebjarsFilesCopier $dest ${args.mkString(" ")}")
}
