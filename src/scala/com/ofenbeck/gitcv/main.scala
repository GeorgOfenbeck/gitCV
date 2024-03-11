package com.ofenbeck.gitcv

import zio.json._
import com.ofenbeck.gitcv.CV
import com.ofenbeck.gitcv.WorkExperince
import java.time.LocalDate
import com.ofenbeck.gitcv.Project
import scala.com.ofenbeck.gitcv.CV2Git
import scala.com.ofenbeck.gitcv.CV2Tikz

object Main extends App {

  def SwisscomHealth = {

    val startDateTime = LocalDate.of(2022, 2, 1).nn
    val endDateTime = LocalDate.of(2024, 10, 3).nn

    val medicalPracticeManagmentSoftware = Project(
      start = startDateTime.plusDays(1).nn,
      end = endDateTime.minusDays(1).nn,
      title = "Medical Practice Managment Software",
      description =
        "I was responsible for improving scalability and maintainability issues. The software is used by ~1000 of medical practices in Switzerland.",
      technologies = List(
        Technology(
          start = startDateTime,
          "Kubernetes (plain), AKS",
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
      start = startDateTime.plusDays(1).nn,
      end = endDateTime.minusDays(1).nn,
      title = "Confidential Medical Data Messing System",
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
          "Azure managed storage solutions",
          "Using managed blob, table and queue storage for the serverless application"
        ),
        Technology(start = startDateTime, "Open Telemetry", "Implementation of distributed tracing")
      )
    )
    val devOps = Project(
      start = startDateTime.plusDays(1).nn,
      end = endDateTime.minusDays(1).nn,
      title = "DevOps",
      description = "Modernizing or creating new CI/CD pipelines for various products",
      technologies = List(
        Technology(start = startDateTime, "Azure DevOps", "Migration from Jenkins to Azure DevOps"),
        Technology(
          start = startDateTime,
          "Terraform",
          "Migration from ARM templates and Ansible to Terraform"
        ),
        Technology(
          start = startDateTime,
          "Dev Container / Dev Setup",
          "Lowering the barrier for new developers to start working on the project and creating faster feedback loops"
        )
      )
    )

    val viennaTrip = Social(
      start = startDateTime.plusDays(1).nn,
      title = "Team Vienna Trip",
      description = "Organizied and guided a team trip to Vienna"
    )
    val pokerEvenings = Social(
      start = startDateTime.plusDays(1).nn,
      title = "Company Poker Evenings",
      description = "Organizied multiple poker evenings for the whole health department"
    )

    val beerTasting = Social(
      start = startDateTime.plusDays(1).nn,
      title = "Company Beer Tasting",
      description = "Organizied a beer tasting event for the whole health department"
    )


    val boatTrips = Social(
      start = startDateTime.plusDays(1).nn,
      title = "Limmat Boetln",
      description = ""
    )

    val climbing = Social(
      start = startDateTime.plusDays(1).nn,
      title = "Climbing",
      description = "I climb regularly with my some of my colleagues"
    )

    val kitsurf = Social(
      start = startDateTime.plusDays(1).nn,
      title = "Kitsurfing",
      description = "I am a passionate kitsurfer and regulary go with one of my work colleagues"
    )

    WorkExperince(
      end = endDateTime,
      start = startDateTime,
      title = "Senior Cloud Architect",
      company = "Swisscom Health",
      description =
        "Various projects in the context of a medical practice managment software. Design and implementation of a cloud native serverless application for a medical data messing system. Modernizing or creating new CI/CD pipelines for various products.",
      projects = List(
        medicalPracticeManagmentSoftware,
        devOps,
        ragnarok
      ),
      socials = List(viennaTrip, pokerEvenings, beerTasting, boatTrips, climbing, kitsurf)
    )
  }

  def SwisscomDNA = {
    val startDateTime = LocalDate.of(2018, 4, 3).nn
    val endDateTime = LocalDate.of(2022, 1, 31).nn
    WorkExperince(
      end = endDateTime,
      start = startDateTime,
      title = "Senior Software Engineer",
      company = "Data, Analytics und AI Department, Swisscom",
      description =
        "Implementation of a big data pipeline for machine learning. Implementation of a high throughput streaming application.",
      projects = List(
        Project(
          start = startDateTime.plusDays(1).nn,
          end = endDateTime.minusDays(1).nn,
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
          start = startDateTime.plusDays(1).nn,
          end = endDateTime.minusDays(1).nn,
          title = "High Throughput Streaming Application",
          description =
            "Implementation and operation of a real-time streaming application processing 6k msgs/s triggering interactions with customers.",
          technologies = List(
            Technology(start = startDateTime, "Kafka, Akka Streaming, Flink", "")
          )
        )
      ),
      socials = List(
        Social(
          start = startDateTime.plusDays(1).nn,
          title = "Poker Evenings",
          description = "Organizied multiple poker evenings for the team"
        ),
        Social(
          start = startDateTime.plusDays(1).nn,
          title = "Cinema Evenings",
          description = "Regularly organized cinema evenings with some teammates"
        )),
    )
  }

  val thesis = Publication(
    start = LocalDate.of(2017, 4, 1).nn,
    title = "Generic Programming Applied on the Time Dimension of Meta Programming",
    description =
      "How to apply generic programming techniques to challenges in metaprogramming in the context of high performance code generators.",
    github = Some("https://github.com/GeorgOfenbeck/SpaceTime"),
    thesis = Some(
      "https://drive.google.com/file/d/0B9SH4AFkecQFMnkzWi1IRGprSFE/view?usp=sharing&resourcekey=0-LezbiD5TLA2KMTDOYifYvg"
    ),
    publication = "https://www.research-collection.ethz.ch/handle/20.500.11850/271073"
  )

  val randomTesting = Publication(
    start = LocalDate.of(2016, 4, 1).nn,
    title = "Random Testing for Compilers",
    description =
      "A tool for differential testing of compilers using random instances of a given intermediate representation.",
    github = Some("https://github.com/GeorgOfenbeck/virtualization-lms-core/tree/buttom_up/src/test/test"),
    thesis = None,
    publication = "https://www.cs.purdue.edu/homes/rompf/papers/ofenbeck-scala16.pdf"
  )

  val genVector = Publication(
    start = LocalDate.of(2015, 4, 1).nn,
    title = "Generating Vector Instructions",
    description =
      "A demonstration on how to provide modular and extensible support for modern SIMD vector architectures in a DSL-based generator.",
    github = None,
    thesis = None,
    publication = "https://spiral.ece.cmu.edu/pub-spiral/pubfile/paper_179.pdf"
  )

  val roofLine = Publication(
    start = LocalDate.of(2014, 4, 1).nn,
    title = "Applying the Roofline Model",
    description = "We show how to produce roofline plots with measured data on recent generations of Intel platforms.",
    github = Some("https://github.com/GeorgOfenbeck/perfplot"),
    thesis = None,
    publication = "https://spiral.ece.cmu.edu/pub-spiral/pubfile/ispass-2013_177.pdf"
  )

  val masterthesis = Publication(
    start = LocalDate.of(2011, 4, 1).nn,
    title = "High Performance Distributed Bio-Inspired Optimization Library",
    description = "A parallel software library that implements the Evolution Strategy with Co-variance Matrix Adaptation (CMAES) using MPI for efficient parallelization.",
    github = Some("https://git.mpi-cbg.de/mosaic/software/black-box-optimization/libpcma/-/tree/master"),
    thesis = None,
    publication = "https://sbalzarini-lab.org//docs/Mueller2009b.pdf"
  )

  val fastcode17 = Teaching(
    start = LocalDate.of(2017, 4, 1).nn,
    title = "How To Write Fast Numerical Code",
    description =
      "This interdisciplinary course aims to give the student an understanding of performance and introduces foundations and state-of-the-art techniques in high performance software development. A general strategy for performance analysis and optimization is introduced that the students will apply in group projects that accompany the course. Supervised a wide range of projects including projects from the domain of machine learning, computer vision, financial modeling etc."
  )

  val fastcode16 = Teaching(
    start = LocalDate.of(2016, 4, 1).nn,
    title = "How To Write Fast Numerical Code",
    description =
      "This interdisciplinary course aims to give the student an understanding of performance and introduces foundations and state-of-the-art techniques in high performance software development. A general strategy for performance analysis and optimization is introduced that the students will apply in group projects that accompany the course. Supervised a wide range of projects including projects from the domain of machine learning, computer vision, financial modeling etc."
  )

  val fastcode14 = Teaching(
    start = LocalDate.of(2014, 4, 1).nn,
    title = "How To Write Fast Numerical Code",
    description =
      "This interdisciplinary course aims to give the student an understanding of performance and introduces foundations and state-of-the-art techniques in high performance software development. A general strategy for performance analysis and optimization is introduced that the students will apply in group projects that accompany the course. Supervised a wide range of projects including projects from the domain of machine learning, computer vision, financial modeling etc."
  )
  val fastcode13 = Teaching(
    start = LocalDate.of(2013, 4, 1).nn,
    title = "How To Write Fast Numerical Code",
    description =
      "This interdisciplinary course aims to give the student an understanding of performance and introduces foundations and state-of-the-art techniques in high performance software development. A general strategy for performance analysis and optimization is introduced that the students will apply in group projects that accompany the course. Supervised a wide range of projects including projects from the domain of machine learning, computer vision, financial modeling etc."
  )
  val fastcode12 = Teaching(
    start = LocalDate.of(2012, 4, 1).nn,
    title = "How To Write Fast Numerical Code",
    description =
      "This interdisciplinary course aims to give the student an understanding of performance and introduces foundations and state-of-the-art techniques in high performance software development. A general strategy for performance analysis and optimization is introduced that the students will apply in group projects that accompany the course. Supervised a wide range of projects including projects from the domain of machine learning, computer vision, financial modeling etc."
  )

   val fastcode11 = Teaching(
     start = LocalDate.of(2011, 4, 1).nn,
     title = "How To Write Fast Numerical Code",
     description =
       "This interdisciplinary course aims to give the student an understanding of performance and introduces foundations and state-of-the-art techniques in high performance software development. A general strategy for performance analysis and optimization is introduced that the students will apply in group projects that accompany the course. Supervised a wide range of projects including projects from the domain of machine learning, computer vision, financial modeling etc."
   )

  val introprogramming = Teaching(
    start = LocalDate.of(2016, 4, 1).nn,
    title = "Introduction to Programming",
    description = ""
  )

  val compscienc2 = Teaching(
    start = LocalDate.of(2015, 4, 1).nn,
    title = "Computer Science 2",
    description = ""
  )
  val parralelProgramming2013 = Teaching(
    start = LocalDate.of(2013, 4, 1).nn,
    title = "Parallel Programming",
    description = ""
  )
  val foundationsCompScience2015 = Teaching(
    start = LocalDate.of(2015, 4, 1).nn,
    title = "Foundations of Computer Science",
    description = ""
  )

  val foundationsCompScience2014 = Teaching(
    start = LocalDate.of(2014, 4, 1).nn,
    title = "Foundations of Computer Science",
    description = ""
  )

  val parralelProgramming2015 = Teaching(
    start = LocalDate.of(2015, 4, 1).nn,
    title = "Parallel Programming",
    description = ""
  )

  val compScience2012 = Teaching(
    start = LocalDate.of(2012, 4, 1).nn,
    title = "Computer Science",
    description = ""
  )

  val compScience2013 = Teaching(
    start = LocalDate.of(2013, 4, 1).nn,
    title = "Computer Science",
    description = ""
  )

  val ethPhd = Education(
    end = LocalDate.of(2017, 5, 1).nn,
    start = LocalDate.of(2011, 10, 1).nn,
    title = "PhD, Computer Science",
    school = "ETH Zurich",
    description =
      "I worked on the topic of appling generic programming techniques to challenges in metaprogramming in the context of high performance code generators.",
    publications = List(thesis, randomTesting, genVector, roofLine),
    teaching = List(
      fastcode17,
      fastcode16,
      fastcode14,
      fastcode13,
      fastcode12,
      introprogramming,
      compscienc2,
      parralelProgramming2013,
      foundationsCompScience2015,
      foundationsCompScience2014,
      parralelProgramming2015,
      compScience2012,
      compScience2013
    ),
    socials = List(
      Social(
        start = LocalDate.of(2016, 4, 1).nn,
        title = "SOLA Stafette",
        description = "Every year I captained a team of 14 runners in the SOLA Stafette"
      ),
      Social(
        start = LocalDate.of(2015, 4, 1).nn,
        title = "Team BBQs",
        description = "Organizied many BBQs with the team"
      ),
      Social(
        start = LocalDate.of(2012, 4, 1).nn,
        title = "Poker Evenings",
        description = "Organizied multiple poker evenings for the team"
      ),
    )
  )
  val ethPhd0 = Education(
    end = LocalDate.of(2011, 9, 30).nn,
    start = LocalDate.of(2010, 8, 1).nn,
    title = "PhD student, Computer Science",
    school = "ETH Zurich",
    description =
      "Before switching topic, I worked my first year on enabling micro transaction support in the kernel of an OS",
    publications = List.empty,
    teaching = List(fastcode11)
  )
  val ethMsc = Education(
    end = LocalDate.of(2009, 11, 1).nn,
    start = LocalDate.of(2007, 9, 1).nn,
    title = "MSc, Computational Biology and Bioinformatics",
    school = "ETH Zurich",
    description = "",
    publications = List(masterthesis)
  )

  val tuBsc = Education(
    end = LocalDate.of(2007, 6, 1).nn,
    start = LocalDate.of(2003, 9, 2).nn,
    title = "BSc, Medical Informatics",
    school = "Technical University Vienna",
    description = "",
    publications = List.empty
  )

  val vieBsc = Education(
    end = LocalDate.of(2007, 6, 2).nn,
    start = LocalDate.of(2003, 9, 1).nn,
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

    Files
      .walk(directory.toPath())
      .sorted(java.util.Comparator.reverseOrder())
      .forEach(file => if (file != directory.toPath) Files.delete(file))

    Files.createDirectories(directory.toPath())
  } else {
    Files.createDirectories(directory.toPath())
  }

  CV2Git.createGitRepositoryWithCV(directoryPath, cv)

  val tex = new File( "./cv.tex")
  val content = CV2Tikz.craeteTex(cv)
  Files.write(tex.toPath(), content.getBytes())
  // val path = java.nio.file.Files.createTempDirectory("temp").toString

  // CV2Git.createGitRepositoryWithCV(path, cv)

   //CV2Git.createGitRepositoryWithCV("/tmp/gitcv", cv)

}
