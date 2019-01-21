package edu.nju.cs.inform.core.group;

import edu.nju.cs.inform.core.diff.CodeElementsComparer;
import edu.nju.cs.inform.io.ChangedArtifacts;
import edu.nju.cs.inform.io.CorpusExtractor;
import edu.nju.cs.inform.core.relation.CallRelationGraph;
import edu.nju.cs.inform.core.relation.RelationInfo;

/**
 * Created by niejia on 16/4/13.
 */
public class ChangeRegionAlgorithm {

//    String newVersionJarPath = "data/sample/jar/change4.jar";
//    String oldVersionJarPath = "data/sample/jar/change3.jar";

    public ChangeRegionAlgorithm(CodeElementsComparer codeElementsComparer) {

//        RelationInfo relationInfoForChangedPart = new RelationInfo(newVersionJarPath, oldVersionJarPath, codeElementsComparer, false);
        RelationInfo relationInfoForChangedPart = new RelationInfo(codeElementsComparer.getNewVersionCodeDirPath(), codeElementsComparer.getOldVersionCodeDirPath(), codeElementsComparer, false);
        CallRelationGraph callGraphForChangedPart = new CallRelationGraph(relationInfoForChangedPart);
        double thresholdForInitialRegion = 0.35;
//        RelationInfo oldRelationInfo = new RelationInfo(newVersionJarPath,false);
        RelationInfo oldRelationInfo = new RelationInfo(codeElementsComparer.getOldVersionCodeDirPath(),false);
        oldRelationInfo.setPruning(thresholdForInitialRegion);
//        RelationInfo newRelationInfo = new RelationInfo(oldVersionJarPath,false);
        RelationInfo newRelationInfo = new RelationInfo(codeElementsComparer.getNewVersionCodeDirPath(),false);
        newRelationInfo.setPruning(thresholdForInitialRegion);

        CallRelationGraph oldCallGraph = new CallRelationGraph(oldRelationInfo);
        CallRelationGraph newCallGraph = new CallRelationGraph(newRelationInfo);

        CorpusExtractor newCorpus = new CorpusExtractor(codeElementsComparer.getNewVersionCodeElements());
        CorpusExtractor oldCorpus = new CorpusExtractor(codeElementsComparer.getOldVersionCodeElements());

        ChangedArtifacts changedArtifacts = new ChangedArtifacts();
        changedArtifacts.parse(codeElementsComparer);

        InitialRegionFetcher fetcher = new InitialRegionFetcher(changedArtifacts, newCallGraph, oldCallGraph);

        ExportInitialRegion exporter = new ExportInitialRegion(fetcher.getChangeRegion());

        ChangedArtifactsGrouper grouper = new ChangedArtifactsGrouper(changedArtifacts, callGraphForChangedPart, fetcher.getChangeRegion(), newCorpus, oldCorpus);

        KeywordsExtracterForChangeRegion keywordsExtracter = new KeywordsExtracterForChangeRegion(codeElementsComparer, changedArtifacts, fetcher.getChangeRegion(), grouper.getChangedArtifactsGroup(), newCorpus, oldCorpus);
        keywordsExtracter.showFinalRegion();

        codeElementsComparer.setChangeDescriptions(keywordsExtracter.getChangeGroupDescriptions());
        codeElementsComparer.setChangedCodeElementsRelationInfo(relationInfoForChangedPart);
        codeElementsComparer.setElementsInGroup(keywordsExtracter.getElementsInGroupList());
    }
}
