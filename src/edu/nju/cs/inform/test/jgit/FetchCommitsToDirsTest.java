package edu.nju.cs.inform.test.jgit;


import edu.nju.cs.inform.jgit.FetchCommitsToDirs;
import org.junit.Test;

public class FetchCommitsToDirsTest {

    @Test
    public void testContentInCommit() throws Exception {

        String gitProjectPath = "data/sample/AquaLush/.git";
        String exportPath = "data/project/";

        FetchCommitsToDirs fetchCommitsToDirs = new FetchCommitsToDirs(gitProjectPath, exportPath);
    }
}
