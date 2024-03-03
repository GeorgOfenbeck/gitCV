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
import scala.com.ofenbeck.gitcv.TikzBranchConfig.branchMap
import com.ofenbeck.gitcv.Technology

object CV2Tikz {

  // |\lipsum[1]
  def craeteTex(cv: CV): String = {
    val headers =
      """\documentclass{article}
        |\usepackage{tikz}
        |\usepackage{xcolor}
        |\usetikzlibrary{shapes,arrows,backgrounds,positioning}
        |\usetikzlibrary{calc}
        |\usepackage{lipsum}
        |\usepackage[dvipsnames]{xcolor}
        |\usepackage{listings}
        |\usepackage{geometry}
        |\geometry{
        |left=1cm,
        |right=1cm,
        |top=1cm,
        |bottom=0cm,
        |includehead,
        |includefoot
        |}
        |\begin{document}
        |\lipsum[1]
        |\begin{tikzpicture}[framed,background rectangle/.style={double,ultra thick,draw=red, top color=white, rounded corners}]
        |\tikzstyle{commit}=[draw,circle,fill=white,inner sep=0pt,minimum size=5pt]
        |\tikzstyle{every path}=[draw]  
        |\tikzstyle{branch}=[draw,rectangle,rounded corners=3,fill=white,inner sep=2pt,minimum size=5pt]
        |
        |\draw (0,0) -- (1,1);
        |\node[commit] (root) at (0,0) {};"
        |""".stripMargin
        // |\draw[help lines] (-5,0) grid (10,-10);
        // |\resizebox{\columnwidth}{!}{%
    val tail = """
        |\end{tikzpicture}%
        |\end{document}\n""".stripMargin

    val (graph, y) = delegateToBranch(cv, LocalDate.now(), TikzBranchConfig.branchMap, -3.0, "root")
    headers +
      createBranches() +
      graph +
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
      date: LocalDate,
      branchMap: Map[String, TikzBranch],
      yOffset: Double,
      lastNode: String
  ): (String, String) = {
    val (graph, prevNode): (String, String) = item match {
      case cv: CV => {
        // branchCVItems(date, cv.education, TikzBranchConfig.education)
        branchCVItems(date, cv.workExperince, TikzBranchConfig.workExperince, yOffset, lastNode)
      }

      case work: WorkExperince => {
        branchCVItems(date, work.projects, TikzBranchConfig.projects, yOffset, lastNode)
        //     git.merge().include(git.getRepository().resolve("projects")).call()
      }
      // case education: Education =>
      //   branchCVItems(date, education.publications, "publications", git, path)
      //   git.merge().include(git.getRepository().resolve("publications")).call()
      //   branchCVItems(date, education.teaching, "teaching", git, path)
      //   git.merge().include(git.getRepository().resolve("teaching")).call()
      case project: Project =>
        branchCVItems(date, project.technologies, TikzBranchConfig.technologies, yOffset, lastNode)
      case _ => ("", lastNode)

    }
    (graph, prevNode)
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
    return cronCVIems.sortBy(_._2).reverse
  }
  def branchCVItems(
      branchdate: LocalDate,
      cvitems: List[CVItem],
      homeBranch: TikzBranch,
      yOffset: Double,
      lastNode: String
  ): (String, String) = {
    import scala.language.unsafeNulls

    val sorted = sortCVItemsByDate(cvitems)

    val sb = new StringBuilder()
    val node_pos = 0
    val message_pos = 0
    var ypos = yOffset
    var prevNode: String = lastNode
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

      val hash: String = item.hashCode().toString().substring(1, 8)

      // val node = s"\\node[commit, below=1cm of ${lastNode}] ($hash) at (${0.5 * node_pos},$ypos) {};\n"
      val allinpos =
        if (prevNode == "root") s"below right =1cm and 0cm of ${prevNode}"
        else s"below = 0.2cm of ${prevNode}.south"

      val text = item match {
        case tech: Technology =>
          s"""|\\node[ text width=15cm, $allinpos ] (label_$hash)  
              |{${item.title}};""".stripMargin
        case _ =>
          s"""
            |\\node[ text width=15cm, $allinpos ] (label_$hash)  
            |{${item.title}\\\\
            |${item.description}};\n"
            """.stripMargin
      }
      val node = s"\\node[commit, left=0.1cm of label_$hash] ($hash)  {};\n"
      val path = s"\\draw[-,${homeBranch.color}, line width=2pt] ($hash -| ${homeBranch.xshift} ,0) -- ($hash);\n"
      val nodeAtBranch = s"\\node[commit] (${hash}branch) at  ($hash -| ${homeBranch.xshift},0) {};\n"

      sb.append(text)
      sb.append(node)
      sb.append(path)
      sb.append(nodeAtBranch)

      item match {
        case withend: WorkExperince =>
          import java.time.format.DateTimeFormatter

          sb.append(
            s"\\node[left = 0cm of ${hash}branch] (datestart$hash) {${withend.`end`.format(DateTimeFormatter.ofPattern("MM/YY"))}};\n"
          )
        case _ =>
      }

      // sb.append(s"\\draw[-,${homeBranch.color}, line width=2pt] (label_$hash.west) -- ($prevNode.west);\n")
      prevNode = s"label_$hash"

      val (subgraph, subLast) = delegateToBranch(item, date, branchMap, yOffset, prevNode)
      sb.append(subgraph)
      prevNode = subLast

      item match {
        case withend: WorkExperince => // CVItemWithEnd =>
          sb.append(s"\\node[commit] (dateend${hash}) at  ($$(${prevNode} -|  ${hash}branch)$$) {};\n")
          sb.append(s"\\draw[-,${homeBranch.color}, line width=2pt] (dateend$hash) to[out=90,in=-90] ($hash);\n")
        case _ =>
      }
    }
    return (sb.toString(), prevNode)
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
