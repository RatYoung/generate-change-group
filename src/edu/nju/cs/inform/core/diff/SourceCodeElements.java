package edu.nju.cs.inform.core.diff;

import edu.nju.cs.inform.core.jdt.JavaElementsParser;
import edu.nju.cs.inform.core.type.ArtifactsCollection;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by niejia on 16/3/15.
 */
public class SourceCodeElements {

    private Set<String> packagesList;
    private Set<String> classesList;
    private Set<String> methodsList;
    private Set<String> fieldsList;

    private Map<String,String> classComments;
    private Map<String,String> methodComments;
    private Map<String,String> methodParameters;
    private Map<String,String> methodBody;

    public SourceCodeElements(ArtifactsCollection codeCollection) {
        packagesList = new LinkedHashSet<>();
        classesList = new LinkedHashSet<>();
        methodsList = new LinkedHashSet<>();
        fieldsList = new LinkedHashSet<>();

        classComments = new HashMap<>();
        methodComments = new HashMap<>();
        methodParameters = new HashMap<>();
        methodBody = new HashMap<>();

        for (String className : codeCollection.keySet()) {
            String contentInCode = codeCollection.get(className).text;
            JavaElementsParser parser = new JavaElementsParser(contentInCode, this);
        }
    }

    public void addPackage(String packageName) {
        getPackagesList().add(packageName);
    }

    public void addClass(String className) {
        getClassesList().add(className);
    }

    public void addMethod(String methodName) {
        getMethodsList().add(methodName);
    }

    public void addField(String fieldName) {
        getFieldsList().add(fieldName);
    }

    public Set<String> getPackagesList() {
        return packagesList;
    }

    public Set<String> getClassesList() {
        return classesList;
    }

    public Set<String> getMethodsList() {
        return methodsList;
    }

    public Set<String> getFieldsList() {
        return fieldsList;
    }

    public void addClassComments(String className, String comments) {
        classComments.put(className, comments);
    }

    public void addMethodComments(String method, String comments) {
        methodComments.put(method, comments);
    }

    public void addMethodParameters(String method, String comments) {
        methodParameters.put(method, comments);
    }

    public void addMethodBody(String method, String body) {
        methodBody.put(method, body);
    }

    public String getMethodBody(String methodName) {
        return methodBody.get(methodName);
    }

    public String getMethodParameters(String methodName) {
        return methodParameters.get(methodName);
    }

    public String getMethodComments(String methodName) {
        return methodComments.get(methodName);
    }

    public String getClassComments(String className) {
        return classComments.get(className);
    }
}
