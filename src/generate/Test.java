package generate;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Collection;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.eclipse.jgit.api.errors.CheckoutConflictException;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.InvalidRefNameException;
import org.eclipse.jgit.api.errors.RefAlreadyExistsException;
import org.eclipse.jgit.api.errors.RefNotFoundException;
import org.eclipse.jgit.internal.storage.file.FileRepository;
import org.sqlite.SQLiteException;

public class Test {
	@SuppressWarnings("unused")
	public static void main(String[] args) throws IOException, RefAlreadyExistsException, RefNotFoundException, InvalidRefNameException, CheckoutConflictException, GitAPIException, SQLException, SQLiteException {
		//needless requirement path!
		String reqPath = "C:\\Users\\24541\\Desktop\\req-swing-demo\\data\\sample\\AquaLush_Requirement";
		
		
		NewRetro re = new NewRetro();
		String name = "vert.x";
		String localRepoPath = "E:\\Desktop\\workspace\\创新项目：紧密度追踪过时需求\\project1006\\"+name;
		double t0 = System.currentTimeMillis();
		String newCommit = "d887515f857acb07a903130cd8a0321cc8dc653c";
		String rollBack = newCommit + "~1";
		Boolean isSaved = false;	
		File localVersion = new File(localRepoPath);
		
		boolean hasJavaFiles = GitUtils.hasJavaFiles(localVersion, newCommit);
		if(!hasJavaFiles) {
			System.out.println("-------------Diffs DON'T contain *.java files----------");
			System.out.println("Commit: " + newCommit);
			double t1 = System.currentTimeMillis();
			System.out.println("Finished.\nTime used: " + ((t1 - t0) / 1000) + "s");
			return;
		}
	
		//get current ObjectId in order to checkout back
		FileRepository repo = new FileRepository(localRepoPath + "\\.git");
		String currentBranch = repo.getBranch();
		GitUtils.currentBranch = currentBranch;
		repo.close();
		
		//mkdirs for java files
		File newJavaPath = new File(localRepoPath+"-"+newCommit+"-java");
		newJavaPath.mkdirs();
		File oldJavaPath = new File(localRepoPath+"-"+rollBack+"-java");
		oldJavaPath.mkdirs();
		
		//get older version
		
		GitUtils.checkoutCommit(localVersion, rollBack);
		Collection<File> oldListFiles = FileUtils.listFiles(localVersion, FileFilterUtils.suffixFileFilter("java"), DirectoryFileFilter.INSTANCE);
		for (File file : oldListFiles) {
			FileUtils.copyFileToDirectory(file, oldJavaPath);
		}
		GitUtils.checkoutCommit(localVersion, currentBranch);
		
		GitUtils.checkoutCommit(localVersion, newCommit);
		Collection<File> newListFiles = FileUtils.listFiles(localVersion, FileFilterUtils.suffixFileFilter("java"), DirectoryFileFilter.INSTANCE);
		for (File file : newListFiles) {
			FileUtils.copyFileToDirectory(file, newJavaPath);
		}
		GitUtils.checkoutCommit(localVersion, currentBranch);
		
		System.out.println();
		System.out.println("-------------Generating ChangeGroups----------");
		try {
			re.process(newJavaPath.getAbsolutePath(), oldJavaPath.getAbsolutePath(), reqPath, isSaved);
		}catch(Exception e) {
			System.out.println(e.getClass().getName());
			if (!isSaved) {
				Boolean deleteNewJava = FileUtils.deleteQuietly(newJavaPath);
				Boolean deleteOldJava = FileUtils.deleteQuietly(oldJavaPath);
			}
			return;
		}
		if (!isSaved) {
			Boolean deleteNewJava = FileUtils.deleteQuietly(newJavaPath);
			Boolean deleteOldJava = FileUtils.deleteQuietly(oldJavaPath);
		}
		double t1 = System.currentTimeMillis();
		System.out.println("Finished.\nTime used: " + ((t1 - t0) / 1000) + "s");
	}
}