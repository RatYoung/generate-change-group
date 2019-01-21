package edu.nju.cs.inform.io;

import edu.nju.cs.inform.core.type.Artifact;
import edu.nju.cs.inform.core.type.ArtifactsCollection;
import edu.nju.cs.inform.util._;

import java.io.File;

/**
 * Created by niejia on 15/2/10.
 */
public class ArtifactsReader {

    public static ArtifactsCollection getCollections(String dirPath, String postfixName) {

        File dirFile = new File(dirPath);
        if (!dirFile.exists()) {
            _.abort("Artifacts directory doesn't exist");
        }

        if (!dirFile.isDirectory()) {
            _.abort("Artifacts path should be a directory");
        }

        ArtifactsCollection collections = new ArtifactsCollection();
        for (File f : dirFile.listFiles()) {
            if (f.getName().endsWith(postfixName)) {
                String id = f.getName().split(postfixName)[0];
                Artifact artifact = new Artifact(id, _.readFile(f.getPath()));
                collections.put(id, artifact);
            }
        }

        return collections;
    }
}
