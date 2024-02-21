package scala.com.ofenbeck.gitcv

   import java.io.File
   import org.eclipse.jgit.api.Git
   import org.eclipse.jgit.lib.Repository
   import org.eclipse.jgit.storage.file.FileRepositoryBuilder
class CV2Git {
  
}


object CV2Git {


 // i want to create an empty git repository on a given path
 def createEmptyGitRepository(path: String): Unit = {
   import scala.language.unsafeNulls
   val repositoryPath = new File(path)
   val builder = new FileRepositoryBuilder()
   val repository = builder.setGitDir(repositoryPath)
     .readEnvironment()
     .findGitDir()
     .build()
   repository.create()
   repository.close()
 }   
}

