package commitFinder;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.errors.IncorrectObjectTypeException;
import org.eclipse.jgit.errors.MissingObjectException;
import org.eclipse.jgit.internal.storage.file.FileRepository;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;

import commitFinder.CertainMsg;

public class CommitHashset {
	HashMap<String, String> map = new HashMap<>();
	File repoDir = null;
	File gitDir = null;
	
	public CommitHashset(String repoPath) throws IOException {
		repoDir = new File(repoPath);
		gitDir = new File(repoDir.getAbsolutePath() + "\\.git");
		FileRepository repo = new FileRepository(gitDir);
		Git git = new Git(repo);
		RevWalk revWalk = new RevWalk(repo);
		ObjectId commitId = repo.resolve(repo.getBranch());
		revWalk.markStart(revWalk.parseCommit(commitId));
		for(RevCommit commit : revWalk) {
			String commitCleaned = commit.getFullMessage().trim().replaceAll("(?m)^\\s*$(\\n|\\r\\n)", "").replaceAll("\\r", "");
			String SHA1 = commit.getId().getName();
			map.put(commitCleaned, SHA1);
//			map.put(finalContent, SHA1);
		}
		revWalk.close();
		repo.close();
		git.close();
	}
	
	public static void main(String[] args) throws MissingObjectException, IncorrectObjectTypeException, IOException {
		String repoPath = "E:\\Desktop\\workspace\\创新项目：紧密度追踪过时需求\\project1006\\react-native";
		String SHA1 = "ade9645ae8d18e335cf7221b79d830";
		
		File repoDir = new File(repoPath);
		File gitDir = new File(repoDir.getAbsolutePath() + "\\.git");
		FileRepository repo = new FileRepository(gitDir);
		Git git = new Git(repo);
		RevWalk revWalk = new RevWalk(repo);
		ObjectId commitId = repo.resolve(SHA1);
		String commitCleaned = revWalk.parseCommit(commitId).getFullMessage().trim().replaceAll("(?m)^\\s*$(\\n|\\r\\n)", "").replaceAll("\\r", "");
		String[] lines = commitCleaned.split("\n");
		String finalContent = "";
		for(String line: lines) {
			line = line.trim();
			finalContent += line + "\n";
		}
		System.out.println("------------------- SHA1: " + commitId.getName() + " ---------------------");
		System.out.println(finalContent);
		revWalk.close();
		repo.close();
		git.close();
		
		String msgPath = "E:\\Desktop\\workspace\\创新项目：紧密度追踪过时需求\\Commitgen Public\\commitmsgs\\repo1\\5095.msg";
		String content = CertainMsg.get(msgPath);
		System.out.println("------------------- Certain msg: " + new File(msgPath).getName() + " ---------------------");
		System.out.println(content);
		
		System.out.println(content.equals(finalContent) ? "same" : "not same");
	}
}
