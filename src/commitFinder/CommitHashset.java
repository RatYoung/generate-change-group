package commitFinder;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.NoHeadException;
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
	
	public CommitHashset(String repoPath) throws IOException, GitAPIException {
		repoDir = new File(repoPath);
		gitDir = new File(repoDir.getAbsolutePath() + "\\.git");
		FileRepository repo = new FileRepository(gitDir);
		Git git = new Git(repo);
		RevWalk revWalk = new RevWalk(repo);
//		Iterable<RevCommit> iterable = git.log().call();
		
		ObjectId commitId = repo.resolve(repo.getBranch());
//		ObjectId commitId = repo.resolve("a6bdfb92d2d4852f5ab5ee7f3c7a3f37795fede1"); //断层
		revWalk.markStart(revWalk.parseCommit(commitId));
		for(RevCommit commit : revWalk) {
//			String commitCleaned = commit.getFullMessage().trim().replaceAll("(?m)^\\s*$(\\n|\\r\\n)", "").replaceAll("\\r", "");
			String commitCleaned = commit.getFullMessage().trim();
			String SHA1 = commit.getId().getName();
			map.put(commitCleaned, SHA1);
//			map.put(finalContent, SHA1);
		}
		revWalk.close();
		repo.close();
		git.close();
	}
	
	public static void main(String[] args) throws MissingObjectException, IncorrectObjectTypeException, IOException {
		String repoPath = "E:\\Desktop\\workspace\\创新项目：紧密度追踪过时需求\\project1006\\ZhihuDailyPurify";
		String SHA1 = "388b4587b857fcafbbedaebf265a8b10eb8ff203";
		
		File repoDir = new File(repoPath);
		File gitDir = new File(repoDir.getAbsolutePath() + "\\.git");
		FileRepository repo = new FileRepository(gitDir);
		Git git = new Git(repo);
		RevWalk revWalk = new RevWalk(repo);
		ObjectId commitId = repo.resolve(SHA1);
		String commitCleaned = revWalk.parseCommit(commitId).getFullMessage().trim().replaceAll("(?m)^\\*$(\\n|\\r\\n)", "").replaceAll("\\r", "");
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
		
		String msgPath = "E:\\Desktop\\workspace\\创新项目：紧密度追踪过时需求\\Commitgen Public\\commitmsgs\\repo212\\896935.msg";
		String content = CertainMsg.get(msgPath);
		System.out.println("------------------- Certain msg: " + new File(msgPath).getName() + " ---------------------");
		System.out.println(content);
		
//		System.out.println(content.equals(finalContent) ? "same" : "not same");
		System.out.println(content.contains(finalContent) || finalContent.contains(content) ? "same" : "not same");
	}
}
