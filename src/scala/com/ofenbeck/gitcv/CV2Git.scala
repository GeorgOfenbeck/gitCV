package scala.com.ofenbeck.gitcv

import java.io.File
import org.eclipse.jgit.api.Git
import org.eclipse.jgit.lib.Repository
import org.eclipse.jgit.storage.file.FileRepositoryBuilder
import com.ofenbeck.gitcv.CV
import com.ofenbeck.gitcv.main.cv

    import scala.language.unsafeNulls
    import zio.json.EncoderOps

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

    // git.push().call()
    println(path)
  }

  def branchEducation(cv: CV, git: Git): Unit = {
    import scala.language.unsafeNulls
    val branch = "Education"
    git.checkout().setName(branch).call()
    val education = cv.education
    for (e <- education) {
      val educationFile = new File(
        git.getRepository.getDirectory.getParent + "/education/" + e.school + ".json"
      )
      educationFile.createNewFile()

      import java.nio.file.{Files, Paths}
      Files.write(Paths.get(educationFile.getAbsolutePath), e.toJson.getBytes)
      git
        .add()
        .addFilepattern(
          git.getRepository.getDirectory.getParent + "/education/" + e.school + ".json"
        )
        .call()
      val commitmsg: String = """${e.title}
      
      ${e.description}
      """ 
      git.commit().setMessage(commitmsg).call()
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
