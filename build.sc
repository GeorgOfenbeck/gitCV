import mill._, scalalib._, publish._

trait MyModule extends PublishModule {
  def publishVersion = "0.0.1"

  def pomSettings = PomSettings(
    description = "gitCV",
    organization = "com.ofenbeck",
    url = "https://github.com/GeorgOfenbeck/gitCV",
    licenses = Seq(License.MIT),
    versionControl = VersionControl.github("GeorgOfenbeck", "gitCV"),
    developers = Seq(Developer("ofenbeck", "Georg Ofenbeck", "https://github.com/GeorgOfenbeck"))
  )
}

import mill._, scalalib._

object gitCV extends RootModule with ScalaModule {
  def scalaVersion = "3.3.2"

  // You can have arbitrary numbers of third-party dependencies
  def ivyDeps = Agg(
    ivy"ch.qos.logback:logback-classic:1.2.12",
  )

  override def mainClass = Some("com.example.PekkoQuickstart")

  // Additional Scala compiler options, e.g. to turn warnings into errors
  def scalacOptions: T[Seq[String]] = Seq(
    "-deprecation",
    "-feature",
    "-language:implicitConversions",
    "-unchecked",
    "-Xfatal-warnings",
    "-Yexplicit-nulls",
    "-Ysafe-init",
  )
}