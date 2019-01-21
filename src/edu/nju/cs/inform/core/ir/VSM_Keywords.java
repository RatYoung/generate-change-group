package edu.nju.cs.inform.core.ir;

import edu.nju.cs.inform.core.type.ArtifactsCollection;
import edu.nju.cs.inform.core.type.TermDocumentMatrix;

import java.util.*;

/**
 * Created by niejia on 16/2/27.
 */
public class VSM_Keywords {

    private TermDocumentMatrix changeMatrix;
    private TermDocumentMatrix codeMatrix;
    private TermDocumentMatrix TFIDF;



    public VSM_Keywords(ArtifactsCollection changeCollection, ArtifactsCollection codeCollection) {

        TermDocumentMatrix changeMatrix = new TermDocumentMatrix(changeCollection);
        TermDocumentMatrix codeMatrix = new TermDocumentMatrix(codeCollection);

        TermDocumentMatrix TF_Changes = ComputeTF(changeMatrix);

        Map<String, Double> idfMap = ComputeIDFFromBoth(changeCollection, codeCollection);
        this.TFIDF = ComputeTFIDF(TF_Changes, idfMap);
//        getTopkTerm(0, TFIDF);

    }

    public List<String> getTopkTermInDoc(int k, String docName) {
        List<String> topkTerm = new ArrayList<>();
        Map<String, Double> termsList = new TreeMap<>();
        double[] documents = TFIDF.getDocument(docName);
        for (int j = 0; j < documents.length; j++) {
            termsList.put(TFIDF.getTermName(j), TFIDF.getValue(TFIDF.getDocumentIndex(docName), j));
        }
        Map<String, Double> sortedTermsList = sortByValue(termsList);

        int i = 0;
        for (String term : sortedTermsList.keySet()) {
            if (i < k) {
                topkTerm.add(term);
                i++;
            } else {
                break;
            }
        }

        return topkTerm;
    }

    public Map<String, Double> getTermsScoreInDoc(String docName) {
        Map<String, Double> termsList = new TreeMap<>();
        double[] documents = TFIDF.getDocument(docName);
        for (int j = 0; j < documents.length; j++) {
            termsList.put(TFIDF.getTermName(j), TFIDF.getValue(TFIDF.getDocumentIndex(docName), j));
            if (TFIDF.getValue(TFIDF.getDocumentIndex(docName), j) != 0.0) {
//                System.out.println(TFIDF.getTermName(j) + " " + TFIDF.getValue(TFIDF.getDocumentIndex(docName), j));
            }

        }
        Map<String, Double> sortedTermsList = sortByValue(termsList);

        return sortedTermsList;
    }

    private Map<String, Double> ComputeIDFFromBoth(ArtifactsCollection changeCollection, ArtifactsCollection codeCollection) {
        Map<String, Double> idfMap = new LinkedHashMap<>();

        ArtifactsCollection bothSourceAndTarget = new ArtifactsCollection();
        bothSourceAndTarget.putAll(changeCollection);
        bothSourceAndTarget.putAll(codeCollection);

        TermDocumentMatrix both = new TermDocumentMatrix(bothSourceAndTarget);
        double[] IDF = ComputeIDF(ComputeDF(both), both.NumDocs());

        for (int j = 0; j < both.NumTerms(); j++) {
            idfMap.put(both.getTermName(j), IDF[j]);
        }

        return sortByValue(idfMap);
    }

    private static Map<String, Double> ComputeIDFMap(TermDocumentMatrix matrix, double[] idf) {
        Map<String, Double> idfMap = new LinkedHashMap<>();

        for (int j = 0; j < matrix.NumTerms(); j++) {
            idfMap.put(matrix.getTermName(j), idf[j]);
        }

        return idfMap;
    }

    private  List<Map<String, Double>> getTopkTerm(int k, TermDocumentMatrix tfidf) {

        List<Map<String, Double>> result = new ArrayList<>();


        for (int i = 0; i < tfidf.NumDocs(); i++) {
            Map<String, Double> topkTerm = new TreeMap<>();
            for (int j = 0; j < tfidf.NumTerms(); j++) {
                topkTerm.put(tfidf.getTermName(j), tfidf.getValue(i, j));
            }
            Map<String, Double> sortedTopkTerm = sortByValue(topkTerm);
            result.add(topkTerm);
        }

        return result;
    }

    private  double[] ComputeIDF(double[] df, int numDocs) {
        double[] idf = new double[df.length];
        Map<String, Double> idfMap = new LinkedHashMap<>();
        for (int i = 0; i < df.length; i++) {
            if (df[i] <= 0.0) {
                idf[i] = 0.0;
            } else {
                idf[i] = Math.log(numDocs / df[i]);
            }
        }
        return idf;
    }

    private  TermDocumentMatrix ComputeTFIDF(TermDocumentMatrix tf, Map<String,Double> idf) {
        for (int i = 0; i < tf.NumDocs(); i++) {
            for (int j = 0; j < tf.NumTerms(); j++) {
                tf.setValue(i, j, tf.getValue(i, j) * idf.get(tf.getTermName(j)));
            }
        }
        return tf;
    }

    private  double[] ComputeDF(TermDocumentMatrix matrix) {
        double[] df = new double[matrix.NumTerms()];
        for (int j = 0; j < matrix.NumTerms(); j++) {
            df[j] = 0.0;
            for (int i = 0; i < matrix.NumDocs(); i++) {
                df[j] += (matrix.getValue(i, j) > 0.0) ? 1.0 : 0.0;
            }
        }
        return df;
    }

    private  TermDocumentMatrix ComputeTF(TermDocumentMatrix matrix) {
        for (int i = 0; i < matrix.NumDocs(); i++) {
            double max = 0.0;
            for (int k = 0; k < matrix.NumTerms(); k++) {
                max += matrix.getValue(i, k);
            }

            for (int j = 0; j < matrix.NumTerms(); j++) {
                matrix.setValue(i, j, (matrix.getValue(i, j) / max));
            }
        }
        return matrix;
    }



    private  <K, V extends Comparable<? super V>> Map<K, V>
    sortByValue( Map<K, V> map )
    {
        List<Map.Entry<K, V>> list =
                new LinkedList<>( map.entrySet() );
        Collections.sort(list, new Comparator<Map.Entry<K, V>>(){
            @Override
            public int compare(Map.Entry<K, V> o1, Map.Entry<K, V> o2) {
                return (o2.getValue()).compareTo(o1.getValue());
            }
        });

        Map<K, V> result = new LinkedHashMap<>();
        for (Map.Entry<K, V> entry : list)
        {
            result.put( entry.getKey(), entry.getValue() );
        }
        return result;
    }
}
