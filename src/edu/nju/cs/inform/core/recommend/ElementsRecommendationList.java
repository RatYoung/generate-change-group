package edu.nju.cs.inform.core.recommend;

import edu.nju.cs.inform.core.type.ArtifactsCollection;
import edu.nju.cs.inform.util.JavaElement;
import edu.nju.cs.inform.util.NDCG;
import edu.nju.cs.inform.util._;

import java.util.*;

/**
 * Created by niejia on 16/3/3.
 */
public class ElementsRecommendationList {

    private List<String> elementsRank;
    private Map<String, Double> elementPrecison;

    private List<String> bestRank;

    public ElementsRecommendationList(ArtifactsCollection elementsCollection, String oracleText) {

        elementsRank = new ArrayList<>();
        elementPrecison = new LinkedHashMap<>();
        for (String element : elementsCollection.keySet()) {
            elementsRank.add(element);

            List<String> elementTokens = getTokens(elementsCollection.get(element).text);
            Set<String> oracleTokens = getTokensSet(oracleText);
            double precision = computePrecision(elementTokens, oracleTokens);
            elementPrecison.put(element, precision);
        }


        Map<String, Double> bestRankMap = new LinkedHashMap<>();
        for (String element : elementsRank) {
            bestRankMap.put(element, elementPrecison.get(element));
        }

        bestRank = new ArrayList<>();
        Map<String, Double> sortedRankMap = _.sortValueByDescending(bestRankMap);
        for (String element : sortedRankMap.keySet()) {
            bestRank.add(element);
        }

        System.out.println(" bestRank = " + bestRank);
//        System.out.println("Precision: " + computePrecision(getTokens(changeCollection.get(m).text), getTokensSet(changelogCollection.get(version).text)));
    }

    public void randomList() {
        long seed = System.nanoTime();
        Collections.shuffle(elementsRank, new Random(seed));
//        System.out.println(elementsRank);
    }

    public double getPrecisionDCGByRank(int k) {
        System.out.println(" elementsRank = " + elementsRank );
        if (k > elementsRank.size()) {
            return getDCGByRank(getScoresOfRank(elementPrecison, elementsRank), elementsRank.size());
        }
        return getDCGByRank(getScoresOfRank(elementPrecison,elementsRank), k);
    }

    public double getPrecisionNDCGByRank(int k) {
        if (k > elementsRank.size()) {
            return getNDCGByRank(getScoresOfRank(elementPrecison, elementsRank), getScoresOfRank(elementPrecison, bestRank), elementsRank.size());
        }
        return getNDCGByRank(getScoresOfRank(elementPrecison, elementsRank), getScoresOfRank(elementPrecison, bestRank), k);
    }

    public double getDCGByRank(List<Double> values, int k) {
        double dcg = NDCG.getDCG(values, k);
        return dcg;
    }

    public double getNDCGByRank(List<Double> values, List<Double> oracle,int k) {
        double dcg = NDCG.getNDCG(values, oracle, k);
        return dcg;
    }

    public List<Double> getScoresOfRank(Map<String, Double> valuesMap, List<String> rank) {
        List<Double> scores = new ArrayList<>();
        for (String e : rank) {
            scores.add(valuesMap.get(e));
        }
        return scores;
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

    public void setRank(List<String> rank) {
        this.elementsRank = rank;
    }


    public void setRankWithAlphabet() {


        Map<String, String> elementsMethods = new LinkedHashMap<>();
        for (String str : elementsRank) {
            String identifier = JavaElement.getIdentifier(str);
            if (identifier.equals("<init>")) {
                elementsMethods.put(str, JavaElement.getClassName(str).toLowerCase());
            } else {
                elementsMethods.put(str, identifier);
            }

        }

        Map<String, String> sortedElementsMethods = _.sortValueAscending(elementsMethods);
        List<String> result = new LinkedList<>();
        for (String str : sortedElementsMethods.keySet()) {
            result.add(str);
        }
        elementsRank = result;
        System.out.println("AlphaRank: " + elementsRank);
    }
}
