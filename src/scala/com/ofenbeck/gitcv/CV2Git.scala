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

class CV2Git {}

object CV2Git {

// i want to create an empty git repository on a given path and create a file with the content of the CV on branch cv
  def createGitRepositoryWithCV(path: String, cv: CV): Unit = {
    val cvFile = new File(path + "/cv.json")
    cvFile.createNewFile()

    import java.nio.file.{Files, Paths}
    val git = Git.init().setDirectory(new File(path)).call()

    git
      .commit()
      .setMessage("Initial commit Repo")
      .setCommitter(createCommiter(cv.birthdate))
      .setAuthor(createCommiter(cv.birthdate))
      .call()

    createBranchWithInitCommit(cv.birthdate, git, path, "cv")
    createBranchWithInitCommit(cv.birthdate, git, path, "technologies")
    delegateToBranch(cv, cv.birthdate, git, path)
    println(path)
  }
/**
  * createCommiter creates a commiter with a given date 
  *
  * @param date
  * @return
  */
  def createCommiter(date: LocalDate) = {
    import org.eclipse.jgit.lib.PersonIdent;
    val commitTime: Instant = date.atStartOfDay().toInstant(ZoneOffset.UTC)
    val committer = new PersonIdent("Georg Ofenbeck", "georg@ofenbeck.com")
    val committerWithTime = new PersonIdent(committer, commitTime)
    committerWithTime
  }
/**
  * createBranchIfNotExists creates a branch if it does not exist
  *
  * @param commitDate
  * @param git
  * @param path
  * @param branchName
  */
  def createBranchIfNotExists(
      commitDate: LocalDate,
      git: Git,
      path: String,
      branchName: String,
      withInitCommit: Boolean = true
  ): Unit = {
    import scala.language.unsafeNulls
    import scala.jdk.CollectionConverters._
    val branches = git.branchList().call()
    val branchNames = branches.asScala.map(_.getName).toSet
    val branchExists = branchNames.contains( s"refs/heads/$branchName")
    if (!branchExists) {
      if (withInitCommit)
      createBranchWithInitCommit(commitDate, git, path, branchName)
      else
      git.branchCreate().setName(branchName).call()
    }
  }
/**
  * createBranchWithInitCommit creates a branch with an initial commit
  *
  * @param commitDate
  * @param git
  * @param path
  * @param branchName
  */
  def createBranchWithInitCommit(
      commitDate: LocalDate,
      git: Git,
      path: String,
      branchName: String
  ): Unit = {
    git.branchCreate().setName(branchName).call()
    git.checkout().setName(branchName).call()
    val cvFile = new File(s"$path/$branchName.json")
    cvFile.createNewFile()
    git.add().addFilepattern(".").call()
    git
      .commit()
      .setMessage("Initial commit")
      .setCommitter(createCommiter(cv.birthdate))
      .setAuthor(createCommiter(cv.birthdate))
      .call()
  }
/**
  * sortCVItemsByDate sorts the CVItems of a CV by date
  *
  * @param cvitems
  * @return
  */
  def sortCVItemsByDate(cvitems: List[CVItem]): Vector[(CVItem, LocalDate)] = {
    val cronCVIems =
      cvitems.foldLeft(Vector.empty[(CVItem, LocalDate)])((acc, e) => {
        val start = e.start
        e match {
          case withend: CVItemWithEnd =>
            acc :+ (withend -> start) :+ (withend -> withend.end)
          case _ => acc :+ (e -> start)
        }
      })
    return cronCVIems.sortBy(_._2)
  }
/**
  * createCVItemFile creates a file for a CVItem in the git repository
  *
  * @param homeBranch
  * @param git
  * @param path
  */
  def createCVItemFile(
      homeBranch: String,
      git: Git,
      path: String
  ): Unit = {
    // create a file for the item
    val filename = s"$path/${homeBranch}_${UUID.randomUUID()}.txt"
    val changeFile = new File(filename)
    changeFile.createNewFile()

    import java.nio.file.{Files, Paths}
    Files.write(Paths.get(changeFile.getAbsolutePath), "xxx".getBytes)
    git
      .add()
      .addFilepattern(".")
      .call()
  }
  /**
    * commit a CVItem to the git repository 
    *
    * @param item 
    * @param date
    * @param git
    */
  def commitCVItem(item: CVItem, date: LocalDate, git: Git): Unit = {
    val commitmsg: String = s"""${item.title}
      | 
      |${item.description}""".stripMargin

    git
      .commit()
      .setMessage(commitmsg)
      .setCommitter(createCommiter(date))
      .setAuthor(createCommiter(date))
      .call()
  }
  

  /**
    * branchCVItems branches the CVItems of a CV to the git repository
    *
    * @param branchdate
    * @param cvitems
    * @param homeBranch
    * @param git
    * @param path
    */
  def branchCVItems(
      branchdate: LocalDate,
      cvitems: List[CVItem],
      homeBranch: String,
      git: Git,
      path: String
  ): Unit = {
    import scala.language.unsafeNulls
    val originBranch = git.getRepository().getBranch()
    createBranchIfNotExists(branchdate, git, path, homeBranch)
    val sorted = sortCVItemsByDate(cvitems)
    for ((item, date) <- sortCVItemsByDate(cvitems)) {
      // go to home branch
      git.checkout().setName(homeBranch).call()

      // create a branch for the item
      val subBranch = item.title.toString().replace(" ", "_")
      if (item.start == date) createBranchIfNotExists(date, git, path, subBranch, false)
      git.checkout().setName(subBranch).call()

      createCVItemFile(homeBranch, git, path)
      commitCVItem(item, date, git)

      if(item.start == date) delegateToBranch(item, date, git, path)

      // go back to home branch and merge the subbranch
      git.checkout().setName(homeBranch).call()
      item match {
        case withend: CVItemWithEnd =>
          if (date == withend.end) {
            git.merge().include(git.getRepository().resolve(subBranch)).call()
          }
        case _: CVItem =>
          git.merge().include(git.getRepository().resolve(subBranch)).call()
      }
    }
    git.checkout().setName(originBranch).call()
  }
/**
  * delegateToBranch delegates the CVItems of a CV to the git repository
  *
  * @param item
  * @param date
  * @param git
  * @param path
  */
  def delegateToBranch(
      item: CVItem,
      date: LocalDate,
      git: Git,
      path: String
  ): Unit = {
    item match {
      case cv: CV => {
        branchCVItems(date, cv.education, "education", git, path)
        branchCVItems(date, cv.workExperience, "workExperince", git, path)
      }
      
        case work: WorkExperience => {
          branchCVItems(date, work.projects, "projects", git, path)
          git.merge().include(git.getRepository().resolve("projects")).call()
        }
        case education: Education =>
          branchCVItems(date, education.publications, "publications", git, path)
          git.merge().include(git.getRepository().resolve("publications")).call()
          branchCVItems(date, education.teaching, "teaching", git, path)
          git.merge().include(git.getRepository().resolve("teaching")).call()
        case project: Project =>
           branchCVItems(date, project.technologies, "technologies", git, path)
           git.merge().include(git.getRepository().resolve("technologies")).call()
        case _ => ()

    }
  }
/**
  * createEmptyGitRepository creates an empty git repository on a given path
  */
  def createEmptyGitRepository(path: String): Unit = {
    import scala.language.unsafeNulls
    val repositoryPath = new File(path)
    val builder = new FileRepositoryBuilder()
    val repository = builder
      .setGitDir(repositoryPath)
      .readEnvironment()
      .findGitDir()
      .build()
    repository.create()
    repository.close()
  }
}
