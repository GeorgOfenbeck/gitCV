package com.ofenbeck.gitcv

import java.time.LocalDate
import zio.json.JsonDecoder
import zio.json.DeriveJsonDecoder
import zio.json.JsonEncoder
import zio.json.DeriveJsonEncoder




final case class WorkExperince(start: LocalDate, end: LocalDate, title: String, company: String, description: String, projects: List[Project]) 

final case class Project(title: String, description: String, technologies: List[Technology])
final case class Technology(name: String, description: String)



final case class Education(start: LocalDate, end: LocalDate, title: String, school: String, description: String)
final case class CV(
    name: String,
    email: String,
    phone: String,
    workExperince: List[WorkExperince],
    education: List[Education]
)

object CV {
  implicit val decoder: JsonDecoder[CV] = DeriveJsonDecoder.gen[CV]
  implicit val encoder: JsonEncoder[CV] = DeriveJsonEncoder.gen[CV]
}
object WorkExperince {
  implicit val decoder: JsonDecoder[WorkExperince] = DeriveJsonDecoder.gen[WorkExperince]
  implicit val encoder: JsonEncoder[WorkExperince] = DeriveJsonEncoder.gen[WorkExperince]
}   
object Project {
  implicit val decoder: JsonDecoder[Project] = DeriveJsonDecoder.gen[Project]
  implicit val encoder: JsonEncoder[Project] = DeriveJsonEncoder.gen[Project]
}
object Technology {
  implicit val decoder: JsonDecoder[Technology] = DeriveJsonDecoder.gen[Technology]
  implicit val encoder: JsonEncoder[Technology] = DeriveJsonEncoder.gen[Technology]
}
object Education {
  implicit val decoder: JsonDecoder[Education] = DeriveJsonDecoder.gen[Education]
  implicit val encoder: JsonEncoder[Education] = DeriveJsonEncoder.gen[Education]
}