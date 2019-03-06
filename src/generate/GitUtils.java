package generate;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.*;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.internal.storage.file.FileRepository;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.ObjectReader;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevTree;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.treewalk.AbstractTreeIterator;
import org.eclipse.jgit.treewalk.CanonicalTreeParser;
import org.eclipse.jgit.treewalk.filter.PathSuffixFilter;

public class GitUtils {
	
	public static String currentBranch = null;
	
	public static void checkoutCommit(File repoDir, String commitName) throws IOException, RefAlreadyExistsException, RefNotFoundException, InvalidRefNameException, CheckoutConflictException, GitAPIException {
		File gitDir = new File(repoDir.getAbsolutePath() + "\\.git");
		FileRepository repo = new FileRepository(gitDir);
		Git git = new Git(repo);
		ObjectId id = repo.resolve(commitName);
		if(!commitName.equals(currentBranch)) {
			git.checkout().setName(id.name()).call();
			System.out.println("-------------Checkout successfully----------");
			System.out.println("Commit: " + commitName);
			System.out.println("SHA-1: " + id.name());
		}else
			git.checkout().setName(currentBranch).call();
		git.getRepository().close();
		git.close();
	}
	
	public static boolean hasJavaFiles(File repoDir, String commitName) throws IOException, GitAPIException {
		File gitDir = new File(repoDir.getAbsolutePath() + "\\.git");
		FileRepository repo = new FileRepository(gitDir);
        // the diff works on TreeIterators, we prepare two for the two branches
		String SHA = commitName;
        AbstractTreeIterator oldTreeParser = prepareTreeParser(repo, SHA + "~1");
        AbstractTreeIterator newTreeParser = prepareTreeParser(repo, SHA);

        // then the porcelain diff-command returns a list of diff entries
        Git git = new Git(repo);
        List<DiffEntry> diff = git.diff().
                setOldTree(oldTreeParser).
                setNewTree(newTreeParser).
                setPathFilter(PathSuffixFilter.create(".java")).
                call();
        repo.close();
        git.close();
        if(!diff.isEmpty()) {
        	return true;
        }else {
        	return false;
        }   
    }
	
	private static AbstractTreeIterator prepareTreeParser(Repository repository, String objectId) throws IOException {
        // from the commit we can build the tree which allows us to construct the TreeParser
        //noinspection Duplicates
    	ObjectId id = repository.resolve(objectId);
        try (RevWalk walk = new RevWalk(repository)) {
            RevCommit commit = walk.parseCommit(id);
            RevTree tree = walk.parseTree(commit.getTree().getId());

            CanonicalTreeParser treeParser = new CanonicalTreeParser();
            try (ObjectReader reader = repository.newObjectReader()) {
                treeParser.reset(reader, tree.getId());
            }
            walk.dispose();
            return treeParser;
        }
    }
	
	public static void main(String[] args) throws RefAlreadyExistsException, RefNotFoundException, InvalidRefNameException, CheckoutConflictException, IOException, GitAPIException {
//		File repoDir = new File("E:\\Desktop\\workspace\\创新项目：紧密度追踪过时需求\\测试用开源工程\\commons-io");
		File repoDir = new File("F:\\创新项目\\sample projects\\liquibase");
//		GitUtils.checkoutCommit(repoDir, "HEAD^");  //commit SHA-1 value  or  HEAD[^] or master or etc.
		File gitDir = new File(repoDir.getAbsolutePath() + "\\.git");
		FileRepository repo = new FileRepository(gitDir);
		Git git = new Git(repo);
		String sha = "1b165ce5b52c0f763c5f35ec339ebd92765a62ba";
	    ObjectId headId = git.getRepository().resolve(sha);
	    ObjectId oldId = git.getRepository().resolve(sha + "~1");
	    ObjectReader reader = git.getRepository().newObjectReader();
	     
	    CanonicalTreeParser oldTreeIter = new CanonicalTreeParser();
	    oldTreeIter.reset(reader, oldId);
	    CanonicalTreeParser newTreeIter = new CanonicalTreeParser();
	    newTreeIter.reset(reader, headId);
		List<DiffEntry> diffs = git.diff().setOldTree(oldTreeIter).setNewTree(newTreeIter).call();
		System.out.println(diffs);
		repo.close();
		git.close();
	}
}
