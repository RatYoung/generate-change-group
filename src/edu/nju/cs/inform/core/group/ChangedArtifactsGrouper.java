package edu.nju.cs.inform.core.group;


import edu.nju.cs.inform.io.ChangedArtifacts;
import edu.nju.cs.inform.io.CorpusExtractor;
import edu.nju.cs.inform.core.relation.CallRelationGraph;
import edu.nju.cs.inform.core.relation.graph.CodeVertex;
import edu.nju.cs.inform.util.JavaElement;

import java.util.*;

/**
 * Created by niejia on 16/1/16.
 */
public class ChangedArtifactsGrouper {

    private ChangedArtifacts changedArtifacts;
    private CallRelationGraph callGraphForChangedPart;

    private List<HashSet<String>> changedArtifactsGroup;
    private HashMap<String,Boolean> isMethodMerged;

    private ChangedArtifacts changedMethods;
    private ChangedArtifacts changedFields;

    private CorpusExtractor newCorpus;
    private CorpusExtractor oldCorpus;

    public ChangedArtifactsGrouper(ChangedArtifacts changedArtifacts, CallRelationGraph callGraphForChangedPart, Map<String, HashSet<String>> initialChangeRegion) {
        this.callGraphForChangedPart = callGraphForChangedPart;
        this.changedArtifacts = changedArtifacts;
        this.changedArtifactsGroup = new ArrayList<>();

        groupChangedArtifact();
    }

    public ChangedArtifactsGrouper(ChangedArtifacts changedArtifacts, CallRelationGraph callGraphForChangedPart, Map<String, HashSet<String>> initialChangeRegion, CorpusExtractor newCorpus, CorpusExtractor oldCorpus) {
        this.callGraphForChangedPart = callGraphForChangedPart;
        this.changedArtifacts = changedArtifacts;
        this.changedArtifactsGroup = new ArrayList<>();

        this.newCorpus = newCorpus;
        this.oldCorpus = oldCorpus;

        this.newCorpus = newCorpus;


        groupChangedArtifact();
    }

    // concerned added, removed, modified artifacts
    private void groupChangedArtifact() {
        isMethodMerged = new LinkedHashMap<>();
        HashSet<String> visitedVertexName = new HashSet<>();

        for (String vn : changedArtifacts.getWholeChangedArtifactList()) {
            if (changedArtifacts.isMethod(vn)) {
                if (!visitedVertexName.contains(vn)) {
                    List<CodeVertex> vertexRegion = new ArrayList<>();

                    // The artifact has no call relation with others
                    if (callGraphForChangedPart.getCodeVertexByName(vn) != null) {
                        callGraphForChangedPart.searhNeighbourConnectedGraphByCall(vn, vertexRegion);
                        HashSet<String> subGraph = new HashSet<>();
                        subGraph.add(vn);
                        for (CodeVertex cv : vertexRegion) {
                            visitedVertexName.add(cv.getName());
                            subGraph.add(cv.getName());
                        }
                        changedArtifactsGroup.add(subGraph);
                    } else {
                        HashSet<String> region = new HashSet<>();
                        region.add(vn);
                        changedArtifactsGroup.add(region);
                    }
                }
            }
        }

//        FieldUseageParser fieldUseageParser_old = new FieldUseageParser(changedArtifacts.getRemovedFieldsList(), changedArtifacts.getRemovedMethodsList(), oldCorpus);
//        FieldUseageParser fieldUseageParser_new = new FieldUseageParser(changedArtifacts.getAddedFieldsList(), changedArtifacts.getAddedMethodsList(), newCorpus);
//
//        Hashtable<String, Vector<String>> newFieldUseageGraphMap = fieldUseageParser_new.getFieldUseageRelationsList();
//        Hashtable<String, Vector<String>> oldFieldUseageGraphMap = fieldUseageParser_old.getFieldUseageRelationsList();
//
//        Set<String> allocatedFieldsList = new LinkedHashSet<>();
//
//        for (int i = 0; i < changedArtifactsGroup.size(); i++) {
//            Set<String> region = changedArtifactsGroup.get(i);
//            Iterator it = region.iterator();
//
//            Set<String> fieldsInRegion = new LinkedHashSet<>();
//            while (it.hasNext()) {
//                String method = (String) it.next();
//                Vector<String> usedField = new Vector<>();
//                if (changedArtifacts.isAddedMethod(method)) {
//                    usedField = newFieldUseageGraphMap.get(method);
//                } else if (changedArtifacts.isRemovedMethod(method)) {
//                    usedField = oldFieldUseageGraphMap.get(method);
//                }
//
//                for (String field : usedField) {
//                    fieldsInRegion.add(field);
//                }
//
//            }
//
//            for (String field : fieldsInRegion) {
//                region.add(field);
//                allocatedFieldsList.add(field);
//            }
//
//            for (String field : fieldsInRegion) {
//                System.out.println(" field = " + field );
//            }
//        }
//
//        Set<String> fieldsNotAllocated = new LinkedHashSet<>();
//        for (String f : changedArtifacts.getFieldsList()) {
//            if (!allocatedFieldsList.contains(f)) {
//                fieldsNotAllocated.add(f);
//            }
//        }
//        HashSet<String> region = new HashSet<>();
//        for (String f : fieldsNotAllocated) {
//            region.add(f);
//        }
//        changedArtifactsGroup.add(region);
//
//        System.out.println("Not allocated fields: " + fieldsNotAllocated);

        //remove all method which is just changed in method body
        removeModifiedArtifacts();

        cleanEmptyRegion();

        // remove java method like equals, finalize
        removeJavaSpecificMethod();

        cleanEmptyRegion();

        // Merge left single method(added or removed) into the existed group (the class that method belongs to appears most times)
        mergeSingleMethodToExistedGroup();

        // merge the separated method which is added or removed
        mergeSeparatedMethod();

        // remove the region has only one method, ant it's a java specific method, like hasCode, equals, <init>
        // should <init> to be removed
        removeRegionContainsOnlyOneJavaSpecificMethod();

        cleanEmptyRegion();

        FieldUseageParser fieldUseageParser_old = new FieldUseageParser(changedArtifacts.getRemovedFieldsList(), changedArtifacts.getRemovedMethodsList(), oldCorpus);
        FieldUseageParser fieldUseageParser_new = new FieldUseageParser(changedArtifacts.getAddedFieldsList(), changedArtifacts.getAddedMethodsList(), newCorpus);

        Hashtable<String, Vector<String>> newFieldUseageGraphMap = fieldUseageParser_new.getFieldUseageRelationsList();
        Hashtable<String, Vector<String>> oldFieldUseageGraphMap = fieldUseageParser_old.getFieldUseageRelationsList();

        Set<String> allocatedFieldsList = new LinkedHashSet<>();

        for (int i = 0; i < changedArtifactsGroup.size(); i++) {
            Set<String> region = changedArtifactsGroup.get(i);
            Iterator it = region.iterator();

            Set<String> fieldsInRegion = new LinkedHashSet<>();
            while (it.hasNext()) {
                String method = (String) it.next();
                Vector<String> usedField = new Vector<>();
                if (changedArtifacts.isAddedMethod(method)) {
                    usedField = newFieldUseageGraphMap.get(method);
                } else if (changedArtifacts.isRemovedMethod(method)) {
                    usedField = oldFieldUseageGraphMap.get(method);
                }

                for (String field : usedField) {
                    fieldsInRegion.add(field);
                }

            }

            for (String field : fieldsInRegion) {
                region.add(field);
                allocatedFieldsList.add(field);
            }

            for (String field : fieldsInRegion) {
//                System.out.println(" field = " + field );
            }
        }

        Set<String> fieldsNotAllocated = new LinkedHashSet<>();
        for (String f : changedArtifacts.getFieldsList()) {
            if (!allocatedFieldsList.contains(f)) {
                fieldsNotAllocated.add(f);
            }
        }


        for (String f : fieldsNotAllocated) {
            HashSet<String> region = new HashSet<>();
            region.add(f);
            changedArtifactsGroup.add(region);
        }
//
//        System.out.println("Not allocated fields: " + fieldsNotAllocated);
//
////        Merge left single method(added or removed) into the existed group (the class that method belongs to appears most times)
        mergeSingleMethodToExistedGroup();
//
////        merge the separated method which is added or removed
        mergeSeparatedMethod();
        cleanEmptyRegion();

        HashSet<String> leftFieldIntoOneRegion = new HashSet<>();
        Iterator iterator = changedArtifactsGroup.iterator();
        while (iterator.hasNext()) {
            HashSet<String> region = (HashSet<String>) iterator.next();

            if (region.size() == 1) {
                String content = null;
                for (String s : region) {
                    content = s;
                }
                if (fieldsNotAllocated.contains(content)) {
                    leftFieldIntoOneRegion.add(content);
                    iterator.remove();
                }
            }
        }

        if (leftFieldIntoOneRegion.size() > 0) {
            if (changedArtifactsGroup.size() == 0) {
                changedArtifactsGroup.add(leftFieldIntoOneRegion);
            }
        }

//
//        AddFieldNeighboursIntoChangeRegion();
    }

    private void AddFieldNeighboursIntoChangeRegion() {
        Map<String, Set<String>> addedFieldsNeighbours = fetchFieldNeighbours(changedArtifacts.getAddedFieldsList(), newCorpus);
        Map<String, Set<String>> removedFieldsNeighbours = fetchFieldNeighbours(changedArtifacts.getRemovedFieldsList(), oldCorpus);


        for (int i = 0; i < changedArtifactsGroup.size(); i++) {
            Set<String> region = changedArtifactsGroup.get(i);
            Iterator it = region.iterator();

            Set<String> fieldsMethodNeighboursInRegion = new LinkedHashSet<>();
            while (it.hasNext()) {
                String artifact = (String) it.next();
                Set<String> nbs = null;
                if (changedArtifacts.isAddedField(artifact)) {
//                    System.out.println("added field = " + artifact );
                    nbs = addedFieldsNeighbours.get(artifact);
                } else if (changedArtifacts.isRemovedField(artifact)) {
//                    System.out.println("removed field = " + artifact );
                    nbs = removedFieldsNeighbours.get(artifact);
                }
//                System.out.println("nbs " + nbs);
                if (nbs != null) {
                    for (String m : nbs) {
                        fieldsMethodNeighboursInRegion.add(m);
                    }
                }
            }

            for (String m : fieldsMethodNeighboursInRegion) {
                region.add(m);
            }
        }

    }

    // find the neighbour method used this field
    private Map<String, Set<String>> fetchFieldNeighbours(HashSet<String> fieldsList, CorpusExtractor corpus) {
        Set<String> classesForField = new LinkedHashSet<>();
        for (String field : fieldsList) {
            classesForField.add(JavaElement.getClassName(field));
        }

        HashSet<String> methodsInClasses = new LinkedHashSet<>();
        for (String c : classesForField) {
            for (String m : corpus.getMethodsInClass(c)) {
                methodsInClasses.add(m);
            }
        }


        FieldUseageParser fieldUseageParser = new FieldUseageParser(fieldsList, methodsInClasses, corpus);
        Hashtable<String, Vector<String>> fieldUseageGraphMap = fieldUseageParser.getFieldUseageRelationsList();

        Map<String, Set<String>> fieldMethodsNeighbours = new LinkedHashMap<>();
        for (String f : fieldsList) {
            fieldMethodsNeighbours.put(f, new HashSet<String>());
        }

        for (String method : fieldUseageGraphMap.keySet()) {
            for (String f : fieldUseageGraphMap.get(method)) {
                Set<String> nbs = fieldMethodsNeighbours.get(f);
                nbs.add(method);
                fieldMethodsNeighbours.put(f, nbs);
            }
        }

        return fieldMethodsNeighbours;
    }


    private void mergeSingleMethodToExistedGroup() {
        for (int i = 0; i < changedArtifactsGroup.size(); i++) {
            HashSet<String> region = changedArtifactsGroup.get(i);
            if (region.size() == 1) {
                String singleMethod = null;
                for (String s : region) {
                    singleMethod = s;
                }

                if (changedArtifacts.getAddedArtifactList().contains(singleMethod) || changedArtifacts.getRemovedArtifactList().contains(singleMethod)) {
                    mergeSingleMethodIntoOneExistedRegion(singleMethod, changedArtifactsGroup);
                } else {
                    // ignore method that only changed in method body
                }
            }
        }

        // remove the single method which has already bean merged into other region
        removeMergedMethod();
    }

    private void removeModifiedArtifacts() {
        for (int i = 0; i < changedArtifactsGroup.size(); i++) {
            HashSet<String> region = changedArtifactsGroup.get(i);

            for (String changedMethod : changedArtifacts.getModifiedArtifactList()) {
                region.remove(changedMethod);
            }
        }
    }

    private void removeJavaSpecificMethod() {
        for (int i = 0; i < changedArtifactsGroup.size(); i++) {
            HashSet<String> region = changedArtifactsGroup.get(i);
            Iterator it = region.iterator();

            while (it.hasNext()) {
                String method = (String) it.next();
                String identifier = JavaElement.getIdentifier(method);
                if (identifier.equals("toString") ||
                        identifier.equals("finalize") ||
                        identifier.equals("equals")) {
                    System.out.println("Remove method: " + method);
                    it.remove();
                }
            }
        }
    }

    private void cleanEmptyRegion() {
        Iterator it = changedArtifactsGroup.iterator();
        while (it.hasNext()) {
            HashSet<String> r = (HashSet<String>) it.next();
            if (r.size() == 0) {
                it.remove();
            }
        }
    }

    private void removeMergedMethod() {
        Iterator it = changedArtifactsGroup.iterator();
        while (it.hasNext()) {
            HashSet<String> r = (HashSet<String>) it.next();
            if (r.size() == 1) {
                String m = null;
                for (String s : r) {
                    m = s;
                }

                if (isMethodMerged.get(m) != null && isMethodMerged.get(m) == true) {
                    it.remove();

                }
            }
        }
    }

    private void removeRegionContainsOnlyChangedMethod() {
        Iterator it = changedArtifactsGroup.iterator();
        while (it.hasNext()) {
            HashSet<String> r = (HashSet<String>) it.next();
            boolean isAllMethodChangesAreModified = true;
            for (String m : r) {
                if (changedArtifacts.getAddedArtifactList().contains(m) || changedArtifacts.getRemovedArtifactList().contains(m)) {
                    isAllMethodChangesAreModified = false;
                }
            }

            if (isAllMethodChangesAreModified) {
                it.remove();
            }
        }
    }

    private void mergeSeparatedMethod() {
        Map<String, List<String>> separatedClassMethod = new HashMap<>();
        Iterator it = changedArtifactsGroup.iterator();
        while (it.hasNext()) {
            HashSet<String> r = (HashSet<String>) it.next();
            if (r.size() == 1) {
                String m = null;
                for (String s : r) {
                    m = s;
                }

                String className = JavaElement.getClassName(m);
                if (separatedClassMethod.get(className) == null) {
                    List<String> methods = new ArrayList<>();
                    methods.add(m);
                    separatedClassMethod.put(className, methods);
                } else {
                    List<String> methods = separatedClassMethod.get(className);
                    methods.add(m);
                    separatedClassMethod.put(className, methods);
                }

                it.remove();
            }
        }


        for (String className : separatedClassMethod.keySet()) {
            List<String> methods = separatedClassMethod.get(className);
            changedArtifactsGroup.add(new HashSet<String>(methods));
        }
    }

    private void removeRegionContainsOnlyOneJavaSpecificMethod() {
        Iterator it = changedArtifactsGroup.iterator();
        while (it.hasNext()) {
            HashSet<String> region = (HashSet<String>) it.next();
            if (region.size() == 1) {
                String methodIdentifier = "";
                for (String s : region) {
                    methodIdentifier = JavaElement.getIdentifier(s);
                }

                if (methodIdentifier.equals("hashCode") || methodIdentifier.equals("<init>")) {
                    it.remove();
                }
            }
        }
    }

    private void mergeSingleMethodIntoOneExistedRegion(String singleMethod, List<HashSet<String>> changeRegion) {

        String className = JavaElement.getClassName(singleMethod);
//        // if this class doesn't appears in another region, keep this region independent
        int num = 0;
        for (int i = 0; i < changeRegion.size(); i++) {
            if (getAppearTimesInRegion(className, changeRegion.get(i)) > 0) {
                num++;
            }
        }
        if (num <= 1) {
            isMethodMerged.put(singleMethod, false);
            return;
        }

        HashSet<String> appearClassNameMostTimesRegion = changeRegion.get(0);
        int mostTime = 0;
        for (int i = 0; i < changeRegion.size(); i++) {
            HashSet<String> region = changeRegion.get(i);
            if (region.size() > 1) {
                int appearTimes = getAppearTimesInRegion(className, region);
                if (appearTimes > mostTime) {
                    appearClassNameMostTimesRegion = region;
                    mostTime = appearTimes;
                }
            }
        }

        if (mostTime > 0) {
            appearClassNameMostTimesRegion.add(singleMethod);
            isMethodMerged.put(singleMethod, true);
        } else {
            isMethodMerged.put(singleMethod, false);
        }
    }

    private int getAppearTimesInRegion(String className, HashSet<String> region) {
        int appearTime = 0;
        for (String s : region) {
            if (JavaElement.getClassName(s).equals(className)) {
                appearTime++;
            }
        }
        return appearTime;
    }

    public List<HashSet<String>> getChangedArtifactsGroup() {
        return changedArtifactsGroup;
    }
}
