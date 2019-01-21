package edu.nju.cs.inform.test.core.recommend;

import edu.nju.cs.inform.core.diff.CodeElementsComparer;
import edu.nju.cs.inform.core.ir.IRModelConst;
import edu.nju.cs.inform.core.ir.Retrieval;
import edu.nju.cs.inform.core.type.ArtifactsCollection;
import edu.nju.cs.inform.core.type.SimilarityMatrix;
import edu.nju.cs.inform.io.ArtifactsReader;
import edu.nju.cs.inform.core.recommend.MethodRecommendation;
import org.junit.Test;

import java.util.List;
import java.util.Map;

public class MethodRecommendationTest {

    @Test
    public void testTracing() throws Exception {
        String newVersionCodePath = "data/sample/AquaLush_Change4";
        String oldVersionCodePath = "data/sample/AquaLush_Change3";
        String requirementPath = "data/sample/AquaLush_Requirement";

        CodeElementsComparer comparer = new CodeElementsComparer(newVersionCodePath, oldVersionCodePath);
        comparer.diff();

        // get change description from code changes
        ArtifactsCollection changeDescriptionCollection = comparer.getChangeDescriptionCollection();
        ArtifactsCollection requirementCollection = ArtifactsReader.getCollections(requirementPath, ".txt");

        // retrieval change description to requirement
        Retrieval retrieval = new Retrieval(changeDescriptionCollection, requirementCollection, IRModelConst.VSM);
        retrieval.tracing();

        SimilarityMatrix similarityMatrix = retrieval.getSimilarityMatrix();
        Map<String, Double> candidatedOutdatedRequirementsRank = retrieval.getCandidateOutdatedRequirementsRank();

        System.out.println(similarityMatrix );
        System.out.println(candidatedOutdatedRequirementsRank);

        MethodRecommendation methodRecommendation = new MethodRecommendation(comparer, requirementCollection, similarityMatrix);
        Map<String, List<String>> recommendMethodsForRequirements = methodRecommendation.getRecommendMethodsForRequirements();

        // show method recommendation
        for (String req : recommendMethodsForRequirements.keySet()) {
            System.out.println(req);
            List<String> recommendList = recommendMethodsForRequirements.get(req);
            for (String method : recommendList) {
                System.out.println(method);
            }
        }
    }
}