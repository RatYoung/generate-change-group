package generate;

import generate.NewRetro;
import generate.GitUtils;

import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.eclipse.jgit.api.errors.*;
import org.eclipse.jgit.internal.storage.file.FileRepository;
import org.sqlite.SQLiteException;

public class NewProcessBody {
	public static List<HashSet<String>> group;
	public static Map<String, HashSet<String>> changeRegions;
	private static ArrayList<Set<String>> finalRegionsList;
	
	@SuppressWarnings("unused")
	public static void main(String[] args) throws IOException, RefAlreadyExistsException, RefNotFoundException, InvalidRefNameException, CheckoutConflictException, GitAPIException, SQLException, SQLiteException {
		//needless requirement path!
		String reqPath = "C:\\Users\\24541\\Desktop\\req-swing-demo\\data\\sample\\AquaLush_Requirement";
		
		Connection c = null;
		Statement stmt = null;
		ResultSet rs_versions = null;
		try {
			Class.forName("org.sqlite.JDBC");
			c = DriverManager.getConnection("jdbc:sqlite:netty.sqlite3");
			c.setAutoCommit(true);
			System.out.println("Opened database successfully");

			stmt = c.createStatement();
			rs_versions = stmt.executeQuery("SELECT version FROM total_data");
			
			System.out.println("________Repositories loaded successfully_________");
		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			System.exit(0);
			
			stmt.close();
			c.close();
		}
		
		List<String> versions = new ArrayList<>();
		while(rs_versions.next()) {
			versions.add(rs_versions.getString("version"));
			//System.out.println(rs_versions.getString("version"));
		}
		//System.out.println(versions.size());
		
		stmt.executeUpdate("create table version_region(version varchar(20), region varchar(100));");
		
		NewRetro re = new NewRetro();
		
		for(String each: versions) {
			if(versions.indexOf(each) == versions.size()-1)
				break;
			String localRepoPath = "F:\\创新项目\\sample projects\\netty";
			String newCommit = each;
			String rollBack = newCommit + "~1";
			Boolean isSaved = false;	
			File localVersion = new File(localRepoPath);
			
			boolean hasJavaFiles = GitUtils.hasJavaFiles(localVersion, newCommit);
			if(!hasJavaFiles)
				continue;
			
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
				group = re.group;
				changeRegions = re.changeRegions;
				finalRegionsList = new ArrayList<Set<String>>();
				for (Set<String> changedGroup : group) {
		            Set<String> region = new LinkedHashSet<>();
		            for (String changedArtifact : changedGroup) {
		                Set<String> regionForArtifact = changeRegions.get(changedArtifact);
		                if (regionForArtifact != null) {
		                    for (String v : regionForArtifact) {
		                        region.add(v);
		                    }
		                } else {
		                    region.add(changedArtifact);
		                }
		            }
		            finalRegionsList.add(region);
		        }
				
				String total_region = "";
				int i = 1;
		        for (Set<String> region : finalRegionsList) {
		            //System.out.println("Region " + i + ":");
		            total_region = total_region + "Region " + i;
		            int j = 1;
		            for (String s : region) {
		                //System.out.println(j+": "+s);
		            	total_region = total_region + j+": "+s + "\n";
		                j++;
		            }
		            i++;
		        }
		        
		        if(i > 2) {
		        	String sql = "insert into version_region values('" + each + "','" + total_region + "');";
		        	stmt.executeUpdate(sql);
		        }
		        
			}catch(Exception e) {
				e.printStackTrace();
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
		}
		stmt.close();
		c.close();
	}
}
