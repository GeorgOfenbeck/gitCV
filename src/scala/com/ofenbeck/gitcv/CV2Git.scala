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

class CV2Git {}

object CV2Git {

// i want to create an empty git repository on a given path and create a file with the content of the CV on branch cv
  def createGitRepositoryWithCV(path: String, cv: CV): Unit = {
    val cvFile = new File(path + "/cv.json")
    cvFile.createNewFile()

    import java.nio.file.{Files, Paths}
    val git = Git.init().setDirectory(new File(path)).call()
    git.commit().setMessage("Initial commit").call()
    git.branchCreate().setName("cv").call()
    Files.write(Paths.get(cvFile.getAbsolutePath), cv.toJson.getBytes)
    git.add().addFilepattern(path + "cv.json").call()
    git.commit().setMessage("Initial commit").call()
    branchEducation(cv, git, path)
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

  def branchEducation(cv: CV, git: Git, path: String): Unit = {
    import scala.language.unsafeNulls
    val branch = "Education"
    git.branchCreate().setName(branch).call()
    git.checkout().setName(branch).call() // checkout the branch Education

    val eduFile = new File(path + "/edu.json")
    eduFile.createNewFile()

    git.add().addFilepattern(path + "edu.json").call()
    git.commit().setMessage("Initial commit edu").call()

    val education = cv.education

    for (e <- education.sortBy(_.start)) {

      git.checkout().setName(branch).call() // checkout the branch Education
      val subBranch = e.title.toString().replace(" ", "_")//.replace(",", "_")
      git.branchCreate().setName(subBranch).call()
      git.checkout().setName(subBranch).call()
      val educationFile = new File(
        path + "/education_" + e.school + ".json"
      )
      educationFile.createNewFile()

      import java.nio.file.{Files, Paths}
      Files.write(Paths.get(educationFile.getAbsolutePath), e.toJson.getBytes)
      git
        .add()
        .addFilepattern(
          path + "/education_" + e.school + ".json"
        )
        .call()
      val commitmsg: String = s"""${e.title}
      | 
      |${e.description}""".stripMargin

      git
        .commit()
        .setMessage(commitmsg)
        .setCommitter(createCommiter(e.start))
        .setAuthor(createCommiter(e.start))
        .call()

    }
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
