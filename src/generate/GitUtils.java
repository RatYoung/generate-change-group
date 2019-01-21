package generate;

import java.io.File;
import java.io.IOException;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.*;
import org.eclipse.jgit.internal.storage.file.FileRepository;
import org.eclipse.jgit.lib.ObjectId;

public class GitUtils {
	
	public static void checkoutCommit(File repoDir, String commitName) throws IOException, RefAlreadyExistsException, RefNotFoundException, InvalidRefNameException, CheckoutConflictException, GitAPIException {
		File gitDir = new File(repoDir.getAbsolutePath() + "\\.git");
		FileRepository repo = new FileRepository(gitDir);
		Git git = new Git(repo);
		ObjectId id = repo.resolve(commitName);
		git.checkout().setName(id.name()).call();
		System.out.println("-------------Checkout successfully----------");
		System.out.println("Commit: " + commitName);
		System.out.println("SHA-1: " + id.name());
		git.getRepository().close();
	}
	
	
	
	public static void main(String[] args) throws RefAlreadyExistsException, RefNotFoundException, InvalidRefNameException, CheckoutConflictException, IOException, GitAPIException {
		File repoDir = new File("E:\\Desktop\\workspace\\创新项目：紧密度追踪过时需求\\测试用开源工程\\commons-io");
		GitUtils.checkoutCommit(repoDir, "HEAD^");  //commit SHA-1 value  or  HEAD[^] or master or etc.
	}
}
