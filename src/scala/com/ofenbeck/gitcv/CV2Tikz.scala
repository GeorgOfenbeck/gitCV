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
import com.ofenbeck.gitcv.WorkExperience
import com.ofenbeck.gitcv.Education
import com.ofenbeck.gitcv.Project
import com.ofenbeck.gitcv.Technology
import com.ofenbeck.gitcv.TikzBranchConfig
import com.ofenbeck.gitcv.TikzBranch

import java.time.format.DateTimeFormatter
import com.ofenbeck.gitcv.Teaching
import zio.Config.Bool
import com.ofenbeck.gitcv.Main.introprogramming
import com.ofenbeck.gitcv.Social
import com.ofenbeck.gitcv.Publication

object CV2Tikz {

  val workExperienceName = "Work Experience"
  val educationName = "Education"
  val projectsName = "Projects"
  val technologiesName = "Technologies"
  val publicationsName = "Publications"
  val teachingName = "Teaching"
  val socialName = "Social"

  val educationColor = "DarkMidnightBlue"
  val workExperienceColor = "DarkMidnightBlue"
  val publicationsColor = "pistachio"
  val teachingColor = "ivyblue"
  val projectsColor = "pistachio"
  val technologiesColor = "ivyblue"
  val socialColor = "melrose"

  val drawHelpers = false

  val drawboxes = if (!drawHelpers) "" else "draw, "

  val fastcodeOverwrite = "How To Write Fast Numerical Code. [6 semesters]"
  val introProgOverwrite = "BSc Programming Courses (Java, C++). [8 semesters]"
  val textBoxWidth = 16
  val xshift = 0.2
  val multiNodeSpacing = 0.2
  val inCompleteIndent = 0.2
  val fillcolor = "pattensblue"

  val mergeLineStyle = "line width=2pt, solid"
  val pointerLineStyle = "line width=1pt, densely dashed "

  var counter = 1

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
        |\usepackage{hyperref}
        |\usepackage{tabularx}
        |\usepackage[dvipsnames]{xcolor}
        |\geometry{
        |left=0cm,
        |right=1cm,
        |top=0cm,
        |bottom=0cm,
        |includehead,
        |includefoot
        |}
        |\pagenumbering{gobble}
        |\usepackage{array}
        |\newcounter{mycounter}
        |\definecolor{DarkMidnightBlue}{HTML}{1E488F}
        |\definecolor{ivyblue}{HTML}{7DC8F7}
        |\definecolor{pistachio}{HTML}{93C572}
        |\definecolor{melrose}{HTML}{C7C1FF}
        |\definecolor{pattensblue}{HTML}{DEF5FF}
        |\definecolor{french}{HTML}{0072BB}
        |\begin{document}
        |""".stripMargin
        // |\draw[help lines] (-5,0) grid (10,-10);
        // |\resizebox{\columnwidth}{!}{%
        // |\lipsum[1]
    val tail = """
        |\end{document}\n""".stripMargin

    val (graph1, y) = delegateToBranch(cv, LocalDate.now(), workBranches().branchMap, 0.0, "root", None, 1)
    val (graph2, y2) = delegateToBranch(cv, LocalDate.now(), educationBranches().branchMap, 0.0, "root", None, 1)
    val workPicture: String = insertTikzSurrounding(createBranches(workBranches()) + graph1, withHeader = true)

    val educationPicture: String = insertTikzSurrounding(createBranches(educationBranches()) + graph2)

    s"""
        |$headers
        |$workPicture
        |$educationPicture 
        |$tail """.stripMargin
  }

  def staticHeader(): String = {
    s"""
        |\\node[${drawboxes} ] (pic) at (14,5) {\\includegraphics[height=3cm]{img/cvpic_hor.jpg}};
        |\\node[${drawboxes} ]  (name) at (1,6) {\\fontsize{24}{24}\\selectfont\\textbf{Georg Ofenbeck}};
        |\\node[${drawboxes} below=0.5cm of name] (details) {
        |\\begin{tabular}{ l l}
        |Date of Birth: & 12.06.1984\\\\
        |Languages: & German, English\\\\
        |Nationality: & Austrian, Swiss C Permit\\\\
        |Location: & Greater Zurich Area, CH\\\\
        |\\href{https://www.linkedin.com/in/ofenbeck/}{\\includegraphics[height=0.4cm]{img/LinkedIn_Logo.png}}: & /in/ofenbeck\\\\
        |\\end{tabular}
        |};
        |\\node[draw, french, line width=1.5pt,inner sep=5pt, text width=${textBoxWidth}cm, below right=1cm and 0cm of details, xshift=-4.55cm, ] (summary) {
        |\\color{black}\\textbf{$$CV Summary_{(tl:dr)}$$}\\\\
        |I am a Cloud Architect/Software Engineer/Tech Lead with a strong grasp on performance and scalability.
        |This experience ranges from low level programming\\hyperlink{link20}{${link2footnode(20)}}\\hyperlink{link19}{${link2footnode(19)}}\\hyperlink{link18}{${link2footnode(18)}},
        | via compilers\\hyperlink{link16}{${link2footnode(16)}}, up to scaling big data applications\\hyperlink{link9}{${link2footnode(9)}} and SaaS solutions for sensitive data on a public cloud\\hyperlink{link1}{${link2footnode(1)}}.
        |I like to foster a strong team spirit by actively organizing many social events\\hyperlink{link8}{${link2footnode(8)}}\\hyperlink{link14}{${link2footnode(14)}}\\hyperlink{link22}{${link2footnode(22)}}.
        |};
    """.stripMargin
  }
  
  def link2footnode(nr: Int): String = {
    nr match
      case 20 => "$_{[1]}$"
      case 19 => "$_{[2]}$"
      case 18 => "$_{[3]}$"
      case 16 => "$_{[4]}$"
      case 9 => "$_{[5]}$"
      case 1 => "$_{[6]}$"
      case 8 => "$_{[7]}$"
      case 14 => "$_{[8]}$"
      case 22 => "$_{[9]}$"
      case _ => ""  
  }

  def insertTikzSurrounding(content: String, withHeader: Boolean = false): String = {
    s"""
        |\\begin{tikzpicture}${
        if (drawHelpers)
          "[framed,background rectangle/.style={double,ultra thick,draw=red, top color=white, rounded corners}]"
        else ""
      }
        |
        |\\definecolor{babyblue}{rgb}{0.54, 0.81, 0.94}
        |\\definecolor{babyblueeyes}{rgb}{0.63, 0.79, 0.95}
        |\\definecolor{beaublue}{rgb}{0.74, 0.83, 0.9}
        |\\tikzstyle{commit}=[draw,circle,fill=white,inner sep=0pt,minimum size=5pt]
        |\\tikzstyle{inv}=[draw,circle,fill=white,inner sep=0pt,minimum size=${if (drawHelpers) "2pt" else "0pt"}]
        |\\tikzstyle{every path}=[draw]  
        |\\tikzstyle{branch}=[rectangle,rounded corners=3,fill=white,inner sep=2pt,minimum size=5pt]
        |${if (withHeader) staticHeader() else ""}
        |\\node[inv] (root) at (0,0) {};
        """.stripMargin + content + """
        |\end{tikzpicture}%""".stripMargin
  }

  def workBranches(): TikzBranchConfig = {

    val branchXPos = -2
    val branchYPos = 0.0

    val titleYOffset = 0.5
    val branchOffset = 0.5

    val branchLength = -18

    val branches = Vector(
      (workExperienceName, workExperienceColor),
      // ("Publications", publicationsColor),
      // ("Teaching", teachingColor),
      (projectsName, projectsColor),
      (technologiesName, technologiesColor),
      (socialName, socialColor)
      // (educationName, educationColor),
    )
    return TikzBranchConfig(branchXPos, branchYPos, titleYOffset, branchOffset, branches, branchLength)
  }

  def educationBranches(): TikzBranchConfig = {
    val branchXPos = -2
    val branchYPos = 0.0

    val titleYOffset = 0.5
    val branchOffset = 0.5

    val branchLength = -23.0

    val branches = Vector(
      (educationName, educationColor),
      (teachingName, teachingColor),
      (publicationsName, publicationsColor),
      (socialName, socialColor)
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
        if (branchMap.contains(workExperienceName))
          branchCVItems(
            date,
            cv.workExperience,
            branchMap.get(workExperienceName).get,
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

      case work: WorkExperience => {
        val (workgraph, workParentNode) = branchCVItems(
          date,
          work.projects,
          branchMap.get(projectsName).get,
          xOffset,
          lastNode,
          parentNode,
          depth,
          branchMap
        )
        val (socialgraph, socialParentNode) = branchCVItems(
          date,
          work.socials,
          branchMap.get(socialName).get,
          xOffset,
          workParentNode,
          parentNode,
          depth,
          branchMap
        )
        (workgraph + socialgraph, socialParentNode)
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

        val (socialgraph, socialParentNode) = branchCVItems(
          date,
          education.socials,
          branchMap.get(socialName).get,
          xOffset,
          teachParentNode,
          parentNode,
          depth,
          branchMap
        )
        (pubgraph + teachgraph + socialgraph, socialParentNode)
      }
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
      case teach: Teaching =>
        if (cvitems.size < 2)
          return Vector.empty[(CVItem, LocalDate)] // for the sake of desgin we drop the accuaracy of the date
        else {
          val fastcodeOrig = cvitems.find(_.title == "How To Write Fast Numerical Code").get
          val fastcode = Teaching(
            title = fastcodeOverwrite,
            start = LocalDate.of(2017, 4, 1).nn,
            description = fastcodeOrig.description
          )
          val rest = Teaching(
            title = introProgOverwrite,
            start = LocalDate.of(2016, 4, 1).nn,
            description = ""
          )
          return Vector((fastcode, fastcode.start), (rest, rest.start))
        }
      case tech: Technology =>
        Vector((tech.copy(title = cvitems.map(_.title).mkString(", "), description = ""), tech.start))
      case social: Social =>
        Vector((social.copy(title = cvitems.map(_.title).mkString(", "), description = ""), social.start))
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

    if (cvitems.isEmpty) return ("", lastNode)

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
        case pub: Publication =>
          s"""|\\node[${drawboxes} text width=${textBoxWidth - xOffset * depth}cm, $allinpos ${
               if (first) s", xshift=${xOffset}cm" else ""
             } ] (label_$hash)  
              |{${if (depth == 1) "\\textbf{" else ""}${if (depth == 2) "\\textit{" else ""}
              |\\hypertarget{link${counter}}{${item.title}}${
               if (depth == 1 || depth == 2) "}" else ""
             }${link2footnode(counter)}\\\\
              |${item.description}\\\\
              |\\vspace{0.1cm}
              |\\begin{tabular}{ p{5cm} p{5cm} p{5cm}}
              |${
               if (pub.publication != "")
                 s" Publication: \\href{${pub.publication}}{\\centering \\includegraphics[height=0.4cm]{img/web.png}}  "
               else ""
             }
              |&
              |${
               if (pub.github.isDefined)
                 s"Github: \\href{${pub.github.get}}{ \\centering \\includegraphics[height=0.4cm]{img/git.png}} "
               else ""
             }
              |&
              |${
               if (pub.thesis.isDefined)
                 s"Thesis: \\href{${pub.thesis.get}}{ \\centering \\includegraphics[height=0.4cm]{img/pdf.jpg}} "
               else ""
             }
              |\\\\
              |\\end{tabular}
              | };
            """.stripMargin
        case _ =>
          s"""
            |\\node[${drawboxes} text width=${textBoxWidth - xOffset * depth}cm, $allinpos ${
              if (first) s", xshift=${xOffset}cm" else ""
            } ] (label_$hash)  
            |{
            |${if (depth == 1) "\\color{DarkMidnightBlue}\\underline{\\textbf{" else ""}${
              if (depth == 2) "\\textit{" else ""
            }\\hypertarget{link${counter}}{${item.title}}${
              if (depth == 1)
                "}}"
              else if (depth == 2) "}"
              else ""
            } ${link2footnode(counter)}\\hfill ${addPlace(item)}\\\\
            |${item.description}};\n
            """.stripMargin
      }
      first = false

      counter = counter + 1
      val incompleteOffset =
        if (item.title == "Diploma Student, Molecular Biology" || item.title == "PhD student, Computer Science")
          inCompleteIndent
        else 0

      val node = s"\\node[commit, left=0.1cm of label_$hash] ($hash)  {};\n"
      val path =
        s"\\draw[-,${homeBranch.color},${pointerLineStyle}] ($hash -| ${homeBranch.xshift + incompleteOffset} ,0) -- ($hash);\n"
      val nodeAtBranchLabel = s"${hash}branch"
      val nodeAtBranch =
        s"\\node[commit] (${nodeAtBranchLabel}) at  ($hash -| ${homeBranch.xshift + incompleteOffset},0) {};\n"

      sb.append(text)
      sb.append(node)
      sb.append(path)
      sb.append(nodeAtBranch)

      parentNode.map { parent =>
        val pathToParent =
          s"\\draw[-,${homeBranch.color}, ${mergeLineStyle}] (${nodeAtBranchLabel}) to[out=90,in=-90] ($parent);\n"
        sb.append(pathToParent)
      }

      if (item.title == fastcodeOverwrite) multiNodeAtBranch(sb, hash, homeBranch, 6)
      if (item.title == introProgOverwrite) multiNodeAtBranch(sb, hash, homeBranch, 8)
      item match {
        case withend: Education     => addEndTimeToGraph(sb, prevNode, hash, homeBranch, withend, incompleteOffset)
        case withend: WorkExperience => addEndTimeToGraph(sb, prevNode, hash, homeBranch, withend, incompleteOffset)
        case _                      =>
      }

      prevNode = s"label_$hash"

      val (subgraph, subLast) =
        delegateToBranch(item, date, branchMap, xOffset + xshift, prevNode, Some(nodeAtBranchLabel), depth + 1)
      sb.append(subgraph)

      prevNode = subLast

      item match {
        case withend: Education =>
          addStartTimeToGraph(sb, prevNode, hash, homeBranch, withend, prevNode == s"label_$hash", incompleteOffset)
        case withend: WorkExperience =>
          addStartTimeToGraph(sb, prevNode, hash, homeBranch, withend, prevNode == s"label_$hash", incompleteOffset)
        case _ =>
      }

    }
    val invName = "invis" + prevNode
    sb.append(s"\\node[inv, xshift=-${xOffset}cm] (${invName}) at (${prevNode}.south) {};\n")

    return (sb.toString(), invName)
  }

  def addPlace(cvitem: CVItem): String ={
    cvitem match
      case work: WorkExperience => s"${work.company}"
      case edu: Education => s"${edu.school}"
      case _ => ""    
  }


  def multiNodeAtBranch(
      sb: StringBuilder,
      hash: String,
      homeBranch: TikzBranch,
      count: Int
  ): Unit = {
    val nodeAtBranchLabel = s"${hash}branch"
    for (i <- 0 to count) {
      val nodeAbove =
        s"\\node[commit, above = ${-3 * multiNodeSpacing + multiNodeSpacing * i}cm of ${nodeAtBranchLabel} ] (${nodeAtBranchLabel}$i)  {};\n"
      sb.append(nodeAbove)
      sb.append(s"\\draw[-,${homeBranch.color}, ${pointerLineStyle}] (${nodeAtBranchLabel}${i})  -- ($hash);\n")
    }
  }

  def addEndTimeToGraph(
      sb: StringBuilder,
      prevNode: String,
      hash: String,
      homeBranch: TikzBranch,
      withend: CVItemWithEnd,
      incompleteOffset: Double
  ): Unit = {
    sb.append(
      s"\\node[left = ${incompleteOffset}cm of ${hash}branch] (datesend$hash) {${withend.`end`
          .format(DateTimeFormatter.ofPattern("MM/YY"))}};\n"
    )
  }

  def addStartTimeToGraph(
      sb: StringBuilder,
      prevNodex: String,
      hash: String,
      homeBranch: TikzBranch,
      withend: CVItemWithEnd,
      space: Boolean,
      incompleteOffset: Double
  ): Unit = {
    var prevNode = prevNodex
    if (space) {
      sb.append(s"\\node[inv, yshift=-0.1cm] (datestartinv${hash}branch) at (${prevNode}.south) {};\n")
      prevNode = s"datestartinv${hash}branch"
    }
    if (withend.title == "BSc, Medical Informatics") {
      sb.append(s"\\node[commit] (bscend) at  (${hash}branch) {};\n")
    } else {
      sb.append(
        s"\\node[commit, xshift=${-1 * incompleteOffset}cm] (datestart${hash}branch) at  ($$(${prevNode} -|  ${hash}branch)$$) {};\n"
      )
      sb.append(
        s"\\draw[-,${homeBranch.color}, line width=3pt] (${hash}branch) to[out=250,in=90] (datestart${hash}branch);\n"
      )
      sb.append(
        s"\\node[left = 0cm of datestart${hash}branch] (datestart${hash}branchtdate) {${withend.start
            .format(DateTimeFormatter.ofPattern("MM/YY"))}};\n"
      )
      if (withend.title == "Diploma Student, Molecular Biology") {
        sb.append(
          s"\\draw[-,${homeBranch.color}, line width=3pt] (bscend) to[out=220,in=100] (datestart${hash}branch);\n"
        )
      }
    }
  }
}
