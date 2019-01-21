package edu.nju.cs.inform.core.group;

import edu.nju.cs.inform.io.CorpusExtractor;
import edu.nju.cs.inform.util.JavaElement;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by niejia on 16/2/11.
 */
public class FieldUseageParser {

    private Hashtable<String, Vector<String>> fieldUseageRelationsList;
    private Set<String> changedFieldsList;
    private CorpusExtractor corpus;

    public FieldUseageParser(HashSet<String> changedFieldsList, HashSet<String> changedMethodsList, CorpusExtractor corpus) {

        fieldUseageRelationsList = new Hashtable<>();
        this.corpus = corpus;
        this.changedFieldsList = changedFieldsList;

        for (String methodName : changedMethodsList) {
            Set<String> usedFieldsList = involvedChangedFieldInMethod(methodName);

            Vector<String> v = new Vector<>();
            for (String s : usedFieldsList) {
                if (!v.contains(s)) {
                    v.add(s);
                }
            }
            fieldUseageRelationsList.put(methodName, v);
        }
    }

    public Set<String> fetchFieldUseagesInMethod(String methodName, String filedName, CorpusExtractor corpus) {
        Set<String> usages = new LinkedHashSet<>();
        String methodBody = corpus.getMethodBody(methodName);
        String fieldID = JavaElement.getIdentifier(filedName);
        for (String line : methodBody.split("\n")) {
            if (fetchWordInLine(line, fieldID)) {
//                System.out.println(line);
                usages.add(line);
            }
        }
        return usages;
    }

    public boolean fetchWordInLine(String line, String word) {
        Pattern pat = Pattern.compile(word);
        Matcher m = pat.matcher(line);
        return m.find();
    }

    public Set<String> involvedChangedFieldInMethod(String methodName) {
        String className = JavaElement.getClassName(methodName);
        Set<String> changedFieldsInClass = getFieldsBelongToClass(className, changedFieldsList);

        Set<String> involvedField = new LinkedHashSet<>();
        for (String fieldName : changedFieldsInClass) {
            Set<String> fieldUsages = fetchFieldUseagesInMethod(methodName, fieldName, corpus);
            if (fieldUsages.size() != 0) {
                involvedField.add(fieldName);
            }
        }

//        System.out.println(" changedFieldsInClass = changedFieldsInClass" + changedFieldsInClass );
//        System.out.println(" involvedField = " + involvedField );

        return involvedField;
    }

    private Set<String> getFieldsBelongToClass(String className, Set<String> fields) {
        Set<String> result = new LinkedHashSet<>();

        for (String s : fields) {
            String s_className = JavaElement.getClassName(s);
            if (s_className.equals(className)) {
                result.add(s);
            }
        }
        return result;
    }



    public Hashtable<String, Vector<String>> getFieldUseageRelationsList() {
        return fieldUseageRelationsList;
    }
}
