package com.ofenbeck.gitcv

import java.io.File
import org.eclipse.jgit.api.Git
import org.eclipse.jgit.lib.Repository
import org.eclipse.jgit.storage.file.FileRepositoryBuilder
import com.ofenbeck.gitcv.CV
import com.ofenbeck.gitcv.Main.cv

import scala.language.unsafeNulls
import zio.json.EncoderOps
import java.time.LocalDate
import java.time.ZoneOffset
import java.util.TimeZone
import java.time.Instant
import java.util.UUID
import java.nio.file.Files
import com.ofenbeck.gitcv.CVItem
import com.ofenbeck.gitcv.CVItemWithEnd
import com.ofenbeck.gitcv.WorkExperince
import com.ofenbeck.gitcv.Education
import com.ofenbeck.gitcv.Project

class TikzBranch(val name: String, val color: String,  val xshift: Double,val yOffset: Double) {
  def branch: String = {
    s"\\node[branch, fill=$color, rotate=90] ($name) at ($xshift,$yOffset) {\\texttt{$name}};\n" +
      s"\\draw[-, $color, line width=2pt] ($name) -- ($name |- 0,${-19});\n"
  }
}

case class TikzBranchConfig(branchXPos: Double , branchYPos: Double, titleYOffset: Double, branchOffset: Double, branches: Vector[(String, String)]) 
{
 
  val branchesWithOffset = branches.zipWithIndex.map { case ((name, color), index) =>
    new TikzBranch(name, color, branchXPos + index * branchOffset, branchYPos) // + index * titleYOffset)
  }

  val branchMap = branchesWithOffset.map(b => (b.name, b)).toMap

}
