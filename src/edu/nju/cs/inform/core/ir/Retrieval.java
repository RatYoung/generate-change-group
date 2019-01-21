package edu.nju.cs.inform.core.ir;

import edu.nju.cs.inform.core.diff.CodeElementsComparer;
import edu.nju.cs.inform.core.preprocess.ArtifactPreprocessor;
import edu.nju.cs.inform.core.type.*;
import edu.nju.cs.inform.util._;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by niejia on 16/3/24.
 */
public class Retrieval {

    private ArtifactsCollection sourceCollection;
    private ArtifactsCollection targetCollection;
    private String modelType;

    private SimilarityMatrix sm;
    private Map<String, Double> candidateOutdatedRequirementsRank;

    public Retrieval(ArtifactsCollection sourceCollection, ArtifactsCollection targetCollection, String modelType) {
        this.sourceCollection = deepCopy(sourceCollection);
        this.targetCollection = deepCopy(targetCollection);
        this.modelType = modelType;
        this.candidateOutdatedRequirementsRank = new LinkedHashMap<>();
    }

    public Retrieval(CodeElementsComparer codeElementsComparer, ArtifactsCollection targetCollection, String modelType) {
        this.sourceCollection = deepCopy(codeElementsComparer.getChangeDescriptionCollection());
        this.targetCollection = deepCopy(targetCollection);
        this.modelType = modelType;
        this.candidateOutdatedRequirementsRank = new LinkedHashMap<>();


    }

    public void tracing() {
        preprocessCode(sourceCollection);
        preprocessRequirement(targetCollection);
        computeSimilarity();

        // to generate a single rank from matrix
        generateFinalRank();
    }

     public void generateFinalRank() {

        LinksList allLinks = sm.getLinksAboveThreshold();
        for (SingleLink link : allLinks) {
            String req = link.getTargetArtifactId();
            if (candidateOutdatedRequirementsRank.containsKey(req)) {
                double currentScore = candidateOutdatedRequirementsRank.get(req);
                currentScore += link.getScore();
                candidateOutdatedRequirementsRank.put(req, currentScore);
            } else {
                candidateOutdatedRequirementsRank.put(req, link.getScore());
            }
        }

        _.sortValueByDescending(candidateOutdatedRequirementsRank);
    }

    public Map<String, Double> getCandidateOutdatedRequirementsRank() {
        return candidateOutdatedRequirementsRank;
    }

    private void computeSimilarity() {
        sm = IR.compute(sourceCollection, targetCollection, modelType);
    }

    private void preprocessCode(ArtifactsCollection codeCollection) {
        for (String change : codeCollection.keySet()) {
            Artifact artifact = codeCollection.get(change);
            artifact.text = ArtifactPreprocessor.handleJavaFile(artifact.text);
        }
    }

    private void preprocessRequirement(ArtifactsCollection reqCollection) {
        for (String requirement : reqCollection.keySet()) {
            Artifact artifact = reqCollection.get(requirement);
            artifact.text = ArtifactPreprocessor.handlePureTextFile(artifact.text);
        }
    }

    public SimilarityMatrix getSimilarityMatrix() {
        return sm;
    }

    private ArtifactsCollection deepCopy(ArtifactsCollection originCollection) {
        ArtifactsCollection collections = new ArtifactsCollection();
        for (String id : originCollection.keySet()) {
            Artifact artifact = new Artifact(id, originCollection.get(id).text);
            collections.put(id, artifact);
        }
        return collections;
    }
}
