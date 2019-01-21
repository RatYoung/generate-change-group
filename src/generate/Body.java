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

		//String rollBackNum = args[2];
		//String newVerPath = args[0];
		//String oldVerPath = args[1] + rollBackNum;
		String rollBackNum = "50";
		String newVerPath = "C:\\Users\\24541\\Desktop\\创新项目\\source_codes\\测试用开源工程\\commons-io-master";
		String oldVerPath = "C:\\Users\\24541\\Desktop\\创新项目\\source_codes\\测试用开源工程\\commons-io-head~"+rollBackNum;
		String reqPath = "C:\\Users\\24541\\Desktop\\req-swing-demo\\data\\sample\\AquaLush_Requirement";
		
		//copy master dir in order to prepare older version
		File newVersion = new File(newVerPath);
		File oldVersion = new File(oldVerPath);
		oldVersion.mkdirs();
		FileUtils.copyDirectory(newVersion, oldVersion);
		
		//get older version
		GitUtils.checkoutCommit(oldVersion, "HEAD~"+rollBackNum);
		
		//mkdirs for java files
		File newJavaPath = new File(newVerPath + "-java");
		newJavaPath.mkdirs();
		File oldJavaPath = new File(oldVerPath + "-java");
		oldJavaPath.mkdirs();
		
		//String normalNewPath = FilenameUtils.normalize(newVerPath);
		//String normalOldPath = FilenameUtils.normalize(oldVerPath);
		
		//extracting java files
		Collection<File> newListFiles = FileUtils.listFiles(new File(newVerPath), FileFilterUtils.suffixFileFilter("java"), DirectoryFileFilter.INSTANCE);
		Collection<File> oldListFiles = FileUtils.listFiles(new File(oldVerPath), FileFilterUtils.suffixFileFilter("java"), DirectoryFileFilter.INSTANCE);
		for (File file : newListFiles) {
			FileUtils.copyFileToDirectory(file, newJavaPath);
		}
		for (File file : oldListFiles) {
			FileUtils.copyFileToDirectory(file, oldJavaPath);
		}
		
		re.process(newJavaPath.getAbsolutePath(), oldJavaPath.getAbsolutePath(), reqPath);
	}
}
