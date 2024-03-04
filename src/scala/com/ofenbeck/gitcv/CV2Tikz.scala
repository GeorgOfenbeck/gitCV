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

    val (graph, y) = delegateToBranch(cv, LocalDate.now(), TikzBranchConfig.branchMap, -3.0, "root", None)
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
      lastNode: String,
      parentNode: Option[String],
  ): (String, String) = {
    val (graph, prevNode): (String, String) = item match {
      case cv: CV => {
        // branchCVItems(date, cv.education, TikzBranchConfig.education)
        branchCVItems(date, cv.workExperince, TikzBranchConfig.workExperince, yOffset, lastNode, parentNode)
      }

      case work: WorkExperince => {
        branchCVItems(date, work.projects, TikzBranchConfig.projects, yOffset, lastNode,parentNode)
        //     git.merge().include(git.getRepository().resolve("projects")).call()
      }
      // case education: Education =>
      //   branchCVItems(date, education.publications, "publications", git, path)
      //   git.merge().include(git.getRepository().resolve("publications")).call()
      //   branchCVItems(date, education.teaching, "teaching", git, path)
      //   git.merge().include(git.getRepository().resolve("teaching")).call()
      case project: Project =>
        branchCVItems(date, project.technologies, TikzBranchConfig.technologies, yOffset, lastNode, parentNode)
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
    if (cvitems.isEmpty) return Vector.empty[(CVItem, LocalDate)]

    cvitems.head match {
      case tech: Technology => cvitems.foldLeft(Vector{(tech, tech.start)})((acc, ele) => Vector(tech.copy(title = tech.title + ",  " + ele.title) -> tech.start))
      case _ =>
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
  }
  def branchCVItems(
      branchdate: LocalDate,
      cvitems: List[CVItem],
      homeBranch: TikzBranch,
      yOffset: Double,
      lastNode: String,
      parentNode: Option[String],
  ): (String, String) = {
    import scala.language.unsafeNulls

    val sorted = sortCVItemsByDate(cvitems)

    val sb = new StringBuilder()
    val node_pos = 0
    val message_pos = 0
    var ypos = yOffset
    var prevNode: String = lastNode
    for ((item, date) <- sorted) {

      val hash: String = item.hashCode().toString().substring(1, 8)

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
      val nodeAtBranchLabel = s"${hash}branch" 
      val nodeAtBranch = s"\\node[commit] (${nodeAtBranchLabel}) at  ($hash -| ${homeBranch.xshift},0) {};\n"
      sb.append(text)
      sb.append(node)
      sb.append(path)
      sb.append(nodeAtBranch)
      
      parentNode.map{ parent =>
        val pathToParent = s"\\draw[-,${homeBranch.color}, line width=2pt] (${nodeAtBranchLabel}) to[out=90,in=-90] ($parent);\n"
        sb.append(pathToParent)
      }
      item match {
        case withend: WorkExperince =>
          import java.time.format.DateTimeFormatter

          sb.append(
            s"\\node[left = 0cm of ${hash}branch] (datestart$hash) {${withend.`end`.format(DateTimeFormatter.ofPattern("MM/YY"))}};\n"
          )
        case _ =>
      }

      prevNode = s"label_$hash"

      val (subgraph, subLast) = delegateToBranch(item, date, branchMap, yOffset, prevNode, Some(nodeAtBranchLabel))
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
}
