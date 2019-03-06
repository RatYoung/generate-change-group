package generate;

import generate.NewRetro;
import generate.GitUtils;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Collection;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.eclipse.jgit.api.errors.*;
import org.eclipse.jgit.internal.storage.file.FileRepository;

public class Body {
	@SuppressWarnings("unused")
	public static void main(String[] args) throws IOException, RefAlreadyExistsException, RefNotFoundException, InvalidRefNameException, CheckoutConflictException, GitAPIException {
		double t0 = System.currentTimeMillis();
		
		NewRetro re = new NewRetro();
		
		//needless requirement path!
		String reqPath = "C:\\Users\\24541\\Desktop\\req-swing-demo\\data\\sample\\AquaLush_Requirement";
		
//		String localRepoPath = "E:\\Desktop\\workspace\\创新项目：紧密度追踪过时需求\\project1006\\okhttp";
//		String localRepoPath = "E:\\Desktop\\workspace\\创新项目：紧密度追踪过时需求\\project1006\\elasticsearch"; //failed
//		String localRepoPath = "E:\\Desktop\\workspace\\创新项目：紧密度追踪过时需求\\project1006\\retrofit";
//		String localRepoPath = "E:\\Desktop\\workspace\\创新项目：紧密度追踪过时需求\\project1006\\android-best-practices";
//		String localRepoPath = "E:\\Desktop\\workspace\\创新项目：紧密度追踪过时需求\\project1006\\RxJava";
//		String localRepoPath = "E:\\Desktop\\workspace\\创新项目：紧密度追踪过时需求\\project1006\\react-native";	//failed
//		String localRepoPath = "E:\\Desktop\\workspace\\创新项目：紧密度追踪过时需求\\project1006\\ion";
//		String localRepoPath = "E:\\Desktop\\workspace\\创新项目：紧密度追踪过时需求\\project1006\\hive";
//		String localRepoPath = "E:\\Desktop\\workspace\\创新项目：紧密度追踪过时需求\\project1006\\liquibase";
		String localRepoPath = "F:\\创新项目\\sample projects\\okhttp";
		
		String newCommit = "757f8ede8936402e576a16988f50994eda41f7a1";
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
			StringWriter sw = null;
			PrintWriter pw = null;
			System.out.println(e);
			sw = new StringWriter();
			pw = new PrintWriter(sw);
			e.printStackTrace(pw);
			pw.flush();
			sw.flush();
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
