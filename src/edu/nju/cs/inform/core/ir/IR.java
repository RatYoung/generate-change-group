package edu.nju.cs.inform.core.ir;

import edu.nju.cs.inform.core.type.ArtifactsCollection;
import edu.nju.cs.inform.core.type.SimilarityMatrix;

/**
 * Created by niejia on 15/2/23.
 */
public class IR {

    public static SimilarityMatrix compute(ArtifactsCollection sourceCollection, ArtifactsCollection targetCollection, String modelType) {
        SimilarityMatrix similarityMatrix = null;
        try {
            Class modelTypeClass = Class.forName(modelType);
            IRModel irModel = (IRModel) modelTypeClass.newInstance();
            similarityMatrix = irModel.Compute(sourceCollection, targetCollection);

            return similarityMatrix;

        } catch (ClassNotFoundException e) {
            System.out.println("No such IR model exists");
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return similarityMatrix;
    }
}
