package generate;

import generate.NewRetro;
import generate.GitUtils;
import generate.ReadSql;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.*;
import org.eclipse.jgit.internal.storage.file.FileRepository;
import org.sqlite.SQLiteException;

@SuppressWarnings("unused")
public class HistoryParser {
	
	public static void main(String[] args) throws IOException, RefAlreadyExistsException, RefNotFoundException, InvalidRefNameException, CheckoutConflictException, GitAPIException {
		System.out.println(now() + "Job started.");
		
		List<String> hashes = getHashListFromFile();
//		List<String> hashes = getHashListFromDB();
//		int i = 0;
//		for(String hash: hashes) {
//			System.out.println(hash);
//			++i;
//			if(i == 20)
//				break;
//		}
		
		String currentRepoPath = "\\Desktop\\workspace\\创新项目：紧密度追踪过时需求\\mavenHistory\\mavenA";
		String previousRepoPath = "\\Desktop\\workspace\\创新项目：紧密度追踪过时需求\\mavenHistory\\mavenB";
		File currentRepo = new File(currentRepoPath);
		File previousRepo = new File(previousRepoPath);
//		System.out.println(previousRepo.getAbsolutePath());
//		System.out.println(previousRepo.isAbsolute());
//		Git currentGit = new Git(currentRepo);
//		Git previousGit = new Git(previousRepo);
		
		int oldestIndex = hashes.size();
		
		FileWriter fileWriter = new FileWriter("E:\\Desktop\\workspace\\创新项目：紧密度追踪过时需求\\mavenHistory\\logs.txt");
		fileWriter.write("");
		fileWriter.close();
			
		for(int i = 0; i < oldestIndex; ++i) {
			double t0 = System.currentTimeMillis();
			String hash = hashes.get(i);
			System.out.println(now() + "Checking[" + i + "]: " + hash);
			
			boolean hasJavaFiles = GitUtils.hasJavaFiles(currentRepo, hash);
			if(!hasJavaFiles) {
				String logName = "E:\\Desktop\\workspace\\创新项目：紧密度追踪过时需求\\mavenHistory\\logs.txt";
				FileWriter logger = new FileWriter(logName, true);
				logger.write(hash + ": no java files\r\n");
				logger.close();
				double t1 = System.currentTimeMillis();
				System.out.println(now() + "Commit: " + hash + ": diffs DON'T contain *.java files\t" + timeCost(t0, t1));
				System.out.println("--------------------------------------");
				continue;
			}
			
			GitUtils.checkoutCommit(previousRepo, hash + "~1");
			GitUtils.checkoutCommit(currentRepo, hash);
			
			Collection<File> previousListFiles = FileUtils.listFiles(previousRepo, FileFilterUtils.suffixFileFilter("java"), DirectoryFileFilter.INSTANCE);
			Collection<File> currentListFiles = FileUtils.listFiles(currentRepo, FileFilterUtils.suffixFileFilter("java"), DirectoryFileFilter.INSTANCE);
			
			File previousJava = new File(previousRepo.getPath() + "-" + hash + "~1-java");
			File currentJava = new File(currentRepo.getPath() + "-" + hash + "-java");
			previousJava.mkdirs();
			currentJava.mkdirs();
			
			for (File file : previousListFiles) 
				FileUtils.copyFileToDirectory(file, previousJava);
			for (File file : currentListFiles)
				FileUtils.copyFileToDirectory(file, currentJava);
			
			System.out.println(now() +"Generating ChangeGroups");
			NewRetro re = new NewRetro();
			try {
				
				re.process(currentJava.getAbsolutePath(), previousJava.getAbsolutePath(), "", false);
				
			}
			catch(Exception e) {
				String logName = "E:\\Desktop\\workspace\\创新项目：紧密度追踪过时需求\\mavenHistory\\logs.txt";
				FileWriter logger = new FileWriter(logName, true);
				logger.write(hash + ": " + e.getClass().getName() + "\r\n");
				logger.close();
				double t1 = System.currentTimeMillis();
				Boolean deleteNewJava = FileUtils.deleteQuietly(previousJava);
				Boolean deleteOldJava = FileUtils.deleteQuietly(currentJava);
				System.out.println(now() + "Commit: " + hash + ": " + e.getClass().getName() + "\t" + timeCost(t0, t1));
				System.out.println("--------------------------------------");
				continue;
			}
			
			ObjectOutputStream oos = new ObjectOutputStream(
					new FileOutputStream("E:\\Desktop\\workspace\\创新项目：紧密度追踪过时需求\\mavenHistory\\Retros\\" + hash + ".retro"));
            oos.writeObject(re);
            oos.close();
			
			double t1 = System.currentTimeMillis();
			System.out.println(now() + "Commit: " + hash + ": finished!\t" + timeCost(t0, t1));
			System.out.println("--------------------------------------");
//			System.out.println(previousJava.getPath());
			Boolean deleteNewJava = FileUtils.deleteQuietly(previousJava);
			Boolean deleteOldJava = FileUtils.deleteQuietly(currentJava);
		}
		
		System.out.println(now() + "Job finished.");
	}
	
	public static List<String> getHashListFromFile() throws IOException{
		double t0 = System.currentTimeMillis();
		File file = new File("commit_hash.txt");
		List<String> hashes = FileUtils.readLines(file, "unicode");
		double t1 = System.currentTimeMillis();
		System.out.println(now() + "commit_hash collected." + "\t" + timeCost(t0, t1));
		return hashes;
	}
	
	public static List<String> getHashListFromDB(){
		Connection c = null;
		Statement stmt = null;
		try {
			double t0 = System.currentTimeMillis();
			Class.forName("org.sqlite.JDBC");
			c = DriverManager.getConnection("jdbc:sqlite:maven.sqlite3");
			c.setAutoCommit(false);
			System.out.println(now() + "Opened database successfully");

			stmt = c.createStatement();
			
			ResultSet rs = stmt.executeQuery("SELECT commit_hash FROM diff_to_process");

			List<String> hashes = new ArrayList<>();
			while(rs.next()) {
				hashes.add(rs.getString(1));
			}
			
			stmt.close();
			c.close();
			
			double t1 = System.currentTimeMillis();
			System.out.println(now() + "commit_hash collected." + "\t" + timeCost(t0, t1));
			return hashes;
			
		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			System.exit(0);
			return null;
		}
	}
	
	public static String now() {
		SimpleDateFormat sdf = new SimpleDateFormat();// 格式化时间 
//        sdf.applyPattern("yyyy-MM-dd HH:mm:ss a");// a为am/pm的标记
		sdf.applyPattern("HH:mm:ss");// a为am/pm的标记  
        Date date = new Date();// 获取当前时间  
		return "[" + sdf.format(date) + "] ";
	}
	
	public static String timeCost(double t0, double t1) {
		return "time used: " + ((t1 - t0) / 1000) + "s";
	}
}
