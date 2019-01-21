package edu.nju.cs.inform.core.ir;

import edu.nju.cs.inform.core.type.TermDocumentMatrix;

import java.util.NoSuchElementException;

/**
 * Created by niejia on 15/5/20.
 */
public class DocVectorLookUp {

    private TermDocumentMatrix query;

    public DocVectorLookUp(TermDocumentMatrix query) {
        this.query = query;
    }

    public double[] getVectorForDoc(String target) {
        double[] doc = query.getDocument(target);
        if (doc==null) throw new NoSuchElementException("Target Doc not In TermDocumentMatrix");
        return query.getDocument(target);
    }
}
