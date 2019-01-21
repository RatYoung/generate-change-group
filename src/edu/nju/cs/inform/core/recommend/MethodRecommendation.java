package edu.nju.cs.inform.core.recommend;


import edu.nju.cs.inform.core.diff.CodeElementsComparer;
import edu.nju.cs.inform.core.ir.VSM_Keywords;
import edu.nju.cs.inform.core.preprocess.ArtifactPreprocessor;
import edu.nju.cs.inform.core.type.Artifact;
import edu.nju.cs.inform.core.type.ArtifactsCollection;
import edu.nju.cs.inform.core.type.SimilarityMatrix;
import edu.nju.cs.inform.core.relation.CallRelationGraph;
import edu.nju.cs.inform.core.relation.graph.CodeVertex;
import edu.nju.cs.inform.util.JavaElement;
import edu.nju.cs.inform.util._;

import java.util.*;

/**
 * Created by niejia on 16/3/19.
 */
public class MethodRecommendation {


    private double threshold = 0.0;
    private SimilarityMatrix similarity;
    private VSM_Keywords vsm_keywords;

    private Map<String, Double> sortedMethodScoreMap;

    private Map<String, List<String>> recommendMethodsMap;

    public MethodRecommendation(CodeElementsComparer codeElementsComparer, ArtifactsCollection requirementCollection, SimilarityMatrix similarityMatrix) {
        this.recommendMethodsMap = new LinkedHashMap<>();

        Set<String> outdatedRequirements = new LinkedHashSet<>();
        for (String req : requirementCollection.keySet()) {
            outdatedRequirements.add(req);
        }

        this.similarity = similarityMatrix;
        CallRelationGraph callGraphForChangedPart = new CallRelationGraph(codeElementsComparer.getChangedCodeElementsRelationInfo());

        ArtifactsCollection elementCollection = codeElementsComparer.getChangedMethodsCollection();
        ArtifactsCollection codebaseCollection = codeElementsComparer.getPreprocessedNewVersionCodeCollection();

        for (String outdatedReq : outdatedRequirements) {
            Set<String> codesAffectReq = findCodesAffectedReq(outdatedReq);

            ArtifactsCollection involvedElements = new ArtifactsCollection();
            for (String group : codesAffectReq) {
//                String subElementsPath = elementsPath + version + "/" + group;
//
//                ArtifactsCollection elementsCollection = ArtifactsReader.getCollections(subElementsPath, ".txt");
//
//                for (String e : elementsCollection.keySet()) {
//
//                    if (elementCollection.keySet().contains(e)) {
//                        involvedElements.put(e, elementsCollection.get(e));
//                    }
//                }

                Set<String> elementsCollection = codeElementsComparer.getElementsInGroup(group);

                for (String e : elementsCollection) {
                    if (elementCollection.keySet().contains(e)) {
                        involvedElements.put(e, new Artifact(e, ArtifactPreprocessor.handleJavaFile(e)));
                    }
                }
            }

            List<Set<String>> callGroup = createCallGroups(callGraphForChangedPart, involvedElements.keySet());

            addMethodWithoutCallRelationIntoCallGroup(callGroup, involvedElements);
            ArtifactsCollection changeGroupCollection = constructChangeGroupCollection(callGroup, involvedElements);

            this.vsm_keywords = new VSM_Keywords(changeGroupCollection, codebaseCollection);

            Map<String, Double> methodScore = new LinkedHashMap<>();

            for (String method : involvedElements.keySet()) {

                String docThatMethodBelongsTo = findDoc(method, callGroup);

                Map<String, Double> termScores = vsm_keywords.getTermsScoreInDoc(docThatMethodBelongsTo);

                double averageScore = computeAverageScore(termScores);
                methodScore.put(method, averageScore);
            }

            Map<String, Double> sortedMethodScoreMap = _.sortValueByDescending(methodScore);
            this.sortedMethodScoreMap = sortedMethodScoreMap;

            List<String> rank = new ArrayList<>();
            for (String method : sortedMethodScoreMap.keySet()) {
                rank.add(method);
            }

            recommendMethodsMap.put(outdatedReq, rank);
//
//            for (String m : sortedMethodScoreMap.keySet()) {
//                System.out.println(m);
//                System.out.println("score: " + sortedMethodScoreMap.get(m));
//                System.out.println();
//            }
        }
    }

    private void addMethodWithoutCallRelationIntoCallGroup(List<Set<String>> callGroup, ArtifactsCollection involvedElements) {
        List<Set<String>> remainedGroup = new ArrayList<>();

        for (String element : involvedElements.keySet()) {
            if (!callGroupHasElement(callGroup, element)) {
                Set<String> g = new LinkedHashSet<>();
                g.add(element);
                remainedGroup.add(g);
            }
        }

        for (Set<String> group : remainedGroup) {
            callGroup.add(group);
        }
    }

    private boolean callGroupHasElement(List<Set<String>> callGroup, String element) {
        for (int i = 0; i < callGroup.size(); i++) {
            Set<String> group = callGroup.get(i);
            if (group.contains(element)) {
                return true;
            }
        }
        return false;
    }

    private String findDoc(String method, List<Set<String>> callGroup) {

        for (int i = 0; i < callGroup.size(); i++) {
            Set<String> group = callGroup.get(i);
            if (group.contains(method)) {
                return "Group" + i;
            }
        }

        return null;
    }

    private ArtifactsCollection constructChangeGroupCollection(List<Set<String>> callGroup, ArtifactsCollection codeElements) {

        ArtifactsCollection changeGroupCollection = new ArtifactsCollection();

        for (int i = 0; i < callGroup.size(); i++) {
            Set<String> elements = callGroup.get(i);

            StringBuffer content = new StringBuffer();
            for (String e : elements) {
//                System.out.println(" e = " + e );
//                System.out.println(codeElements.get(e));
                content.append(preprocessEmptyToken(codeElements.get(e).text));
//                content.append(CleanUp.chararctorClean(codeElements.get(e).text));
                content.append(" ");
            }

            Artifact artifact = new Artifact("Group" + i, content.toString());
            changeGroupCollection.put("Group" + i, artifact);
        }

        return changeGroupCollection;
    }

    private String preprocessEmptyToken(String text) {
        String[] tokens = text.split(" ");
        StringBuffer sb = new StringBuffer();

        for (int i = 0; i < tokens.length; i++) {
            if (!tokens[i].equals("") && tokens[i].length() > 0) {
                sb.append(tokens[i]);
                sb.append(" ");
            }
        }

        return sb.toString();
    }


    public List<String> getMethodRank() {
        List<String> rank = new ArrayList<>();
        for (String s : sortedMethodScoreMap.keySet()) {
            rank.add(s);
        }
        return rank;
    }

    private double computeSumScore(Map<String, Double> termScores) {
        double sum = 0.0;
        for (String term : termScores.keySet()) {
            double score = termScores.get(term);
            if (score > 0.0) {
                sum += score;
            }
        }

        return sum;
    }

    private double computeAverageScore(Map<String, Double> termScores) {

        int termNumGZero = 0;
        double sum = 0.0;
        for (String term : termScores.keySet()) {
            double score = termScores.get(term);
            if (score > 0.0) {
                sum += score;
                termNumGZero++;
            }
        }

        return sum / (termNumGZero * 1.0);
    }

    private List<String> computeTopkTerm(Set<String> codesAffectReq, int topk) {
        Map<String, Double> scoresMap = new LinkedHashMap<>();
        for (String c : codesAffectReq) {
            Map<String, Double> termsList = vsm_keywords.getTermsScoreInDoc(c);

            for (String t : termsList.keySet()) {
                if (scoresMap.containsKey(t)) {
                    scoresMap.put(t, scoresMap.get(t) + termsList.get(t));
                } else {
                    scoresMap.put(t, termsList.get(t));
                }
            }
        }
        Map<String, Double> sortedScoresMap = _.sortValueByDescending(scoresMap);

        List<String> topkTerm = new ArrayList<>();
        int i = 0;
        for (String term : sortedScoresMap.keySet()) {
            if (i < topk) {
                topkTerm.add(term);
                i++;
            } else {
                break;
            }
        }
        return topkTerm;
    }

    private double computeRecall(List<String> topkTerm, Set<String> changelogtokens) {
        int correctNum = 0;

        for (String token : topkTerm) {
            if (changelogtokens.contains(token)) {
                correctNum++;
            }
        }

        return 1.0 * correctNum / changelogtokens.size();
    }

    private double computePrecision(List<String> topkTerm, Set<String> changelogtokens) {
        int correctNum = 0;

        for (String token : topkTerm) {
            if (changelogtokens.contains(token)) {
                correctNum++;
            }
        }

        return 1.0 * correctNum / topkTerm.size();
    }

    private Set<String> findCodesAffectedReq(String outdatedReq) {
        Set<String> codesList = new HashSet<>();
        for (String code : similarity.sourceArtifactsIds()) {
            if (similarity.getScoreForLink(code, outdatedReq) > threshold) {
                codesList.add(code);
            }
        }
        return codesList;
    }

    private List<String> getTokens(String text) {
        String[] terms = text.split(" ");

        List<String> tokens = new ArrayList<>();
        for (String t : terms) {
            tokens.add(t);
        }
        return tokens;
    }

    private Set<String> getTokensSet(String text) {
        String[] terms = text.split(" ");

        Set<String> tokens = new LinkedHashSet<>();
        for (String t : terms) {
            tokens.add(t);
        }
        return tokens;
    }


    private String computeDiffCorpus(String requirementContent, String changeDescription) {
        StringBuffer sb = new StringBuffer();
        String[] tokensInRequirement = requirementContent.split(" ");
        ArrayList<String> tokensListInRequirement = new ArrayList<String>(Arrays.asList(tokensInRequirement));
        String[] tokensInChangeDescription = changeDescription.split(" ");
        ArrayList<String> tokensListInChangeDescription = new ArrayList<String>(Arrays.asList(tokensInChangeDescription));

        for (String token : tokensListInChangeDescription) {
            if (!tokensListInRequirement.contains(token)) {
                sb.append(token);
                sb.append(" ");
            }
        }

        return sb.toString();
    }

    private void cleanRankForiTrust(List<String> methodRankByGroup, ArtifactsCollection changeCollection) {
        Iterator it = methodRankByGroup.iterator();
        while (it.hasNext()) {
            String method = (String) it.next();
            if (!changeCollection.keySet().contains(method)) {
                it.remove();
            }
        }
    }

    private List<String> rankMethodByGroup(Set<Integer> groupRank, List<Set<String>> callGroup) {
        List<String> rankedMethod = new ArrayList<>();

        for (Integer i : groupRank) {
            Set<String> group = callGroup.get(i);
            List<String> methodsRankInGroup = getMethodRankInGroup(group);
            for (String method : methodsRankInGroup) {
                rankedMethod.add(method);
            }
        }

        return rankedMethod;
    }

    private List<String> getMethodRankInGroup(Set<String> group) {
        List<String> rankList = new ArrayList<>();
        Map<String, Double> map = new LinkedHashMap<>();
        for (String str : group) {
            map.put(str, sortedMethodScoreMap.get(str));
        }

        Map<String, Double> sortedMap = _.sortValueByDescending(map);
        for (String str : sortedMap.keySet()) {
            rankList.add(str);
        }

        return rankList;
    }

    private double getGroupScore(Set<String> artifact) {
        double score = 0.0;
        int size = 0;
        for (String s : artifact) {
//            System.out.println(" s = " + s );
//            if ()
            score += sortedMethodScoreMap.get(s);
        }
        return score / (1.0 * artifact.size());
    }

    private List<Set<String>> createCallGroups(CallRelationGraph callGraphForChangedPart, Set<String> artifactsList) {
        Map<String, Boolean> artifactAllocated = new HashMap<>();
        for (String artifact : artifactsList) {
            artifactAllocated.put(artifact, false);
        }
        List<Set<String>> result = new ArrayList<>();

        for (String artifact : artifactsList) {
            CodeVertex vertex = callGraphForChangedPart.getCodeVertexByName(findCorrespondingName(artifact, callGraphForChangedPart.getVertexes().values()));

            // only consider method
            if (vertex != null) {
                if (artifactAllocated.get(artifact) == false) {
                    List<CodeVertex> connectedGraph = new ArrayList<>();
                    callGraphForChangedPart.searhNeighbourConnectedGraphByCall(vertex, connectedGraph);
                    Set<String> artifactGroup = new LinkedHashSet<>();
                    for (CodeVertex v : connectedGraph) {
                        if (artifactsList.contains(getClassName(v.getName()))) {
                            artifactGroup.add(getClassName(v.getName()));
                            artifactAllocated.put(getClassName(v.getName()), true);
                        }
                    }
                    artifactGroup.add(getClassName(vertex.getName()));
                    artifactAllocated.put(getClassName(vertex.getName()), true);
                    result.add(artifactGroup);
                }
            }

        }
        return result;
    }

    private String getClassName(String name) {
//        String className = JavaElement.getIdentifier(JavaElement.getPackageName(name) + "." + JavaElement.getClassName(name)) + "." + JavaElement.getIdentifier(name);
        String className =  JavaElement.getClassName(name) + "." + JavaElement.getIdentifier(name);
//        System.out.println(" className = " + className );
        return className;
    }

    private String findCorrespondingName(String artifact, Collection<CodeVertex> callGrapgh) {
        for (CodeVertex v : callGrapgh) {
            if (v.getName().endsWith(artifact)) {
                return v.getName();
            }
        }
        return null;
    }

    public Map<String, List<String>> getRecommendMethodsForRequirements() {
        return recommendMethodsMap;
    }
}
