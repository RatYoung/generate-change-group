package edu.nju.cs.inform.core.jdt;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by niejia on 15/2/22.
 */
public class JMethod {
    private String className;
    private String methodName;
    private String doc;
    private List<String> paraNameList;
    private String methodBody;

    public JMethod() {
        paraNameList = new ArrayList<>();
        doc = "";
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public String getDoc() {
        return doc;
    }

    public void setDoc(String doc) {
        this.doc = doc;
    }

    public void addParaName(String paraName) {
        paraNameList.add(paraName);
    }

    public List<String> getParaNameList() {
        return paraNameList;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(className);
        sb.append(" ");
        sb.append(methodName);
        sb.append(" ");
        for (String p : paraNameList) {
            sb.append(p);
            sb.append(" ");
        }
        sb.append("\n");
        sb.append(doc);
        return sb.toString();
    }

    public void setMethodBody(String content) {
        this.methodBody = content;
    }

    public String getMethodBody() {
        return methodBody;
    }
}
