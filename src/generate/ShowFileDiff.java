package generate;

import java.io.File;

/*
   Copyright 2013, 2014 Dominik Stadler

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

     http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
 */

import java.io.IOException;
import java.util.List;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.diff.DiffFormatter;
import org.eclipse.jgit.internal.storage.file.FileRepository;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.ObjectReader;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevTree;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.treewalk.AbstractTreeIterator;
import org.eclipse.jgit.treewalk.CanonicalTreeParser;
import org.eclipse.jgit.treewalk.filter.PathFilter;
import org.eclipse.jgit.treewalk.filter.PathSuffixFilter;


/**
 * Simple snippet which shows how to show diffs between branches
 *
 * @author dominik.stadler at gmx.at
 */
public class ShowFileDiff {

    public static void main(String[] args) throws IOException, GitAPIException {
		File repoDir = new File("E:\\Desktop\\workspace\\创新项目：紧密度追踪过时需求\\project1006\\liquibase");
//		GitUtils.checkoutCommit(repoDir, "HEAD^");  //commit SHA-1 value  or  HEAD[^] or master or etc.
		File gitDir = new File(repoDir.getAbsolutePath() + "\\.git");
		FileRepository repo = new FileRepository(gitDir);
        // the diff works on TreeIterators, we prepare two for the two branches
		String SHA = "1b165ce5b52c0f763c5f35ec339ebd92765a62ba";
        AbstractTreeIterator oldTreeParser = prepareTreeParser(repo, SHA + "~1");
        AbstractTreeIterator newTreeParser = prepareTreeParser(repo, SHA);

        // then the porcelain diff-command returns a list of diff entries
        try (Git git = new Git(repo)) {
            List<DiffEntry> diff = git.diff().
                    setOldTree(oldTreeParser).
                    setNewTree(newTreeParser).
//                    setPathFilter(PathFilter.create("README.md")).
                    // to filter on Suffix use the following instead
                    setPathFilter(PathSuffixFilter.create(".java")).
                    call();
//            for (DiffEntry entry : diff) {
//                System.out.println("Entry: " + entry + ", from: " + entry.getOldId() + ", to: " + entry.getNewId());
//                try (DiffFormatter formatter = new DiffFormatter(System.out)) {
//                    formatter.setRepository(repo);
//                    formatter.format(entry);
//                }
//            }
            if(!diff.isEmpty()) {
            	System.out.println("diffs have *.java :-)");
            }else {
            	System.out.println("diffs don't have *.java :-(");
            }
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
}
