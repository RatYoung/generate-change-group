package edu.nju.cs.inform.generate;

import edu.nju.cs.inform.generate.NewRetro;

import java.io.File;
import java.io.IOException;
import java.util.Collection;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.commons.io.filefilter.FileFilterUtils;

public class Body {
	public static void main(String[] args) throws IOException {
		NewRetro re = new NewRetro();

		//String newVerPath = args[0];
		//String oldVerPath = args[1];
		String newVerPath = "C:\\Users\\24541\\Desktop\\创新项目\\source_codes\\测试用开源工程\\commons-io-master";
		String oldVerPath = "C:\\Users\\24541\\Desktop\\创新项目\\source_codes\\测试用开源工程\\commons-io-head~50";
		String reqPath = "C:\\Users\\24541\\Desktop\\req-swing-demo\\data\\sample\\AquaLush_Requirement";
		
		File newJavaPath = new File(newVerPath + "-java");
		newJavaPath.mkdirs();
		File oldJavaPath = new File(oldVerPath + "-java");
		oldJavaPath.mkdirs();
		
		//String normalNewPath = FilenameUtils.normalize(newVerPath);
		//String normalOldPath = FilenameUtils.normalize(oldVerPath);
		
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
