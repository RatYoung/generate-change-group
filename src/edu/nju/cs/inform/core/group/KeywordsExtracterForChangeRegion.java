package edu.nju.cs.inform.core.group;

import edu.nju.cs.inform.core.diff.CodeElementsComparer;
import edu.nju.cs.inform.core.preprocess.ArtifactPreprocessor;
import edu.nju.cs.inform.io.ChangedArtifacts;
import edu.nju.cs.inform.io.CorpusExtractor;
import edu.nju.cs.inform.util.JavaElement;

import java.util.*;

/**
 * Created by niejia on 16/1/16.
 */
public class KeywordsExtracterForChangeRegion {

    private ChangedArtifacts changedArtifacts;
    private Map<String, HashSet<String>> changeRegions;
    private List<HashSet<String>> changedArtifactsGroup;
    private CorpusExtractor newCorpus;
    private CorpusExtractor oldCorpus;
    private String commitVersion;
    private Map<String, List<String>> methodChangeMapping;
    private String groupsFromReleasePath;

    private List<Set<String>> finalRegionsList;

    private Map<String, Set<String>> elementsInGroupList;

//    private

    private String iTrustRegionForRelease = "/change_region_release";
//
    private String exportDirPath;
    private String methodChangeMappingPath = "data/iTrust/artifacts_for_changes.txt";


    private ChangedArtifacts changedMethods;
    private ChangedArtifacts changedFields;

    private String exportElementsInGroupPath;
    private String exportPathForElements;
    private Map<String, String> changeGroupDescriptions;
    private CodeElementsComparer codeElementsComparer;

    public KeywordsExtracterForChangeRegion(CodeElementsComparer codeElementsComparer, ChangedArtifacts changedArtifacts, Map<String, HashSet<String>> changeRegions, List<HashSet<String>> changedArtifactsGroup, CorpusExtractor newCorpus, CorpusExtractor oldCorpus) {
        this.changedArtifacts = changedArtifacts;
        this.changeRegions = changeRegions;
        this.changedArtifactsGroup = changedArtifactsGroup;
        this.newCorpus = newCorpus;
        this.oldCorpus = oldCorpus;

        this.finalRegionsList = new ArrayList<>();
        this.changeGroupDescriptions = new LinkedHashMap<>();
        this.elementsInGroupList = new LinkedHashMap<>();
        this.codeElementsComparer = codeElementsComparer;
        mergeChangeRegion();
        extractKeywords();
    }

    private void mergeChangeRegion() {
        for (Set<String> changedGroup : changedArtifactsGroup) {
            Set<String> region = new LinkedHashSet<>();
            for (String changedArtifact : changedGroup) {
                Set<String> regionForArtifact = changeRegions.get(changedArtifact);
                if (regionForArtifact != null) {
                    for (String v : regionForArtifact) {
                        region.add(v);
                    }
                } else {
                    region.add(changedArtifact);
                }
            }
            finalRegionsList.add(region);
        }
    }

    private void extractKeywords() {
        int regionNumber = 1;

        for (Set<String> region : finalRegionsList) {
            StringBuilder sb = new StringBuilder();

            for (String v : region) {
                String packageIdentifier = JavaElement.getPackageName(v);
                String classIdentifier = JavaElement.getIdentifier(JavaElement.getClassName(v));
                String methodIdentifier = JavaElement.getIdentifier(v);

                if (changedArtifacts.isAddedMethod(v)) {
//                      String methodParameters = getMethodParameters(v, newCorpus);
                    String methodParameters = newCorpus.getMethodParameters(v);
//                    String methodDoc = getMethodDoc(v, newCorpus);
                    String methodDoc = newCorpus.getMethodComments(v);
                    sb.append(classIdentifier);
                    sb.append("\n");
                    sb.append(methodIdentifier);
                    sb.append("\n");
                    sb.append(methodParameters);
                    sb.append("\n");
                    sb.append(methodDoc);
                    sb.append("\n");

                } else if (changedArtifacts.isRemovedMethod(v)) {
//                    String methodParameters = getMethodParameters(v, oldCorpus);
//                    String methodDoc = getMethodDoc(v, oldCorpus);
                    String methodParameters = oldCorpus.getMethodParameters(v);
                    String methodDoc = oldCorpus.getMethodComments(v);
                    sb.append(classIdentifier);
                    sb.append("\n");
                    sb.append(methodIdentifier);
                    sb.append("\n");
                    sb.append(methodParameters);
                    sb.append("\n");
                    sb.append(methodDoc);
                    sb.append("\n");
                } else if (changedArtifacts.isModifiedMethod(v)) {
//                    String methodParameters = getMethodParameters(v, newCorpus);
                    String methodParameters = newCorpus.getMethodParameters(v);
                    sb.append(classIdentifier);
                    sb.append("\n");
                    sb.append(methodIdentifier);
                    sb.append("\n");
                    sb.append(methodParameters);
                    sb.append("\n");

                } else if (changedArtifacts.isAddedField(v)) {
                    String fieldIdentifier = JavaElement.getIdentifier(v);
                    String className = JavaElement.getIdentifier(JavaElement.getClassName(v));
                    sb.append(className + " " + fieldIdentifier);
                    sb.append(" ");
                } else if (changedArtifacts.isRemovedField(v)) {
                    String fieldIdentifier = JavaElement.getIdentifier(v);
                    String className = JavaElement.getIdentifier(JavaElement.getClassName(v));
                    sb.append(className + " " + fieldIdentifier);
                    sb.append(" ");
                } else {
//                    String methodParameters = getMethodParameters(v, newCorpus);
                    String methodParameters = newCorpus.getMethodParameters(v);
                    sb.append(classIdentifier);
                    sb.append("\n");
                    sb.append(methodIdentifier);
                    sb.append("\n");
                    sb.append(methodParameters);
                    sb.append("\n");
                }
            }

            //Maybe we need add every method's class doc into method region
            Set<String> extractedDocClass = new HashSet<>();
            for (String s : region) {
                if (changedArtifacts.isAddedArtifact(s) || changedArtifacts.isRemovedArtifact(s)) {
                    String className = JavaElement.getClassName(s);
                    if (!extractedDocClass.contains(className)) {
                        String classdoc = extractClassDoc(className);
//                        System.out.println(" className = " + className );
//                        System.out.println(" classdoc = " + classdoc );
                        extractedDocClass.add(className);
                        sb.append(classdoc);
                        sb.append("\n");
                        if (changedArtifacts.isAddedArtifact(s)) {
                        } else if (changedArtifacts.isRemovedArtifact(s)) {
                        }
                    }
                }
            }

            sb.append("\n");

            if (!sb.toString().equals("")) {
                changeGroupDescriptions.put("Group" + regionNumber, ArtifactPreprocessor.handleJavaFile(sb.toString()));
//                _.writeFile(ArtifactPreprocessor.handleJavaFile(ArtifactPreprocessor.handleJavaFile(sb.toString())), exportDirPath + "/" + commitVersion + "/Group" + regionNumber + ".txt");
//                _.writeFile(ArtifactPreprocessor.handleJavaFile(sb.toString()), exportDirPath + "_not_preprocessed/" + commitVersion + "/Group" + regionNumber + ".txt");
            }

            Set<String> methodsInGroup = new LinkedHashSet<>();
            for (String method : finalRegionsList.get(regionNumber-1)) {
                if (codeElementsComparer.getChangedMethodsCollection().keySet().contains(method)) {
                    methodsInGroup.add(method);
                }
            }

            elementsInGroupList.put("Group" + regionNumber, methodsInGroup);
            regionNumber++;
        }
    }

    public Map<String, Set<String>> getElementsInGroupList() {
        return elementsInGroupList;
    }

    private String extractClassDoc(String className) {
//        String oldClassDoc = getClassDoc(className, oldCorpus);
//        String newClassDoc = getClassDoc(className, newCorpus);
        String oldClassDoc = oldCorpus.getClassComments(className);
        String newClassDoc = newCorpus.getClassComments(className);

        if (newClassDoc != null) {
            return newClassDoc;
        } else if (oldClassDoc != null) {
            return oldClassDoc;
        } else {
            return "";
        }
    }

    public void showFinalRegion() {
        int i = 1;
        for (Set<String> region : finalRegionsList) {
            System.out.println("Region " + i + ":");
            int j = 1;
            for (String s : region) {
                System.out.println(j+": "+s);
                j++;
            }
            i++;

        }
    }

    public Map<String, String> getChangeGroupDescriptions() {
        return changeGroupDescriptions;
    }
}
