package com.ofenbeck.gitcv

import zio.json._
import com.ofenbeck.gitcv.CV
import com.ofenbeck.gitcv.WorkExperince
import java.time.LocalDate
import com.ofenbeck.gitcv.Project
import scala.com.ofenbeck.gitcv.CV2Git

object Main extends App {

  
  def SwisscomHealth = {

    val startDateTime = LocalDate.of(2022, 2, 1).nn

    val medicalPracticeManagmentSoftware = Project(
      start = startDateTime,
      title = "Medical Practice Managment Software",
      description =
        "I was responsible for improving scalability and maintainability issues. The software is used by ~1000 of medical practices in Switzerland.",
      technologies = List(
        Technology(
          start = startDateTime, 
          "Kubernetes, AKS, Cilium",
          "Improving scalability and maintainability issues"
        ),
        Technology(
          start = startDateTime,
          "Azure managed services (Managed DB, Managed Storage Solutions)",
          "Migration from vm based solutions to managed services"
        )
      )
    )

    val ragnarok = Project(
      start = startDateTime,
      title = "Ragnarok",
      description =
        "Design and implementation of a cloud native serverless application for a medical data messing system.",
      technologies = List(
        Technology(
          start = startDateTime,
          "Kotlin Spring Cloud, Azure Functions",
          "A serverless redesign of the existing classic java application"
        ),
        Technology(
          start = startDateTime,
          "Azure managed storage Solutions",
          "Using managed blob, table and queue storage for the serverless application"
        ),
        Technology(
          start = startDateTime,
          "Open Telemetry", "Implementation of distributed tracing")
      )
    )
    val devOps = Project(
      start = startDateTime,
      title = "DevOps",
      description =
        "Modernizing or creating new CI/CD pipelines for various products",
      technologies = List(
        Technology(
          start = startDateTime,
          "Azure DevOps", "Migration from Jenkins to Azure DevOps"),
        Technology(
          start = startDateTime,
          "Terraform",
          "Migration from ARM templates and ansible to Terraform"
        ),
        Technology(
          start = startDateTime,
          "Dev Container / Dev Setup",
          "Lowering the barrier for new developers to start working on the project and creating faster feedback loops"
        )
      )
    )

    WorkExperince(
      end = LocalDate.of(2024, 4, 1).nn,
      start = LocalDate.of(2022, 2, 1).nn,
      title = "Senior Cloud Architect",
      company = "Swisscom Health",
      description =
        "Implementation of a big data pipeline for machine learning. Implementation of a high throughput streaming application.",
      projects = List(
        medicalPracticeManagmentSoftware,
        devOps,
        ragnarok
      )
    )
  }

  def SwisscomDNA = {
    val startDateTime = LocalDate.of(2018, 4, 3).nn
    WorkExperince(
      end = LocalDate.of(2022, 1, 31).nn,
      start = startDateTime, 
      title = "Senior Software Engineer",
      company = "Data, Analytics und AI Department, Swisscom",
      description =
        "Implementation of a big data pipeline for machine learning. Implementation of a high throughput streaming application.",
      projects = List(
        Project(
          start = startDateTime,
          title = "Big Data Pipeline for Machine Learning",
          description =
            "Implementing features and improving performance on the ETL data pipeline for ML applications. Reduced the Dev Cycle from larger than a working day to an hour.",
          technologies = List(
            Technology(
              start = startDateTime,
              "Apache Spark, HDFS, Cloud Foundary",
              "working on two different on-premises big data applications"
            )
          )
        ),
        Project(
          start = startDateTime,
          title = "High Throughput Streaming Application",
          description =
            "Implementation and operation of a real-time streaming application processing 6k msgs/s triggering interactions with customers.",
          technologies = List(
            Technology(
              start = startDateTime,
              "Kafka, Akka Streaming, Flink", "")
          )
        )
      )
    )
  }

  val ethPhd = Education(
    end = LocalDate.of(2017, 5, 1).nn,
    start = LocalDate.of(2011, 10, 1).nn,
    title = "PhD, Computer Science",
    school = "ETH Zurich",
    description =
      "I worked on the topic of appling generic programming techniques to challenges in metaprogramming in the context of high performance code generators.",
      publications = List.empty,
  )
  val ethPhd0 = Education(
    end = LocalDate.of(2011, 9, 30).nn,
    start = LocalDate.of(2010, 8, 1).nn,
    title = "PhD student, Computer Science",
    school = "ETH Zurich",
    description =
      "Before switching topic, I worked my first year on enabling micro transaction support in the kernel of an OS",
      publications = List.empty,
  )
  val ethMsc = Education(
    end = LocalDate.of(2009, 11, 1).nn,
    start = LocalDate.of(2007, 9, 1).nn,
    title = "MSc, Computational Biology and Bioinformatics",
    school = "ETH Zurich",
    description = "",
    publications = List.empty,
  )

  val tuBsc = Education(
    end = LocalDate.of(2007, 6, 1).nn,
    start = LocalDate.of(2003, 9, 1).nn,
    title = "BSc, Medical Informatics",
    school = "Technical University Vienna",
    description = "", 
    publications = List.empty,
  )

  val vieBsc = Education(
    end = LocalDate.of(2007, 5, 30).nn,
    start = LocalDate.of(2003, 9, 2).nn,
    title = "Diploma Student, Molecular Biology",
    school = "Univerity of Vienna",
    description = "",
    publications = List.empty
  )

  val htl = Education(
    end = LocalDate.of(2003, 7, 1).nn,
    start = LocalDate.of(1998, 9, 1).nn,
    title = "HTL, IT and Organization",
    school = "HTL Pinkafeld",
    description = "",
    publications = List.empty
  )

  val cv = CV(
    "Georg Ofenbeck",
    "",  
    "",
    LocalDate.of(1984, 6, 12).nn,
    List(
      SwisscomHealth,
      SwisscomDNA
    ),
    List(ethPhd, ethPhd0, ethMsc, tuBsc, vieBsc, htl)
  )

  println(s"Hello World, ${cv.toJson}")

  // path is a temporary directory in the temporary folder of the system
  import java.io.File
  import java.nio.file.Files
      import scala.language.unsafeNulls
  val directoryPath = "/tmp/cv"
  val directory = new File(directoryPath)

  if (directory.exists()) {
    import java.nio.file.Files

    Files.walk(directory.toPath())
      .sorted(java.util.Comparator.reverseOrder())
      .forEach(file => if(file != directory.toPath) Files.delete(file))

    Files.createDirectories(directory.toPath())
  } else {
    Files.createDirectories(directory.toPath())
  }

  CV2Git.createGitRepositoryWithCV(directoryPath, cv)
  // val path = java.nio.file.Files.createTempDirectory("temp").toString

  // CV2Git.createGitRepositoryWithCV(path, cv)

//   CV2Git.createGitRepositoryWithCV("/tmp/gitcv", cv)


  val xx = Publication(
    start = LocalDate.of(2022, 1, 1).nn,
    title = "title",
    description = "description",
    github = None,
    thesis = None,
    publication = "publication"
  )
   

  def test(x: CVItem): Unit={
    x match {
      case x: WorkExperince => println("WorkExperince")
      case x: Publication => println(x.publication)
      case x: Education => println("Education")
    }
  }
  test(xx)
}
