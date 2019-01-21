package edu.nju.cs.inform.test.core.group;

import edu.nju.cs.inform.core.diff.CodeElementsComparer;
import org.junit.Test;

public class ChangeRegionAlgorithmTest {

    @Test
    public void testDiff() throws Exception {
        String newVersionCodePath = "data/sample/AquaLush_Change4";
        String oldVersionCodePath = "data/sample/AquaLush_Change3";
        String newVersionJarPath = "data/sample/jar/change4.jar";
        String oldVersionJarPath = "data/sample/jar/change3.jar";

        CodeElementsComparer comparer = new CodeElementsComparer(newVersionCodePath, oldVersionCodePath);
        comparer.diff();


    }
}