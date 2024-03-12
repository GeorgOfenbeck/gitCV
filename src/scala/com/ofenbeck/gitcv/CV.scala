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

final case class Social(
    start: LocalDate,
    title: String,
    description: String = "",
) extends CVItem


final case class Teaching(
    start: LocalDate,
    title: String,
    description: String
) extends CVItem

final case class Publication(
    start: LocalDate,
    title: String,
    description: String,
    github: Option[String],
    thesis: Option[String],
    publication: String
) extends CVItem

final case class WorkExperience(
    start: LocalDate,
    end: LocalDate,
    title: String,
    company: String,
    description: String,
    projects: List[Project],
    socials: List[Social],
) extends CVItemWithEnd

final case class Project(
    start: LocalDate,
    end: LocalDate,
    title: String,
    description: String,
    technologies: List[Technology]
) extends CVItemWithEnd

final case class Technology(start: LocalDate, title: String, description: String) extends CVItem

final case class Education(
    start: LocalDate,
    end: LocalDate,
    title: String,
    school: String,
    description: String,
    publications: List[Publication],
    teaching: List[Teaching] = List.empty,
    socials: List[Social] = List.empty
) extends CVItemWithEnd


final case class CV(
    name: String,
    email: String,
    phone: String,
    birthdate: LocalDate,
    workExperience: List[WorkExperience],
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
object WorkExperience {
  implicit val decoder: JsonDecoder[WorkExperience] =
    DeriveJsonDecoder.gen[WorkExperience]
  implicit val encoder: JsonEncoder[WorkExperience] =
    DeriveJsonEncoder.gen[WorkExperience]
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
object Teaching {
  implicit val decoder: JsonDecoder[Teaching] = DeriveJsonDecoder.gen[Teaching]
  implicit val encoder: JsonEncoder[Teaching] = DeriveJsonEncoder.gen[Teaching]
}
object Social {
  implicit val decoder: JsonDecoder[Social] = DeriveJsonDecoder.gen[Social]
  implicit val encoder: JsonEncoder[Social] = DeriveJsonEncoder.gen[Social]
}
