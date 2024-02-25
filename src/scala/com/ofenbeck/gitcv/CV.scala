package com.ofenbeck.gitcv

import java.time.LocalDate
import zio.json.JsonDecoder
import zio.json.DeriveJsonDecoder
import zio.json.JsonEncoder
import zio.json.DeriveJsonEncoder

trait CVItem {
  def start: LocalDate
  def title: String
  def description: String
}

trait CVItemWithEnd extends CVItem {
  def end: LocalDate
}

final case class Publication(
    start: LocalDate,
    title: String,
    description: String,
    github: Option[String],
    thesis: Option[String],
    publication: String
) extends CVItem

final case class WorkExperince(
    start: LocalDate,
    end: LocalDate,
    title: String,
    company: String,
    description: String,
    projects: List[Project]
) extends CVItemWithEnd

final case class Project(
    start: LocalDate,
    title: String,
    description: String,
    technologies: List[Technology]
) extends CVItem

final case class Technology(start: LocalDate, title: String, description: String) extends CVItem

final case class Education(
    start: LocalDate,
    end: LocalDate,
    title: String,
    school: String,
    description: String,
    publications: List[Publication]
) extends CVItemWithEnd


final case class CV(
    name: String,
    email: String,
    phone: String,
    birthdate: LocalDate,
    workExperince: List[WorkExperince],
    education: List[Education]
) extends CVItem {
  def start: LocalDate = birthdate
  def title: String = "CV"
  def description: String = "CV"
}

object CV {
  implicit val decoder: JsonDecoder[CV] = DeriveJsonDecoder.gen[CV]
  implicit val encoder: JsonEncoder[CV] = DeriveJsonEncoder.gen[CV]
}
object WorkExperince {
  implicit val decoder: JsonDecoder[WorkExperince] =
    DeriveJsonDecoder.gen[WorkExperince]
  implicit val encoder: JsonEncoder[WorkExperince] =
    DeriveJsonEncoder.gen[WorkExperince]
}
object Project {
  implicit val decoder: JsonDecoder[Project] = DeriveJsonDecoder.gen[Project]
  implicit val encoder: JsonEncoder[Project] = DeriveJsonEncoder.gen[Project]
}
object Technology {
  implicit val decoder: JsonDecoder[Technology] =
    DeriveJsonDecoder.gen[Technology]
  implicit val encoder: JsonEncoder[Technology] =
    DeriveJsonEncoder.gen[Technology]
}
object Education {
  implicit val decoder: JsonDecoder[Education] =
    DeriveJsonDecoder.gen[Education]
  implicit val encoder: JsonEncoder[Education] =
    DeriveJsonEncoder.gen[Education]
}
object Publication {
  implicit val decoder: JsonDecoder[Publication] =
    DeriveJsonDecoder.gen[Publication]
  implicit val encoder: JsonEncoder[Publication] =
    DeriveJsonEncoder.gen[Publication]
}
