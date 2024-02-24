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
      .setMessage("Initial commit")
      .setCommitter(createCommiter(cv.birthdate))
      .setAuthor(createCommiter(cv.birthdate))
      .call()
    createBranchWithInitCommit(cv.birthdate,git, path, "cv")

    // git.commit().setMessage("Initial commit").call()
    // git.branchCreate().setName("cv").call()
    // Files.write(Paths.get(cvFile.getAbsolutePath), cv.toJson.getBytes)
    // git.add().addFilepattern(path + "cv.json").call()
    // git.commit().setMessage("Initial commit").call()
    branchCVItems(cv.birthdate, cv.workExperince, "Work", git, path)
    branchCVItems(cv.birthdate, cv.education, "Education", git, path)
    // git.push().call()
    println(path)
  }

  def createCommiter(date: LocalDate) = {
    import org.eclipse.jgit.lib.PersonIdent;
    val commitTime: Instant = date.atStartOfDay().toInstant(ZoneOffset.UTC)
    val committer = new PersonIdent("Georg Ofenbeck", "georg@ofenbeck.com")
    val committerWithTime = new PersonIdent(committer, commitTime)
    committerWithTime
  }

  def createBranchWithInitCommit(commitDate: LocalDate,
      git: Git,
      path: String,
      branchName: String
  ): Unit = {
    git.branchCreate().setName(branchName).call()
    git.checkout().setName(branchName).call()
    val cvFile = new File(s"$path/$branchName.json")
    cvFile.createNewFile()
    git.add().addFilepattern(s"$path/$branchName.json").call()
    git
      .commit()
      .setMessage("Initial commit")
      .setCommitter(createCommiter(cv.birthdate))
      .setAuthor(createCommiter(cv.birthdate))
      .call()
  }

  def branchCVItems(
      branchdate: LocalDate,
      cvitems: List[CVItem],
      branch: String,
      git: Git,
      path: String
  ): Unit = {
    import scala.language.unsafeNulls
    val origin = git.getRepository().getBranch()
    createBranchWithInitCommit(branchdate ,git, path, branch)
    val cronCVIems = cvitems.map(e => (e.start, e)).toMap ++ cvitems.map(e => (e.end, e)).toMap
    
    for ( (date, item) <- cronCVIems.toVector.sortBy(_._1)) {      
      git.checkout().setName(branch).call()
      val subBranch = item.title.toString().replace(" ", "_") // .replace(",", "_")
      if(item.start == date) {
        git.branchCreate().setName(subBranch).call()
      }  
      git.checkout().setName(subBranch).call()
      val filename = s"$path/${branch}_${UUID.randomUUID()}.txt"
      val changeFile = new File(filename)
      changeFile.createNewFile()

      import java.nio.file.{Files, Paths}
      Files.write(Paths.get(changeFile.getAbsolutePath), "xxx".getBytes)
      git
        .add()
        .addFilepattern(filename)
        .call()
      val commitmsg: String = s"""${item.title}
      | 
      |${item.description}""".stripMargin

      git
        .commit()
        .setMessage(commitmsg)
        .setCommitter(createCommiter(date))
        .setAuthor(createCommiter(date))
        .call()
      git.checkout().setName(branch).call()
      if (date == item.end) {
        git.merge().include(git.getRepository().resolve(subBranch)).call()
      }
    }
    git.checkout().setName(origin).call()
  }

  // i want to create an empty git repository on a given path
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
