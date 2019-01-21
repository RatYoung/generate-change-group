package edu.nju.cs.inform.core.jda.parser;

import edu.nju.cs.inform.core.jda.type.CallRelationAnalyser;
import edu.nju.cs.inform.core.jda.type.JDACallRelation;
import edu.nju.cs.inform.util.JavaElement;

import java.io.File;
import java.util.*;

/**
 * Created by niejia on 16/1/29.
 */
public class ProjectCallRelationAnalyser {
    private Set<JDACallRelation> rukiaCallRelationsList;

    private Set<String> projectClasses;

    private Hashtable<String, Vector<String>> callGraphMap;

    public ProjectCallRelationAnalyser(String projectDirPath) {
        rukiaCallRelationsList = new LinkedHashSet<>();
        projectClasses = new LinkedHashSet<>();
        callGraphMap = new Hashtable<>();

        File dirFile = new File(projectDirPath);
        for (File f : dirFile.listFiles()) {
            CallRelationAnalyser analyser = new CallRelationAnalyser(f.getPath());
            String className = f.getName().split("\\.")[0];
            projectClasses.add(className);
            Set<JDACallRelation> crs = analyser.getRukiaCallRelationsList();
            for (JDACallRelation cr : crs) {
                rukiaCallRelationsList.add(cr);
            }
        }

//        for (String s : projectClasses) {
//            System.out.println(s);
//        }

        // filter the call relation that caller or callee class is not in project
        filterCallRelation();

        constructCallMap();
    }

    private void constructCallMap() {
        for (JDACallRelation cr : rukiaCallRelationsList) {
            String caller = cr.getCaller();
            String callee = cr.getCallee();

            if (callGraphMap.containsKey(caller)) {
                Vector<String> vector = callGraphMap.get(caller);
                if (!vector.contains(callee)) {
                    vector.add(callee);
                }
            } else {
                Vector<String> vector = new Vector<>();
                vector.add(callee);
                callGraphMap.put(caller, vector);
            }
        }
    }

    private void filterCallRelation() {
        Iterator iterator = rukiaCallRelationsList.iterator();
        while (iterator.hasNext()) {
            JDACallRelation cr = (JDACallRelation) iterator.next();
            String caller = cr.getCaller();
            String callee = cr.getCallee();

            String callerClass = JavaElement.getClassName(caller);
            String[] callerTokens = callerClass.split("\\.");
            String calleeClass = JavaElement.getClassName(callee);
            String[] calleeTokens = calleeClass.split("\\.");

            String callerClassName = callerTokens[callerTokens.length - 1];
            String calleeClassName = calleeTokens[calleeTokens.length - 1];

            if (!projectClasses.contains(callerClassName) || !projectClasses.contains(calleeClassName)) {
                iterator.remove();
            }
        }
    }

    public Hashtable<String, Vector<String>> getCallGraphMap() {
        return callGraphMap;
    }

    public static void main(String[] args) {
//        ProjectCallRelationsStaticAnalyser analyser = new ProjectCallRelationsStaticAnalyser("data/Connect");
        ProjectCallRelationAnalyser analyser = new ProjectCallRelationAnalyser("data/iTrust");
    }
}
