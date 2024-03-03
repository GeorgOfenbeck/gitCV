package scala.com.ofenbeck.gitcv

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
      s"\\draw[->, $color] ($name) -- ($name.south |- 0,${-15});\n"
  }
}

object TikzBranchConfig {
  val educationColor = "blue"
  val workExperinceColor = "green"
  val publicationsColor = "red"
  val teachingColor = "yellow"
  val projectsColor = "orange"
  val technologiesColor = "purple"

  val branchXPos = 0.0
  val branchYPos = 0.0

  val titleYOffset = 0.5
  val branchOffset = 0.5

  val branches = Vector(
    ("Work Experince", workExperinceColor),
    ("Education", educationColor),
    ("Publications", publicationsColor),
    ("Teaching", teachingColor),
    ("Projects", projectsColor),
    ("Technologies", technologiesColor)
  )

  val branchesWithOffset = branches.zipWithIndex.map { case ((name, color), index) =>
    new TikzBranch(name, color, branchXPos + index * branchOffset, branchYPos) // + index * titleYOffset)
  }

  val branchMap = branchesWithOffset.map(b => (b.name, b)).toMap

  val education = branchMap.get("Education").get
  val workExperince = branchMap.get("Work Experince").get
  val publications = branchMap.get("Publications").get
  val teaching = branchMap.get("Teaching").get
  val projects = branchMap.get("Projects").get
  val technologies = branchMap.get("Technologies").get

}
