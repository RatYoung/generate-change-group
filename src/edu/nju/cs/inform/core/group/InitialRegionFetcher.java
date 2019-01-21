package edu.nju.cs.inform.core.group;

import edu.nju.cs.inform.io.ChangedArtifacts;
import edu.nju.cs.inform.core.relation.CallRelationGraph;
import edu.nju.cs.inform.core.relation.graph.CodeVertex;

import java.util.*;

/**
 * Created by niejia on 16/1/3.
 */
public class InitialRegionFetcher {
    private ChangedArtifacts changedArtifacts;

    private CallRelationGraph newCallGraph;
    private CallRelationGraph oldCallGraph;

    private Map<String, HashSet<String>> regionForArtifactsList;

    public InitialRegionFetcher(ChangedArtifacts changedArtifacts, CallRelationGraph newCallGraph, CallRelationGraph oldCallGraph) {
        this.changedArtifacts = changedArtifacts;

        this.newCallGraph = newCallGraph;
        this.oldCallGraph = oldCallGraph;
        this.regionForArtifactsList = new LinkedHashMap<>();

        findInitialRegion();

    }

    private void findInitialRegion() {
        findInitialRegionForEachVertex(changedArtifacts.getAddedMethodsList(), changedArtifacts.getAddedFieldsList(),newCallGraph);
        findInitialRegionForEachVertex(changedArtifacts.getRemovedMethodsList(), changedArtifacts.getRemovedFieldsList(), oldCallGraph);

    }

    private void findInitialRegionForEachVertex(Set<String> vertexesForMethod, Set<String> vertexesForField, CallRelationGraph callRelationGraph) {
        for (String vertexName : vertexesForMethod) {
            List<CodeVertex> subGraphVertexes = new ArrayList<>();
            callRelationGraph.searhNeighbourConnectedGraphByCall(vertexName, subGraphVertexes);

            HashSet<String> region = new HashSet<>();
            region.add(vertexName);
            for (CodeVertex v : subGraphVertexes) {
                region.add(v.getName());
            }
            regionForArtifactsList.put(vertexName, region);
        }
//
//        for (String vertexName : vertexesForField) {
//            HashSet<String> region = new HashSet<>();
//            region.add(vertexName);
//            regionForArtifactsList.put(vertexName, region);
//        }
    }

    public Map<String, HashSet<String>> getChangeRegion() {
      return regionForArtifactsList;
    }
}
