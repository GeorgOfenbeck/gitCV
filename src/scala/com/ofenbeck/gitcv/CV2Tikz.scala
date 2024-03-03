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

object CV2Tikz {

 

  def craeteTex(cv: CV): String = {
    val headers =
      """\documentclass{article}
        |\usepackage{tikz}
        |\usepackage{xcolor}
        |\usetikzlibrary{shapes,arrows}
        |\usetikzlibrary{calc}
        |\usepackage[dvipsnames]{xcolor}
        |\usepackage{listings}
        |\begin{document}
        |\begin{tikzpicture}
        |\tikzstyle{commit}=[draw,circle,fill=white,inner sep=0pt,minimum size=5pt]
        |\tikzstyle{every path}=[draw]  
        |\tikzstyle{branch}=[draw,rectangle,rounded corners=3,fill=white,inner sep=2pt,minimum size=5pt]
        |""".stripMargin

    val tail = """
  \end{tikzpicture}\end{document}\n"""

    val graph = delegateToBranch(cv, LocalDate.now())
    headers +
      createBranches() +
      // graph +
      tail

  }

  def createBranches(): String = {
    val branches = TikzBranchConfig.branchesWithOffset
    val sb = new StringBuilder()
    for (branch <- branches) {
      sb.append(branch.branch)
    }
    sb.toString()
  }

  /** delegateToBranch delegates the CVItems of a CV to the git repository
    *
    * @param item
    * @param date
    */
  def delegateToBranch(
      item: CVItem,
      date: LocalDate
  ): String = {
    val graph: String = item match {
      case cv: CV => {
        branchCVItems(date, cv.education, "education")
        branchCVItems(date, cv.workExperince, "workExperince")
      }

      // case work: WorkExperince => {
      //   branchCVItems(date, work.projects, "projects", git, path)
      //   git.merge().include(git.getRepository().resolve("projects")).call()
      // }
      // case education: Education =>
      //   branchCVItems(date, education.publications, "publications", git, path)
      //   git.merge().include(git.getRepository().resolve("publications")).call()
      //   branchCVItems(date, education.teaching, "teaching", git, path)
      //   git.merge().include(git.getRepository().resolve("teaching")).call()
      // case project: Project =>
      //   branchCVItems(date, project.technologies, "technologies", git, path)
      //   git.merge().include(git.getRepository().resolve("technologies")).call()
      case _ => ""

    }
    graph
  }

  /** sortCVItemsByDate sorts the CVItems of a CV by date
    *
    * @param cvitems
    * @return
    */
  def sortCVItemsByDate(cvitems: List[CVItem]): Vector[(CVItem, LocalDate)] = {
    val cronCVIems =
      cvitems.foldLeft(Vector.empty[(CVItem, LocalDate)])((acc, e) => {
        val start = e.start
        e match {
          // case withend: CVItemWithEnd =>
          //   acc :+ (withend -> start) :+ (withend -> withend.end)
          case _ => acc :+ (e -> start)
        }
      })
    return cronCVIems.sortBy(_._2)
  }
  def branchCVItems(
      branchdate: LocalDate,
      cvitems: List[CVItem],
      homeBranch: String
  ): String = {
    import scala.language.unsafeNulls

    val sorted = sortCVItemsByDate(cvitems)

    val sb = new StringBuilder()
    val node_pos = 0
    val message_pos = 0
    var ypos = 0.0
    for ((item, date) <- sorted) {
      // go to home branch

      // create a branch for the item

      // if (item.start == date) delegateToBranch(item, date  )
      // // go back to home branch and merge the subbranch
      // item match {
      //   case withend: CVItemWithEnd =>
      //     if (date == withend.end) {
      //     }
      //   case _: CVItem =>
      // }
      val hash = item.hashCode()
      val node = s"\\node[commit] ($hash) at (${0.5 * node_pos},$ypos) {};\n"
      val text = s"""
        |\\node[right,xshift=${message_pos * 0.5}, text width=10cm] (label_$hash) at ($hash.east) 
        |{${item.title}
        |${item.description}};"
        """.stripMargin
      sb.append(node)
      sb.append(text)
      ypos = ypos + 3
    }
    return sb.toString()
  }
  /*
  def build(line: String): Boolean = {
    var pos = 0
    val words = line.split(" ")
    var hash: Option[String] = None
    var parents: Map[String, Any] = Map.empty
    var nodePos: Option[Int] = None
    var messagePos: Option[Int] = None
    var message = ""

    for (word <- words) {
      word match {
        case sha if sha.matches("[a-f0-9]{7}") && message.isEmpty =>
          if (hash.isEmpty) {
            hash = Some(sha)
          } else {
            parents += (sha -> None)
          }
        case "*" =>
          nodePos = Some(pos)
        case word if word.matches("[^|/\\\\]") =>
          messagePos = Some(pos)
          message += s" $word"
        case _ =>
      }
      pos += 1
    }

    message = message.replace("!", "").trim

    if (hash.isEmpty) {
      false
    } else {
      true
    }
  }

  def export_to_tikz(): Unit = {
    println("\\begin{tikzpicture}")
    var ypos = 0.0
    val ystep = -0.5
    commits.values.foreach { commit =>
      commit.export_to_tikz(ypos)
      ypos += ystep
    }
    branches.foreach { branch =>
      println(
        s"\\node[branch,right,xshift=.1] (${branch.name}) at (label_${branch.hash}.east) {\\texttt{${branch.name}}};"
      )
    }
    println("\\end{tikzpicture}")
  }

  def export_to_tikz(ypos: Double): Unit = {
    println(s"\\node[commit] ($hash) at (${0.5 * node_pos},$ypos) {};")
    println(s"\\node[right,xshift=${message_pos * 0.5}] (label_$hash) at ($hash.east) {\\verb!$hash $message!};")
    children.values.foreach { child =>
      println(s"\\draw[->] ($hash) -- (${child.hash});")
    }
  }*/
}
