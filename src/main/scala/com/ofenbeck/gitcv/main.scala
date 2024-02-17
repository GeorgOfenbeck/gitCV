package com.ofenbeck.gitcv
import zio.json._
import _root_.main.scala.com.ofenbeck.gitcv.CV
import _root_.main.scala.com.ofenbeck.gitcv.WorkExperince
import java.time.LocalDate


object main extends App {
  val start: LocalDate  = LocalDate.of(2022, 12, 31).nn
  val end: LocalDate = LocalDate.of(2024, 12, 31).nn
  val cv = CV("Georg Ofenbeck", "", "", 
  List(
    WorkExperince(
      end = end,
      start = start, 
      title = "Senior Researcher",
      company = "IBM Research",
      description = "Research in AI and ML"
  )
  ), List())
  
  println(s"Hello World, ${cv.toJson}")
}
