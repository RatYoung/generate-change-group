package edu.nju.cs.inform.core.group;

import edu.nju.cs.inform.core.preprocess.ArtifactPreprocessor;
import edu.nju.cs.inform.util.JavaElement;

import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by niejia on 16/2/26.
 */
public class ExportInitialRegion {

    Map<String, String> initialRegionCorpus;

    public ExportInitialRegion(Map<String, HashSet<String>> changeReigon) {
        initialRegionCorpus = new LinkedHashMap<>();

        int regionNum = 1;
        for (String changedMethod : changeReigon.keySet()) {
            HashSet<String> region = changeReigon.get(changedMethod);
            StringBuilder sb = new StringBuilder();
            for (String element : region) {
                sb.append(JavaElement.getIdentifier(JavaElement.getClassName(element)));
                sb.append(" ");
                sb.append(JavaElement.getIdentifier(element));
                sb.append(" ");
            }

            initialRegionCorpus.put("Group" + regionNum, ArtifactPreprocessor.handleJavaFile(sb.toString()));
//            _.writeFile(ArtifactPreprocessor.handleJavaFile(sb.toString()), outputPath + "/Group" + regionNum + ".txt");
            regionNum++;
        }
    }

    public Map<String, String> getInitialRegionCorpus() {
        return initialRegionCorpus;
    }
}
