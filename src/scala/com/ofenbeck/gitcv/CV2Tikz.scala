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
import com.ofenbeck.gitcv.Technology
import com.ofenbeck.gitcv.TikzBranchConfig
import com.ofenbeck.gitcv.TikzBranch

import java.time.format.DateTimeFormatter
import com.ofenbeck.gitcv.Teaching
import zio.Config.Bool

object CV2Tikz {

  val workExperinceName = "Work Experince"
  val educationName = "Education"
  val projectsName = "Projects"
  val technologiesName = "Technologies"
  val publicationsName = "Publications"
  val teachingName = "Teaching"
  val socialName = "Social"

  val educationColor = "blue"
  val workExperinceColor = "babyblue"
  val publicationsColor = "red"
  val teachingColor = "yellow"
  val projectsColor = "green"
  val technologiesColor = "beaublue"
  val socialColor = "orange"

  val drawboxes = "draw, "

  val textBoxWidth = 16
  val xshift = 0.2

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
        |""".stripMargin
        // |\draw[help lines] (-5,0) grid (10,-10);
        // |\resizebox{\columnwidth}{!}{%
    val tail = """
        |\end{document}\n""".stripMargin

    val (graph1, y) = delegateToBranch(cv, LocalDate.now(), workBranches().branchMap, 0.0, "root", None, 1)
    val (graph2, y2) = delegateToBranch(cv, LocalDate.now(), educationBranches().branchMap, 0.0, "root", None, 1)
    val workPicture: String = insertTikzSurrounding(createBranches(workBranches()) + graph1)

    val educationPicture: String = insertTikzSurrounding(createBranches(educationBranches()) + graph2)

    s"""
        |$headers
        |$workPicture
        |$educationPicture 
        |$tail """.stripMargin
  }

  def insertTikzSurrounding(content: String): String = {
    """
        |\begin{tikzpicture}[framed,background rectangle/.style={double,ultra thick,draw=red, top color=white, rounded corners}]
        |
        |\definecolor{babyblue}{rgb}{0.54, 0.81, 0.94}
        |\definecolor{babyblueeyes}{rgb}{0.63, 0.79, 0.95}
        |\definecolor{beaublue}{rgb}{0.74, 0.83, 0.9}
        |\tikzstyle{commit}=[draw,circle,fill=white,inner sep=0pt,minimum size=5pt]
        |\tikzstyle{inv}=[draw,circle,fill=white,inner sep=0pt,minimum size=2pt]
        |\tikzstyle{every path}=[draw]  
        |\tikzstyle{branch}=[draw,rectangle,rounded corners=3,fill=white,inner sep=2pt,minimum size=5pt]
        |
        |\draw (0,0) -- (1,1);
        |\node[commit] (root) at (0,0) {};"
        """.stripMargin + content + """
        |\end{tikzpicture}%""".stripMargin
  }

  def workBranches(): TikzBranchConfig = {

    val branchXPos = -2
    val branchYPos = 0.0

    val titleYOffset = 0.5
    val branchOffset = 0.5

    val branchLength = -16.5

    val branches = Vector(
      (workExperinceName, workExperinceColor),
      // ("Publications", publicationsColor),
      // ("Teaching", teachingColor),
      (projectsName, projectsColor),
      (technologiesName, technologiesColor),
      (socialName, socialColor),
      // (educationName, educationColor),
    )
    return TikzBranchConfig(branchXPos, branchYPos, titleYOffset, branchOffset, branches, branchLength)
  }

  def educationBranches(): TikzBranchConfig = {
    val branchXPos = -2
    val branchYPos = 0.0

    val titleYOffset = 0.5
    val branchOffset = 0.5

    val branchLength = -18.0

    val branches = Vector(
      (educationName, educationColor),
      (teachingName, teachingColor),
      (publicationsName, publicationsColor),
      (socialName, socialColor),
    )
    return TikzBranchConfig(branchXPos, branchYPos, titleYOffset, branchOffset, branches, branchLength)
  }

  def createBranches(branchconfig: TikzBranchConfig): String = {
    val sb = new StringBuilder()
    for (branch <- branchconfig.branchMap.values) {
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
      xOffset: Double,
      lastNode: String,
      parentNode: Option[String],
      depth: Int
  ): (String, String) = {
    val (graph, prevNode): (String, String) = item match {
      case cv: CV => {
        if (branchMap.contains(workExperinceName))
          branchCVItems(
            date,
            cv.workExperince,
            branchMap.get(workExperinceName).get,
            xOffset,
            lastNode,
            parentNode,
            depth,
            branchMap
          )
        else
          branchCVItems(
            date,
            cv.education,
            branchMap.get(educationName).get,
            xOffset,
            lastNode,
            parentNode,
            depth,
            branchMap
          )
        // branchCVItems(date, cv.education, TikzBranchConfig.education)
      }

      case work: WorkExperince => {
        branchCVItems(
          date,
          work.projects,
          branchMap.get(projectsName).get,
          xOffset,
          lastNode,
          parentNode,
          depth,
          branchMap
        )
        //     git.merge().include(git.getRepository().resolve("projects")).call()
      }
      case education: Education => {
        val (pubgraph, pubParentNode) = branchCVItems(
          date,
          education.publications,
          branchMap.get(publicationsName).get,
          xOffset,
          lastNode,
          parentNode,
          depth,
          branchMap
        )
        val (teachgraph, teachParentNode) = branchCVItems(
          date,
          education.teaching,
          branchMap.get(teachingName).get,
          xOffset,
          pubParentNode,
          parentNode,
          depth,
          branchMap
        )
        (pubgraph + teachgraph, teachParentNode)
      }
      //  branchCVItems(date, education.publications, "publications", git, path)
      //   git.merge().include(git.getRepository().resolve("publications")).call()
      //   branchCVItems(date, education.teaching, "teaching", git, path)
      //   git.merge().include(git.getRepository().resolve("teaching")).call()
      case project: Project =>
        branchCVItems(
          date,
          project.technologies,
          branchMap.get(technologiesName).get,
          xOffset,
          lastNode,
          parentNode,
          depth,
          branchMap
        )
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
      case tech: Teaching =>
        cvitems.foldLeft(Vector { (tech, tech.start) })((acc, ele) =>
          Vector(tech.copy(title = tech.title + ",  " + ele.title) -> tech.start)
        )
      /*
        val unique = cvitems.foldLeft(Map[String, CVItem]())((acc, ele) => {
          ele match {
            case teaching: Teaching => {


              if (acc.contains(teaching.title)) {
                val old = acc(teaching.title)
                val newTeaching =
                  teaching.copy(title = old.title + " " + teaching.start.format(DateTimeFormatter.ofPattern("YY")))
                acc + (teaching.title -> newTeaching)
              } else
                acc + (teaching.title -> teaching.copy(title =
                  teaching.title + " " + teaching.start.format(DateTimeFormatter.ofPattern("YY"))
                ))
            }
            case _ => acc
          }
        })
        unique.map { case (k, v) => (v, v.start) }.toVector */
      case tech: Technology =>
        cvitems.foldLeft(Vector { (tech, tech.start) })((acc, ele) =>
          Vector(tech.copy(title = tech.title + ",  " + ele.title) -> tech.start)
        )
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
      xOffset: Double,
      lastNode: String,
      parentNode: Option[String],
      depth: Int,
      branchMap: Map[String, TikzBranch]
  ): (String, String) = {
    import scala.language.unsafeNulls

    if(cvitems.isEmpty) return ("", lastNode)


    val sorted = sortCVItemsByDate(cvitems)

    val sb = new StringBuilder()
    val node_pos = 0
    val message_pos = 0
    var first = true
    var prevNode: String = lastNode
    for ((item, date) <- sorted) {
      val hash: String = item.hashCode().toString().substring(1, 8)
      val allinpos =
        if (prevNode == "root") s"below right =1cm and 0cm of ${prevNode}"
        // else s"below right = 0.2cm and ${-textBoxWidth/2.0 + xOffset}cm of ${prevNode}.south"
        else s"below = 0.2cm of ${prevNode}.south"

      val text = item match {
        case tech: Technology =>
          s"""|\\node[${drawboxes} text width=${textBoxWidth - xOffset * depth}cm, $allinpos ${
               if (first) s", xshift=${xOffset}cm" else ""
             } ] (label_$hash)  
              |{${item.title}};""".stripMargin
        case _ =>
          s"""
            |\\node[${drawboxes} text width=${textBoxWidth - xOffset * depth}cm, $allinpos ${
              if (first) s", xshift=${xOffset}cm" else ""
            } ] (label_$hash)  
            |{${item.title}\\\\
            |${item.description}};\n"
            """.stripMargin
      }
      first = false
      val node = s"\\node[commit, left=0.1cm of label_$hash] ($hash)  {};\n"
      val path = s"\\draw[-,${homeBranch.color}, line width=2pt] ($hash -| ${homeBranch.xshift} ,0) -- ($hash);\n"
      val nodeAtBranchLabel = s"${hash}branch"
      val nodeAtBranch = s"\\node[commit] (${nodeAtBranchLabel}) at  ($hash -| ${homeBranch.xshift},0) {};\n"

      sb.append(text)
      sb.append(node)
      sb.append(path)
      sb.append(nodeAtBranch)

      // sb.append(s"\\node[commit] (${hash}blub) at (label_${hash}.south) {};\n)")

      parentNode.map { parent =>
        val pathToParent =
          s"\\draw[-,${homeBranch.color}, line width=2pt] (${nodeAtBranchLabel}) to[out=90,in=-90] ($parent);\n"
        sb.append(pathToParent)
      }
      item match {
        case withend: Education     => addEndTimeToGraph(sb, prevNode, hash, homeBranch, withend)
        case withend: WorkExperince => addEndTimeToGraph(sb, prevNode, hash, homeBranch, withend)
        case _                      =>
      }

      prevNode = s"label_$hash"

      val (subgraph, subLast) =
        delegateToBranch(item, date, branchMap, xOffset + xshift, prevNode, Some(nodeAtBranchLabel), depth + 1)
      sb.append(subgraph)

      prevNode = subLast

      item match {
        case withend: Education     => addStartTimeToGraph(sb, prevNode, hash, homeBranch, withend, prevNode == s"label_$hash")
        case withend: WorkExperince => addStartTimeToGraph(sb, prevNode, hash, homeBranch, withend, prevNode == s"label_$hash")
        case _                      =>
      }

    }
    val invName = "invis" + prevNode
    sb.append(s"\\node[inv, xshift=-${xOffset}cm] (${invName}) at (${prevNode}.south) {};\n")

    return (sb.toString(), invName)
  }

  def addEndTimeToGraph(
      sb: StringBuilder,
      prevNode: String,
      hash: String,
      homeBranch: TikzBranch,
      withend: CVItemWithEnd
  ): Unit = {
    sb.append(
      s"\\node[left = 0cm of ${hash}branch] (datesend$hash) {${withend.`end`.format(DateTimeFormatter.ofPattern("MM/YY"))}};\n"
    )
  }

  def addStartTimeToGraph(
      sb: StringBuilder,
      prevNodex: String,
      hash: String,
      homeBranch: TikzBranch,
      withend: CVItemWithEnd,
      space: Boolean,
  ): Unit = {
    var prevNode = prevNodex 
    if(space) {
      sb.append(s"\\node[inv, yshift=-0.2cm] (datestartinv${hash}branch) at (${prevNode}.south) {};\n")
      prevNode = s"datestartinv${hash}branch"
    }
    sb.append(s"\\node[commit] (datestart${hash}branch) at  ($$(${prevNode} -|  ${hash}branch)$$) {};\n")
    sb.append(
      s"\\draw[-,${homeBranch.color}, line width=3pt] (${hash}branch) to[out=250,in=90] (datestart${hash}branch);\n"
    )
    sb.append(
      s"\\node[left = 0cm of datestart${hash}branch] (datestart${hash}branch) {${withend.start
          .format(DateTimeFormatter.ofPattern("MM/YY"))}};\n"
    )
  }
}
