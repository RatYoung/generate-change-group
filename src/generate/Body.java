package generate;

import generate.NewRetro;
import generate.GitUtils;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.eclipse.jgit.api.errors.*;
import org.eclipse.jgit.internal.storage.file.FileRepository;
import org.sqlite.SQLiteException;


public class Body {
	@SuppressWarnings("unused")
	public static void main(String[] args) throws IOException, RefAlreadyExistsException, RefNotFoundException, InvalidRefNameException, CheckoutConflictException, GitAPIException, SQLException, SQLiteException {
		//needless requirement path!
		String reqPath = "C:\\Users\\24541\\Desktop\\req-swing-demo\\data\\sample\\AquaLush_Requirement";
		
		Connection c = null;
		Statement stmt = null;
		ResultSet rs_names = null;
		try {
			Class.forName("org.sqlite.JDBC");
			c = DriverManager.getConnection("jdbc:sqlite:NMTdata-ultimate.sqlite3");
			c.setAutoCommit(true);
			//c.setAutoCommit(false);
			System.out.println("Opened database successfully");

			stmt = c.createStatement();
			rs_names = stmt.executeQuery("SELECT name FROM repositories");
			
			System.out.println("________Repositories loaded successfully_________");
		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			System.exit(0);
			
			stmt.close();
			//c.commit();
			c.close();
		}
		
		List<String> names = new ArrayList<>();
		while(rs_names.next()) {
			names.add(rs_names.getString("name"));
		}
		for(String name: names) {
			System.out.println(name);
			if (!name.equals("netty"))
				continue;
			
			NewRetro re = new NewRetro();
			
//			String localRepoPath = "F:\\创新项目\\sample projects\\"+name;
			String localRepoPath = "F:\\创新项目\\sample projects\\"+name;
			int repoNum = 0;
			ResultSet repoNumSet = stmt.executeQuery("SELECT repoNum FROM repositories WHERE name = '" + name + "'");
			if (repoNumSet.next()) {
				repoNum = repoNumSet.getInt("repoNum");
			}
			System.out.println("repoNum:--------" + repoNum);
			
			List<String> shas = new ArrayList<String>();
 			ResultSet shaSet_test = stmt.executeQuery("SELECT sha, note FROM test WHERE repoNum = " + repoNum);
			while(shaSet_test.next()) {
				String sha = shaSet_test.getString("sha");
				String note = shaSet_test.getString("note");
//				if(note != null) {		//if it has been parsed as "noJavaFiles" or <Exceptions>
//					System.out.println(sha + " done: " + note);
//					continue;
//				}
//				System.out.println(sha);
				shas.add(sha);
			}
			ResultSet shaSet_train = stmt.executeQuery("SELECT sha, note FROM train WHERE repoNum = " + repoNum);
			while(shaSet_train.next()) {
				String sha = shaSet_train.getString("sha");
				String note = shaSet_train.getString("note");
//				if(note != null) {		//if it has been parsed as "noJavaFiles" or <Exceptions>
//					System.out.println(sha + " done: " + note);
//					continue;
//				}
//				System.out.println(sha);
				shas.add(sha);
			}
			ResultSet shaSet_valid = stmt.executeQuery("SELECT sha, note FROM valid WHERE repoNum = " + repoNum);
			while(shaSet_valid.next()) {
				String sha = shaSet_valid.getString("sha");
				String note = shaSet_valid.getString("note");
//				if(note != null) {		//if it has been parsed as "noJavaFiles" or <Exceptions>
//					System.out.println(sha + " done: " + note);
//					continue;
//				}
//				System.out.println(sha);
				shas.add(sha);
			}
			
			for(String each : shas) {
				double t0 = System.currentTimeMillis();
				//String newCommit = "757f8ede8936402e576a16988f50994eda41f7a1";
				String newCommit = each;
				String rollBack = newCommit + "~1";
				Boolean isSaved = false;	
				File localVersion = new File(localRepoPath);
				
				boolean hasJavaFiles = GitUtils.hasJavaFiles(localVersion, newCommit);
				if(!hasJavaFiles) {
					System.out.println("-------------Diffs DON'T contain *.java files----------");
					System.out.println("Commit: " + newCommit);
					double t1 = System.currentTimeMillis();
					System.out.println("Finished.\nTime used: " + ((t1 - t0) / 1000) + "s");
					System.out.println("UPDATE _ SET note = 'noJavaFiles' WHERE sha = '"+each+"' AND repoNum = "+repoNum);
					stmt.executeUpdate("UPDATE test SET note = 'noJavaFiles' WHERE sha = '"+each+"' AND repoNum = "+repoNum);
					stmt.executeUpdate("UPDATE train SET note = 'noJavaFiles' WHERE sha = '"+each+"' AND repoNum = "+repoNum);
					stmt.executeUpdate("UPDATE valid SET note = 'noJavaFiles' WHERE sha = '"+each+"' AND repoNum = "+repoNum);
					//c.commit();
					continue;
				}
			
				//get current ObjectId in order to checkout back
				FileRepository repo = new FileRepository(localRepoPath + "\\.git");
				String currentBranch = repo.getBranch();
				GitUtils.currentBranch = currentBranch;
				repo.close();
				
				//mkdirs for java files
				boolean isOldExisted = true;
				boolean isNewExisted = true;
				File newJavaPath = new File(localRepoPath+"-"+newCommit+"-java");
				if(!newJavaPath.exists()) {
					isNewExisted = false;
					newJavaPath.mkdirs();
				}
				File oldJavaPath = new File(localRepoPath+"-"+rollBack+"-java");
				if(!oldJavaPath.exists()) {
					isOldExisted = false;
					oldJavaPath.mkdirs();	
				}
				
				//get older version
				if(!isOldExisted) {
					GitUtils.checkoutCommit(localVersion, rollBack);
					Collection<File> oldListFiles = FileUtils.listFiles(localVersion, FileFilterUtils.suffixFileFilter("java"), DirectoryFileFilter.INSTANCE);
					for (File file : oldListFiles) {
						FileUtils.copyFileToDirectory(file, oldJavaPath);
					}
					GitUtils.checkoutCommit(localVersion, currentBranch);
				}
				if(!isNewExisted) {
					GitUtils.checkoutCommit(localVersion, newCommit);
					Collection<File> newListFiles = FileUtils.listFiles(localVersion, FileFilterUtils.suffixFileFilter("java"), DirectoryFileFilter.INSTANCE);
					for (File file : newListFiles) {
						FileUtils.copyFileToDirectory(file, newJavaPath);
					}
					GitUtils.checkoutCommit(localVersion, currentBranch);
				}
				
				System.out.println("checkout & shift *.java finished.");
				System.out.println("-------------Generating ChangeGroups----------");
				try {
					re.process(newJavaPath.getAbsolutePath(), oldJavaPath.getAbsolutePath(), reqPath, isSaved);
				}catch(Exception e) {
					System.out.println(e.getClass().getName());
					System.out.println("UPDATE _ SET note = '"+e.getClass().getName()+"' WHERE sha = '"+each+"' AND repoNum = "+repoNum);
					stmt.executeUpdate("UPDATE test SET note = '"+e.getClass().getName()+"' WHERE sha = '"+each+"' AND repoNum = "+repoNum);
					stmt.executeUpdate("UPDATE train SET note = '"+e.getClass().getName()+"' WHERE sha = '"+each+"' AND repoNum = "+repoNum);
					stmt.executeUpdate("UPDATE valid SET note = '"+e.getClass().getName()+"' WHERE sha = '"+each+"' AND repoNum = "+repoNum);
					//c.commit();
					if (!isSaved) {
						Boolean deleteNewJava = FileUtils.deleteQuietly(newJavaPath);
						Boolean deleteOldJava = FileUtils.deleteQuietly(oldJavaPath);
					}
					continue;
				}
				if (!isSaved) {
					Boolean deleteNewJava = FileUtils.deleteQuietly(newJavaPath);
					Boolean deleteOldJava = FileUtils.deleteQuietly(oldJavaPath);
				}
				System.out.println("UPDATE _ SET note = 'OK' WHERE sha = '"+each+"' AND repoNum = "+repoNum);
				stmt.executeUpdate("UPDATE test SET note = 'OK' WHERE sha = '"+each+"' AND repoNum = "+repoNum);
				stmt.executeUpdate("UPDATE train SET note = 'OK' WHERE sha = '"+each+"' AND repoNum = "+repoNum);
				stmt.executeUpdate("UPDATE valid SET note = 'OK' WHERE sha = '"+each+"' AND repoNum = "+repoNum);
				//c.commit();
				double t1 = System.currentTimeMillis();
				System.out.println("Finished.\nTime used: " + ((t1 - t0) / 1000) + "s");
			}
		}
		stmt.close();
		c.close();
	}
}
