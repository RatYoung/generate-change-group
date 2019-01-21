package edu.nju.cs.inform.test.core.relation;

import org.junit.Test;

public class RelationInfoTest {


    @Test
    public void testDiff() throws Exception {
//        String newVersionCodePath = "data/sample/AquaLush_Change4";
//        String oldVersionCodePath = "data/sample/AquaLush_Change3";
        String newVersionJarPath = "data/sample/jar/change4.jar";
        String oldVersionJarPath = "data/sample/jar/change3.jar";
        String changePath = "data/sample/changes_change4_change3.txt";

//        RelationInfo relationInfoForChangedPart = new RelationInfo(newVersionCodePath, oldVersionCodePath, changePath, false);
//        RelationInfo relationInfoForChangedPart = new RelationInfo(newVersionJarPath, oldVersionJarPath, changePath, false);
//        CallRelationGraph callGraphForChangedPart = new CallRelationGraph(relationInfoForChangedPart);
//
//        double thresholdForInitialRegion = 0.35;
//        RelationInfo oldRelationInfo = new RelationInfo(newVersionJarPath,false);
//        oldRelationInfo.setPruning(thresholdForInitialRegion);
//        RelationInfo newRelationInfo = new RelationInfo(oldVersionJarPath,false);
//        newRelationInfo.setPruning(thresholdForInitialRegion);
//
//        CallRelationGraph oldCallGraph = new CallRelationGraph(oldRelationInfo);
//        CallRelationGraph newCallGraph = new CallRelationGraph(newRelationInfo);
//
//        ChangedArtifacts changedArtifacts = new ChangedArtifacts();
//        changedArtifacts.parse(changePath);
//
//        InitialRegionFetcher fetcher = new InitialRegionFetcher(changedArtifacts, newCallGraph, oldCallGraph);
//
//        ExportInitialRegion exporter = new ExportInitialRegion(fetcher.getChangeRegion());

    }
}