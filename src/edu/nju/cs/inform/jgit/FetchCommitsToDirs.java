package edu.nju.cs.inform.jgit;

import edu.nju.cs.inform.jgit.helper.CookbookHelper;
import edu.nju.cs.inform.util.AppConfigure;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevTree;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.treewalk.TreeWalk;

import java.io.File;

/**
 * Created by niejia on 16/5/16.
 */
public class FetchCommitsToDirs {

    public FetchCommitsToDirs(String gitProjectPath, String exportDirPath) throws Exception {

        AppConfigure.gitProjectPath = gitProjectPath;

        File gitProjectFile = new File(gitProjectPath);
        String projectName = gitProjectFile.getParentFile().getName();
        String dir = exportDirPath;

        try (Repository repository = CookbookHelper.openJGitCookbookRepository()) {
            // See e.g. GetRevCommitFromObjectId for how to use a SHA-1 directly
            Ref head = repository.findRef("HEAD");
            System.out.println("Ref of HEAD: " + head + ": " + head.getName() + " - " + head.getObjectId().getName());

            try (Git git = new Git(repository)) {
                Iterable<RevCommit> commits = git.log().all().call();
                int count = 0;
                for (RevCommit commit : commits) {
                    System.out.println("LogCommit: " + commit);

                    // a RevWalk allows to walk over commits based on some filtering that is defined
                    try (RevWalk walk = new RevWalk(repository)) {

//                        RevCommit commit = walk.parseCommit(head.getObjectId());
                        System.out.println("Commit: " + commit.getName() + " " + commit.getAuthorIdent() + " " + commit.getCommitTime());

                        String commitDir = dir + "/" + projectName + "/" + "commit_" + commit.getName() + "_" + commit.getCommitTime() + "_" + commit.getAuthorIdent().getName() + "/";

                        // Unix timestamp to dataTime
//                        Date time = new java.util.Date((long) commit.getCommitTime() * 1000);
//                        System.out.println(time);

                        // a commit points to a tree
                        RevTree tree = walk.parseTree(commit.getTree().getId());

                        TreeWalk treeWalk = new TreeWalk(repository);
                        treeWalk.addTree(tree);
                        treeWalk.setRecursive(false);

                        while (treeWalk.next()) {

                            if (treeWalk.isSubtree()) {
//                        System.out.println("found directory: " + treeWalk.getPathString());
                                treeWalk.enterSubtree();
                            } else {
//                        System.out.println("found file: " + treeWalk.getPathString());

                                File file = new File(commitDir + treeWalk.getPathString());
                                File dirFile = new File(file.getParentFile().getPath());

//                                System.out.println(dirFile.getAbsolutePath());

                                if (!dirFile.exists()) {
                                    dirFile.mkdirs();
                                }

                                if (!file.exists()) {
                                    file.createNewFile();
                                }

                                ObjectId objectId = treeWalk.getObjectId(0);
                            }
                        }
                        walk.dispose();
                    }

                    count++;
                }
            }
        }
    }
}
