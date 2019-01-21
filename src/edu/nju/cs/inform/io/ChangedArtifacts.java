package edu.nju.cs.inform.io;

import edu.nju.cs.inform.core.diff.CodeElementsComparer;
import edu.nju.cs.inform.core.type.ChangeType;
import edu.nju.cs.inform.core.type.CodeElementChange;
import edu.nju.cs.inform.core.type.ElementType;
import edu.nju.cs.inform.util._;

import java.util.HashSet;
import java.util.LinkedHashSet;

/**
 * Created by niejia on 15/11/8.
 */
public class ChangedArtifacts {

    private HashSet<String> addedArtifactList;
    private HashSet<String> removedArtifactList;
    private HashSet<String> modifiedArtifactList;

    private HashSet<String> wholeChangedArtifactList;

    private HashSet<String> fieldsList;
    private HashSet<String> methodsList;

//    private HashSet<String> AddedFieldsList;
//    private HashSet<String> AddedMethodsList;
//    private HashSet<String> RemovedFieldsList;
//    private HashSet<String> RemovedMethodsList;
//    private HashSet<String> ModifiedMethodsList;

    public void parse(String path) {
        String input = _.readFile(path);
        String lines[] = input.split("\n");

        addedArtifactList = new LinkedHashSet<>();
        removedArtifactList = new LinkedHashSet<>();
        modifiedArtifactList = new LinkedHashSet<>();
        wholeChangedArtifactList = new LinkedHashSet<>();

        fieldsList = new LinkedHashSet<>();
        methodsList = new LinkedHashSet<>();

        for (String line : lines) {
            if (line.startsWith("Added")) {
                getAddedArtifactList().add(line.split(" ")[2]);

                String type = line.split(" ")[1];
                if (type.equals("Field")) {
                    getFieldsList().add(line.split(" ")[2]);
                } else if (type.equals("Method")) {
                    getMethodsList().add(line.split(" ")[2]);
                }
            } else if (line.startsWith("Removed")) {
                getRemovedArtifactList().add(line.split(" ")[2]);

                String type = line.split(" ")[1];
                if (type.equals("Field")) {
                    getFieldsList().add(line.split(" ")[2]);
                } else if (type.equals("Method")) {
                    getMethodsList().add(line.split(" ")[2]);
                }
            } else if (line.startsWith("Changed")) {
                getModifiedArtifactList().add(line.split(" ")[2]);

                String type = line.split(" ")[1];
                if (type.equals("Field")) {
                    getFieldsList().add(line.split(" ")[2]);
                } else if (type.equals("Method")) {
                    getMethodsList().add(line.split(" ")[2]);
                }
            }
        }

        getWholeChangedArtifactList().addAll(getAddedArtifactList());
        getWholeChangedArtifactList().addAll(getRemovedArtifactList());
        getWholeChangedArtifactList().addAll(getModifiedArtifactList());
    }

    public boolean isAddedArtifact(String artifactName) {
        return getAddedArtifactList().contains(artifactName);
    }

    public boolean isAddedMethod(String artifactName) {
        return getAddedArtifactList().contains(artifactName) && getMethodsList().contains(artifactName);
    }

    public boolean isRemovedMethod(String artifactName) {
        return getRemovedArtifactList().contains(artifactName) && getMethodsList().contains(artifactName);
    }

    public boolean isModifiedMethod(String artifactName) {
        return getModifiedArtifactList().contains(artifactName) && getMethodsList().contains(artifactName);
    }

    public boolean isAddedField(String artifactName) {
        return getAddedArtifactList().contains(artifactName) && getFieldsList().contains(artifactName);
    }

    public boolean isRemovedField(String artifactName) {
        return getRemovedArtifactList().contains(artifactName) && getFieldsList().contains(artifactName);
    }

    public boolean isField(String artifactName) {
        return fieldsList.contains(artifactName);
    }

    public boolean isMethod(String artifactName) {
        return methodsList.contains(artifactName);
    }

    public boolean isRemovedArtifact(String artifactName) {
        return getRemovedArtifactList().contains(artifactName);
    }

    public boolean isModifiedArtifact(String artifactName) {
        return getModifiedArtifactList().contains(artifactName);
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Added elements: " + getAddedArtifactList().size());
        for (String e : getAddedArtifactList()) {
            sb.append(e);
            sb.append("\n");
        }
        sb.append("\n");
        sb.append("Removed elements: " + getRemovedArtifactList().size());
        for (String e : getRemovedArtifactList()) {
            sb.append(e);
            sb.append("\n");
        }
        sb.append("\n");
        sb.append("changed elements: " + getModifiedArtifactList().size());
        for (String e : getModifiedArtifactList()) {
            sb.append(e);
            sb.append("\n");
        }
        return sb.toString();
    }

    public HashSet<String> getAddedArtifactList()
    {
        return addedArtifactList;
    }

    public HashSet<String> getFieldsList() {
        return fieldsList;
    }

    public HashSet<String> getMethodsList() {
        return methodsList;
    }

    public HashSet<String> getRemovedArtifactList() {
        return removedArtifactList;
    }

    public HashSet<String> getModifiedArtifactList() {
        return modifiedArtifactList;
    }

    public HashSet<String> getWholeChangedArtifactList() {
        return wholeChangedArtifactList;
    }

    public HashSet<String> getAddedMethodsList() {
        HashSet<String> result = new LinkedHashSet<>();
        for (String str : addedArtifactList) {
            if (isMethod(str)) {
                result.add(str);
            }
        }
        return result;
    }

    public HashSet<String> getRemovedMethodsList() {
        HashSet<String> result = new LinkedHashSet<>();
        for (String str : removedArtifactList) {
            if (isMethod(str)) {
                result.add(str);
            }
        }
        return result;
    }

    public HashSet<String> getRemovedFieldsList() {
        HashSet<String> result = new LinkedHashSet<>();
        for (String str : removedArtifactList) {
            if (isField(str)) {
                result.add(str);
            }
        }
        return result;
    }

    public HashSet<String> getAddedFieldsList() {
        HashSet<String> result = new LinkedHashSet<>();
        for (String str : addedArtifactList) {
            if (isField(str)) {
                result.add(str);
            }
        }
        return result;
    }

    public void parse(CodeElementsComparer codeElementsComparer) {

        addedArtifactList = new LinkedHashSet<>();
        removedArtifactList = new LinkedHashSet<>();
        modifiedArtifactList = new LinkedHashSet<>();
        wholeChangedArtifactList = new LinkedHashSet<>();

        fieldsList = new LinkedHashSet<>();
        methodsList = new LinkedHashSet<>();

        for (CodeElementChange change : codeElementsComparer.getMethodFieldsChangesList()) {
            if (change.getChangeType().equals(ChangeType.Added)) {
                getAddedArtifactList().add(change.getElementName());

                ElementType type = change.getElementType();
                if (type.equals(ElementType.Field)) {
                    getFieldsList().add(change.getElementName());
                } else if (type.equals(ElementType.Method)) {
                    getMethodsList().add(change.getElementName());
                }
            } else if (change.getChangeType().equals(ChangeType.Removed)) {
                getRemovedArtifactList().add(change.getElementName());

                ElementType type = change.getElementType();
                if (type.equals(ElementType.Field)) {
                    getFieldsList().add(change.getElementName());
                } else if (type.equals(ElementType.Method)) {
                    getMethodsList().add(change.getElementName());
                }
            } else if (change.getChangeType().equals(ChangeType.Unchanged)) {
                getModifiedArtifactList().add(change.getElementName());

                ElementType type = change.getElementType();
                if (type.equals(ElementType.Field)) {
                    getFieldsList().add(change.getElementName());
                } else if (type.equals(ElementType.Method)) {
                    getMethodsList().add(change.getElementName());
                }
            }
        }

        getWholeChangedArtifactList().addAll(getAddedArtifactList());
        getWholeChangedArtifactList().addAll(getRemovedArtifactList());
        getWholeChangedArtifactList().addAll(getModifiedArtifactList());
    }
}
