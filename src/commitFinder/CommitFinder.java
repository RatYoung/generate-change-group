package commitFinder;

import commitFinder.CertainMsg;
import commitFinder.CommitHashset;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.io.FileUtils;


public class CommitFinder {
	public CommitHashset commitHashset = null;
//	public HashMap<String, String> map = null;
	public String writeBuffer = "";
	
	public CommitFinder() {}
	
	public void commitHashsetInitialize(String repoPath) throws IOException {
		writeBuffer = "";
		commitHashset = new CommitHashset(repoPath);
	}
	
	public String findMsg(String msgFilePath) throws IOException {
		String certainMsgContent = CertainMsg.get(msgFilePath);
		if(certainMsgContent.startsWith("Merge"))
			return "MergeCommits";
		boolean isFound = commitHashset.map.containsKey(certainMsgContent);
		if(isFound) {
			String SHA1 = commitHashset.map.get(certainMsgContent);
			return SHA1;
		}else {
			return null;
		}
	}
	
	public void findRepo(String srcRepoPath, String dstRepoPath) throws IOException {
		commitHashsetInitialize(dstRepoPath);
		File srcRepoDir = new File(srcRepoPath);
		String repoNo = srcRepoDir.getName();
		File[] files = srcRepoDir.listFiles();
		int sizeCnt = 0;
		int notFountCnt = 0;
		for(File file: files) {
			++sizeCnt;
			String msgFilePath = file.getAbsolutePath(); 
			String SHA1 = findMsg(msgFilePath);
			if(SHA1 != null) {
				writeBuffer += repoNo + "\t" + file.getName() + "\t" + SHA1 + "\n";
			}else {
				writeBuffer += repoNo + "\t" + file.getName() + "\t" + "NotFound\n";
				++notFountCnt;
			}
		}
		System.out.println("notFound: " + notFountCnt + "/" + sizeCnt);
	}
	
	@SuppressWarnings("deprecation")
	public static void main(String[] args) throws IOException {
		CommitFinder commitFinder = new CommitFinder();
		String srcRepoRootPath = "E:\\Desktop\\workspace\\创新项目：紧密度追踪过时需求\\Commitgen Public\\commitmsgs";
		String dstRepoRootPath = "E:\\Desktop\\workspace\\创新项目：紧密度追踪过时需求\\project1006";
		String outputResPath = "E:\\Desktop\\workspace\\创新项目：紧密度追踪过时需求\\mappingRes";
		
		
		String repoNo2repoNameMappingTxt = "mapping1000.txt";
		File repoNo2repoName = new File(dstRepoRootPath + "\\" +repoNo2repoNameMappingTxt);
		List<String> lines = FileUtils.readLines(repoNo2repoName, "utf-8");
		for(String line: lines) {
			if(line.startsWith("#")) {
				System.out.println("skipped due to repo doesn't exist.");
				continue;
			}
			String[] splitArr = line.split("\t");
			int num = Integer.parseInt(splitArr[0]);
			String repoName = splitArr[1];
			String srcRepoPath = srcRepoRootPath + "\\repo" + num;
			String dstRepoPath = dstRepoRootPath + "\\" + repoName;
			File outputRes = new File(outputResPath + "\\repo" + num + ".mapping");
			if(outputRes.exists()) {
				//comment this if-branch if you wanna refresh the result.
				System.out.println("repo" + num + " finished");
				continue;
			}
			if(!new File(dstRepoPath).exists()) {
				System.out.println("repo" + num + " not exists");
				continue;
			}
			commitFinder.findRepo(srcRepoPath, dstRepoPath);
			FileUtils.writeStringToFile(outputRes, commitFinder.writeBuffer);
			System.out.println("repo" + num + " finished");
		}
		
//		String srcRepoPath = "E:\\Desktop\\workspace\\创新项目：紧密度追踪过时需求\\Commitgen Public\\commitmsgs\\repo5";
//		String dstRepoPath = "E:\\Desktop\\workspace\\创新项目：紧密度追踪过时需求\\project1006\\okhttp";
//		commitFinder.findRepo(srcRepoPath, dstRepoPath);
		System.out.println();
		System.out.println("Finished");
//		System.out.println(new File(dstRepoPath).getName());
	}
}
