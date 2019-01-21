package generate;

import generate.NewRetro;
import generate.GitUtils;

import java.io.File;
import java.io.IOException;
import java.util.Collection;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.eclipse.jgit.api.errors.CheckoutConflictException;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.InvalidRefNameException;
import org.eclipse.jgit.api.errors.RefAlreadyExistsException;
import org.eclipse.jgit.api.errors.RefNotFoundException;

public class Body {
	public static void main(String[] args) throws IOException, RefAlreadyExistsException, RefNotFoundException, InvalidRefNameException, CheckoutConflictException, GitAPIException {
		NewRetro re = new NewRetro();

		//String rollBackNum = args[1];
		//String newVerPath = args[0];
		//String oldVerPath = args[0] +"-head~"+rollBackNum;
		//String rollBackNum = "50";
		//String newVerPath = "C:\\Users\\24541\\Desktop\\创新项目\\source_codes\\测试用开源工程\\commons-io";
		//String oldVerPath = "C:\\Users\\24541\\Desktop\\创新项目\\source_codes\\测试用开源工程\\commons-io"+"-head~"+rollBackNum;
		String reqPath = "C:\\Users\\24541\\Desktop\\req-swing-demo\\data\\sample\\AquaLush_Requirement";
		
		String localRepoPath = "C:\\Users\\24541\\Desktop\\创新项目\\source_codes\\测试用开源工程\\commons-io";
		String rollBack = "b487c9e8bc84";
		String newCommit = "d403c";
		Boolean isSaved = true;
		
		//copy master dir in order to prepare older version
		File localVersion = new File(localRepoPath);
		File newVersion = new File(localRepoPath+"-"+newCommit);
		File oldVersion = new File(localRepoPath+"-"+rollBack);
		newVersion.mkdirs();
		oldVersion.mkdirs();
		FileUtils.copyDirectory(localVersion, newVersion);
		FileUtils.copyDirectory(localVersion, oldVersion);
		
		//get older version
		GitUtils.checkoutCommit(newVersion, newCommit);
		GitUtils.checkoutCommit(oldVersion, rollBack);
		
		//mkdirs for java files
		File newJavaPath = new File(localRepoPath+"-"+newCommit+"-java");
		newJavaPath.mkdirs();
		File oldJavaPath = new File(localRepoPath+"-"+rollBack+"-java");
		oldJavaPath.mkdirs();
		
		//String normalNewPath = FilenameUtils.normalize(newVerPath);
		//String normalOldPath = FilenameUtils.normalize(oldVerPath);
		
		//extracting java files
		Collection<File> newListFiles = FileUtils.listFiles(newVersion, FileFilterUtils.suffixFileFilter("java"), DirectoryFileFilter.INSTANCE);
		Collection<File> oldListFiles = FileUtils.listFiles(oldVersion, FileFilterUtils.suffixFileFilter("java"), DirectoryFileFilter.INSTANCE);
		for (File file : newListFiles) {
			FileUtils.copyFileToDirectory(file, newJavaPath);
		}
		for (File file : oldListFiles) {
			FileUtils.copyFileToDirectory(file, oldJavaPath);
		}
		
		re.process(newJavaPath.getAbsolutePath(), oldJavaPath.getAbsolutePath(), reqPath, isSaved);
		
		Boolean deleteNew = FileUtils.deleteQuietly(newVersion);
		Boolean deleteOld = FileUtils.deleteQuietly(oldVersion);
		
		if (!isSaved) {
			Boolean deleteNewJava = FileUtils.deleteQuietly(newJavaPath);
			Boolean deleteOldJava = FileUtils.deleteQuietly(oldJavaPath);
		}
	}
}
