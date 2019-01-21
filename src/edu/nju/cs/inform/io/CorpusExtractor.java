package edu.nju.cs.inform.io;

import edu.nju.cs.inform.core.diff.SourceCodeElements;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by niejia on 15/11/29.
 * Extract corpus at method level for each version of code
 */
public class CorpusExtractor {

    private SourceCodeElements sourceCodeElements;

    public CorpusExtractor(SourceCodeElements sourceCodeElements) {
        this.sourceCodeElements = sourceCodeElements;
    }



    public Set<String> getFieldsInClass(String className) {
        Set<String> fields = new HashSet<>();
//        String content = _.readFile(extractedClassFieldPath + "/" + className + ".txt");
//        if (content == null) {
//            return fields;
//        } else {
//            String[] tokens = content.split("\n");
//            for (String s : tokens) {
//                fields.add(className+"."+s);
//            }
//            return fields;
//        }
        return fields;
    }


    public Set<String> getMethodsInClass(String className) {
        Set<String> methods = new HashSet<>();
//        String content = _.readFile(extractedClassMethodPath + "/" + className + ".txt");
//        if (content == null) {
//            return methods;
//        } else {
//            String[] tokens = content.split("\n");
//            for (String s : tokens) {
//                methods.add(className + "." + s);
//            }
//            return methods;
//        }
        return methods;
    }

//    public String getMethodBody(String methodName) {
//        String content = _.readFile(extractedMethodBodyPath + "/" + methodName + ".txt");
//        return content != null ? content : "";
//    }

    public String getMethodBody(String methodName) {
        return sourceCodeElements.getMethodBody(methodName);
    }


    public String getMethodParameters(String methodName) {
        return sourceCodeElements.getMethodParameters(methodName);
    }

    public String getMethodComments(String methodName) {
        return sourceCodeElements.getMethodComments(methodName);
    }

    public String getClassComments(String className) {
        return sourceCodeElements.getClassComments(className);
    }
}
