package edu.nju.cs.inform.core.ir;


import edu.nju.cs.inform.core.type.ArtifactsCollection;
import edu.nju.cs.inform.core.type.SimilarityMatrix;

/**
 * Created by niejia on 15/2/23.
 */
public interface IRModel {
    public SimilarityMatrix Compute(ArtifactsCollection sourceCollection, ArtifactsCollection targetCollection);
}
