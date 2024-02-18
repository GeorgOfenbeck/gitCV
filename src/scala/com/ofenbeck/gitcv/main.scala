package com.ofenbeck.gitcv

import zio.json._
import com.ofenbeck.gitcv.CV
import com.ofenbeck.gitcv.WorkExperince
import java.time.LocalDate
import com.ofenbeck.gitcv.Project


object main extends App {

  def SwisscomHealth = {

    val medicalPracticeManagmentSoftware = Project(
        title = "Medical Practice Managment Software",
        description = "I was responsible for improving scalability and maintainability issues. The software is used by ~1000 of medical practices in Switzerland.",
        technologies = List(
            Technology("Kubernetes, AKS, Cilium", "Improving scalability and maintainability issues"),
            Technology("Azure managed services (Managed DB, Managed Storage Solutions)", "Migration from vm based solutions to managed services"),
        )
    )

    val ragnarok = Project(
        title = "Ragnarok",
        description = "Design and implementation of a cloud native serverless application for a medical data messing system.",
        technologies = List(
            Technology("Kotlin Spring Cloud, Azure Functions", "A serverless redesign of the existing classic java application"),
            Technology("", "Improving scalability and maintainability issues"),
        )
    )
    val devOps = Project(
        title = "DevOps",
        description = "Modernizing or creating new CI/CD pipelines for various products",
        technologies = List(
            Technology("Azure DevOps", "Migration from Jenkins to Azure DevOps"),
            Technology("Terraform", "Migration from ARM templates and ansible to Terraform"),
            Technology("Dev Container / Dev Setup", "Lowering the barrier for new developers to start working on the project and creating faster feedback loops"), 
        )
    )

    WorkExperince(
        end = LocalDate.of(2024, 4, 1).nn,
        start = LocalDate.of(2022, 2, 1).nn,
        title = "Senior Cloud Architect",
        company = "Swisscom Health",
        description = "Implementation of a big data pipeline for machine learning. Implementation of a high throughput streaming application.",
        projects = List(medicalPracticeManagmentSoftware)
    )
  }

  def SwisscomDNA = {
    WorkExperince(
        end = LocalDate.of(2022, 1, 31).nn,
        start = LocalDate.of(2018, 4, 3).nn,
        title = "Senior Software Engineer",
        company = "Data, Analytics und AI Department, Swisscom",
        description = "Implementation of a big data pipeline for machine learning. Implementation of a high throughput streaming application.",
        projects = List(
            Project(
                title = "Big Data Pipeline for Machine Learning",
                description = "I was responsible for the implementation of a big data pipeline for machine learning. The pipeline was used to train and deploy machine learning models.",
                technologies = List(
                    Technology("Kubernetes, AKS, Cilium", "Improving scalability and maintainability issues"),
                    Technology("Azure managed services (Managed DB, Managed Storage Solutions)", "Migration from vm based solutions to managed services"),
                )
            ),
            Project(
                title = "High Throughput Streaming Application",
                description = "I was responsible for the implementation of a high throughput streaming application. The application was used to process high throughput data streams.",
                technologies = List(
                    Technology("Kafka, Kafka Streams", "Implementation of a high throughput streaming application"),
                    Technology("Kubernetes, AKS, Cilium", "Improving scalability and maintainability issues"),
                )
            )
        )
    )
  }

  val start: LocalDate  = LocalDate.of(2022, 12, 31).nn
  val end: LocalDate = LocalDate.of(2024, 12, 31).nn
  val cv = CV("Georg Ofenbeck", "", "", 
  List(
    SwisscomHealth,
    SwisscomDNA
  ), List())
  
  println(s"Hello World, ${cv.toJson}")
}
