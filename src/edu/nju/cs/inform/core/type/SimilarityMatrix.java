package edu.nju.cs.inform.core.type;

import java.io.Serializable;
import java.util.*;

/**
 * Created by niejia on 15/2/10.
 */

public class SimilarityMatrix implements Serializable {

    protected Map<String, Map<String, Double>> matrix = new LinkedHashMap<>();

    protected double threshold;
    protected String name;

    public SimilarityMatrix() {
        threshold = 0.0;
        name = "";
    }

    public SimilarityMatrix(SimilarityMatrix inMatrix) {
        this.matrix = inMatrix.matrix;
        this.name = inMatrix.name;
        this.threshold = inMatrix.threshold;
    }

    public void setThreshold(Double value) {
        if (value != null) {
            this.threshold = value;
        }
    }

    public double getThreshold() {
        return threshold;
    }

    public StringHashSet sourceArtifactsIds() {
        StringHashSet hs = new StringHashSet();
        for (String s : matrix.keySet()) {
            hs.add(s);
        }
        return hs;
    }

    public StringHashSet targetArtifactsIds() {
        StringHashSet hs = new StringHashSet();
        for (SingleLink link : allLinks()) {
            hs.add(link.getTargetArtifactId());
        }
        return hs;
    }

    public Double getScoreForLink(String sourceArtfactId, String targetArtfactId) {
        Double retVal = 0.0;
        Map<String, Double> links = matrix.get(sourceArtfactId);
        if (links != null) {
            retVal = links.get(targetArtfactId);
        }
        return retVal;
    }

    public void setScoreForLink(String sourceArtfactId, String targetArtfactId, Double score) {
        if (matrix.get(sourceArtfactId).get(targetArtfactId) != null) {
            matrix.get(sourceArtfactId).put(targetArtfactId, score);
        } else {
            System.out.println("Target link not Found, Update score failed.");
        }
    }

    public LinksList allLinks() {
        LinksList allLinks = new LinksList();
        for (Map.Entry<String, Map<String, Double>> sourceArtifact : matrix.entrySet()) {
            String sourceArtifactId = sourceArtifact.getKey();
            Map<String, Double> sourceArtifactLinks = sourceArtifact.getValue();

            for (Map.Entry<String, Double> targetArtifact : sourceArtifactLinks.entrySet()) {
                String targetArtifactId = targetArtifact.getKey();
                Double score = targetArtifact.getValue();

                allLinks.add(new SingleLink(sourceArtifactId, targetArtifactId, score));
            }
        }
        return allLinks;
    }

    public void addLink(String sourceArtifactId, String targetArtifactId, Double score) {
        Map<String, Double> links = matrix.get(sourceArtifactId);
        if (links == null) {
            links = new LinkedHashMap<>();
            matrix.put(sourceArtifactId, links);
        }

        if (links.containsKey(targetArtifactId) == false) {
            links.put(targetArtifactId, score);
        } else {
            if (score != links.get(targetArtifactId)) {
                throw new IllegalArgumentException(String.format("Link for source artifact %s and target artifact %s has already been added to the spare matrix", sourceArtifactId,
                        targetArtifactId));
            }
        }
    }

    public Map<String, Double> getLinksForSourceId(String sourceArtifactId) {
        return matrix.get(sourceArtifactId);
    }

    public int count() {
        int totalCount = 0;
        for (Map<String, Double> links : matrix.values()) {
            totalCount += links.size();
        }
        return totalCount;
    }

    public boolean isLinkAboveThreshold(String sourceArtifactId, String targetArtifactId) {
        boolean retVal = false;
        Map<String, Double> links = matrix.get(sourceArtifactId);
        if (links != null) {
            Double score = links.get(targetArtifactId);
            if (score != null) {
                retVal = (score >= threshold);
            }
        }
        return retVal;
    }

    public StringHashSet getSetOfTargetArtifactIdsAboveThresholdForSourceArtifact(String sourceArtifactId) {
        StringHashSet linksForSourceArtifact = new StringHashSet();
        linksForSourceArtifact = new StringHashSet();

        Map<String, Double> links = matrix.get(sourceArtifactId);
        if (links != null) {
            for (String targetArtfactId : links.keySet()) {
                if (links.get(targetArtfactId) > threshold) {
                    linksForSourceArtifact.add(targetArtfactId);
                }
            }
        }
        return linksForSourceArtifact;
    }

    public LinksList getLinksAboveThresholdForSourceArtifact(String sourceArtifactId) {
        LinksList linksForSourceArtifact = new LinksList();
        linksForSourceArtifact = new LinksList();

        Map<String, Double> links = matrix.get(sourceArtifactId);
        if (links != null) {
            for (String targetArtifactId : links.keySet()) {
                if (isLinkAboveThreshold(sourceArtifactId, targetArtifactId)) {
                    linksForSourceArtifact.add(new SingleLink(sourceArtifactId, targetArtifactId,
                            links.get(targetArtifactId)));
                }
            }
        }
        return linksForSourceArtifact;
    }

    public int getCountOfLinksAboveThresholdForSourceArtifact(String sourceArtifactId) {
        return getSetOfTargetArtifactIdsAboveThresholdForSourceArtifact(sourceArtifactId).size();
    }

    public LinksList getLinksAboveThreshold() {
        LinksList allLinks = allLinks();
        LinksList linksAboveThreshold = new LinksList();
        for (SingleLink link : allLinks) {
            if (link.getScore() > threshold) {
                linksAboveThreshold.add(link);
            }
        }
        return linksAboveThreshold;
    }

    public String toString() {
        return allLinks().toString();
    }

}

