package edu.nju.cs.inform.test.core.diff;

import edu.nju.cs.inform.core.diff.CodeElementsComparer;
import edu.nju.cs.inform.core.type.CodeElementChange;
import org.junit.Test;

import java.util.Set;

public class CodeElementsComparerTest {

    @Test
    public void testDiff() throws Exception {
        String newVersionCodePath = "data/sample/AquaLush_Change4";
        String oldVersionCodePath = "data/sample/AquaLush_Change3";

        CodeElementsComparer comparer = new CodeElementsComparer(newVersionCodePath, oldVersionCodePath);
        comparer.diff();

        System.out.println("-----------------Code Elements Diff-----------------");
        Set<CodeElementChange> codeElementChangeList = comparer.getCodeElementChangesList();
        for (CodeElementChange elementChange : codeElementChangeList) {
            System.out.println(elementChange.getElementName() + " " + elementChange.getElementType() + " " + elementChange.getChangeType());
        }
    }
}