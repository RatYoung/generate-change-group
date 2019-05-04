package edu.nju.cs.inform.core.diff;

import edu.nju.cs.inform.core.preprocess.ArtifactPreprocessor;
import edu.nju.cs.inform.core.type.*;
import edu.nju.cs.inform.core.group.ChangeRegionAlgorithm;
import edu.nju.cs.inform.core.group.ChangedArtifactsGrouper;
import edu.nju.cs.inform.io.ArtifactsReader;
import edu.nju.cs.inform.io.ChangedArtifacts;
import edu.nju.cs.inform.core.relation.RelationInfo;
import edu.nju.cs.inform.util.JavaElement;

import java.io.Serializable;
import java.util.*;

/**
 * Created by niejia on 16/3/15.
 */
public class CodeElementsComparer implements Serializable {

	private static final long serialVersionUID = -7983877952952295387L;
	public List<HashSet<String>> group;
	public Map<String, HashSet<String>> changeRegions;
	
	private String postfixName = ".java";

	private SourceCodeElements newVersionCodeElements;
	private SourceCodeElements oldVersionCodeElements;
	public SourceCodeElements diffCodeElements;

//    codeElementChangesList contains the whole code changes. It will be filtered, and then has the same result just like JDiff.
	private Set<CodeElementChange> codeElementChangesList;

	private Set<CodeElementChange> methodFieldsChangesList;

	private ArtifactsCollection changeDescriptionCollection;
	private RelationInfo relationInfo;

	private ArtifactsCollection preprocessedNewVersionCodeCollection;
	private Map<String, Set<String>> elementsInGroup;

	private String newVersionCodeDirPath;
	private String oldVersionCodeDirPath;

	public CodeElementsComparer(String newVersionCodeDirPath, String oldVersionCodeDirPath) {
		ArtifactsCollection newVersionCodeCollection = ArtifactsReader.getCollections(newVersionCodeDirPath,
				postfixName);
		ArtifactsCollection oldVersionCodeCollection = ArtifactsReader.getCollections(oldVersionCodeDirPath,
				postfixName);

		setPreprocessedNewVersionCodeCollection(newVersionCodeCollection);

		this.newVersionCodeElements = new SourceCodeElements(newVersionCodeCollection);
		this.oldVersionCodeElements = new SourceCodeElements(oldVersionCodeCollection);
		this.codeElementChangesList = new LinkedHashSet<>();
		this.methodFieldsChangesList = new LinkedHashSet<>();
		this.changeDescriptionCollection = new ArtifactsCollection();
		this.elementsInGroup = new LinkedHashMap<>();
		this.newVersionCodeDirPath = newVersionCodeDirPath;
		this.oldVersionCodeDirPath = oldVersionCodeDirPath;
	}

	public void diff() {
		identifyChanges(newVersionCodeElements.getPackagesList(), oldVersionCodeElements.getPackagesList(),
				ElementType.Package);
		identifyChanges(newVersionCodeElements.getClassesList(), oldVersionCodeElements.getClassesList(),
				ElementType.Class);
		identifyChanges(newVersionCodeElements.getMethodsList(), oldVersionCodeElements.getMethodsList(),
				ElementType.Method);
		identifyChanges(newVersionCodeElements.getFieldsList(), oldVersionCodeElements.getFieldsList(),
				ElementType.Field);

		findMethodFieldChanges();
		// filter the level duplication
		filterOn();
//        extractChangeDescription();

		ChangeRegionAlgorithm algorithm = new ChangeRegionAlgorithm(this);
		group = algorithm.group;
		changeRegions = algorithm.changeRegions;
	}

	private void findMethodFieldChanges() {
		for (CodeElementChange change : codeElementChangesList) {
			if (change.getElementType().equals(ElementType.Method)
					|| change.getElementType().equals(ElementType.Field)) {
				methodFieldsChangesList.add(change);
			}
		}
	}

	private void filterOn() {

		Set<String> changedPackagesList = getChangedElementsByElementType(ElementType.Package);
		Set<String> changedClassesList = getChangedElementsByElementType(ElementType.Class);

		Iterator iterator1 = codeElementChangesList.iterator();

		while (iterator1.hasNext()) {
			CodeElementChange codeElementChange = (CodeElementChange) iterator1.next();
			if (codeElementChange.getElementType().equals(ElementType.Class)) {
				String className = codeElementChange.getElementName();
				if (classBelongsToChangedPackage(className, changedPackagesList)) {
					iterator1.remove();
				}
			}
		}

		Iterator iterator2 = codeElementChangesList.iterator();

		while (iterator2.hasNext()) {
			CodeElementChange codeElementChange = (CodeElementChange) iterator2.next();
			if (codeElementChange.getElementType().equals(ElementType.Method)
					|| codeElementChange.getElementType().equals(ElementType.Field)) {
				String elementName = codeElementChange.getElementName();
				if (elementBelongsToChangedClass(elementName, changedClassesList)) {
					iterator2.remove();
				}
			}
		}
	}

	private boolean elementBelongsToChangedClass(String elementName, Set<String> changedClassesList) {
		String classNameOfTarget = JavaElement.getClassName(elementName);
		for (String cp : changedClassesList) {
			if (cp.equals(classNameOfTarget)) {
				return true;
			}
		}
		return false;
	}

	private void extractChangeDescription() {
		// simply mock some change description
		for (CodeElementChange elementChange : codeElementChangesList) {
			Artifact artifact = new Artifact(elementChange.getElementName(), elementChange.getElementName());
			changeDescriptionCollection.put(elementChange.getElementName(), artifact);
		}
	}

	public void setChangeDescriptions(Map<String, String> changeDescription) {
		for (String groupID : changeDescription.keySet()) {
			Artifact artifact = new Artifact(groupID, changeDescription.get(groupID));
			changeDescriptionCollection.put(groupID, artifact);
		}
	}

	private boolean classBelongsToChangedPackage(String className, Set<String> changedPackagesList) {
		String packageNameOfTarget = JavaElement.getPackageName(className);
		for (String cp : changedPackagesList) {
			if (cp.equals(packageNameOfTarget)) {
				return true;
			}
		}
		return false;
	}

	private Set<String> getChangedElementsByElementType(ElementType elementType) {
		Set<String> changedElementsList = new LinkedHashSet<>();

		for (CodeElementChange elementChange : codeElementChangesList) {
			if (elementChange.getElementType().equals(elementType)) {
				changedElementsList.add(elementChange.getElementName());
			}
		}

		return changedElementsList;
	}

	private void identifyChanges(Set<String> elementsInNewVersionCode, Set<String> elementsInOldVersionCode,
			ElementType elementType) {
		Set<String> unchangedElements = new LinkedHashSet<>();

		for (String p : elementsInNewVersionCode) {
			if (elementsInOldVersionCode.contains(p)) {
				unchangedElements.add(p);
			}
		}

		for (String p : elementsInNewVersionCode) {
			if (!unchangedElements.contains(p)) {
				CodeElementChange elementChange = new CodeElementChange(p, elementType, ChangeType.Added);
				codeElementChangesList.add(elementChange);
			}
		}

		for (String p : elementsInOldVersionCode) {
			if (!unchangedElements.contains(p)) {
				CodeElementChange elementChange = new CodeElementChange(p, elementType, ChangeType.Removed);
				codeElementChangesList.add(elementChange);
			}
		}
		
		//added by yx in 2019.3.9
		//IN NEED OF identification of modified elements!!
		//if you don't want to, just uncomment the for loop below.
		if(elementType.equals(ElementType.Method)) {
			for (String p : unchangedElements) {
//				if(p.contains("com.facebook.presto.TupleInfo.Type")) {
//					System.out.println(p);
//					oldVersionCodeElements.getMethodBody(p);
//					oldVersionCodeElements.getMethodComments(p);
//					oldVersionCodeElements.getMethodParameters(p);
//				}
				String oldMethodBody = oldVersionCodeElements.getMethodBody(p);
				String newMethodBody = newVersionCodeElements.getMethodBody(p);
				if (!oldMethodBody.equals(newMethodBody)) {
					CodeElementChange elementChange = new CodeElementChange(p, elementType, ChangeType.Unchanged);
					codeElementChangesList.add(elementChange);
				}
			}
		}
		
//		//diffElementChange:
//		for(CodeElementChange p: codeElementChangesList) {
//			if(p.getElementType().equals(ElementType.Package)) {
//				diffCodeElements.addPackage(p.getElementName());
//			}
//			else if(p.getElementType().equals(ElementType.Class)) {
//				diffCodeElements.addClass(p.getElementName());
//			}
//			else if(p.getElementType().equals(ElementType.Method)) {
//				diffCodeElements.addMethod(p.getElementName());
//				if(p.getChangeType())
//			}
//			
//		}
		
//		System.out.println("fuck it");
	}

	public Set<CodeElementChange> getCodeElementChangesList() {
		return codeElementChangesList;
	}

	public Set<CodeElementChange> getMethodFieldsChangesList() {
		return methodFieldsChangesList;
	}

	public ArtifactsCollection getChangeDescriptionCollection() {
		return changeDescriptionCollection;
	}

	public ChangedArtifacts getChangedArtifacts() {
		ChangedArtifacts parser = new ChangedArtifacts();
		parser.parse(this);
		return parser;
	}

	public SourceCodeElements getNewVersionCodeElements() {
		return newVersionCodeElements;
	}

	public SourceCodeElements getOldVersionCodeElements() {
		return oldVersionCodeElements;
	}

	public void setChangedCodeElementsRelationInfo(RelationInfo relationInfo) {
		this.relationInfo = relationInfo;
	}

	public RelationInfo getChangedCodeElementsRelationInfo() {
		return relationInfo;
	}

	public ArtifactsCollection getPreprocessedNewVersionCodeCollection() {
		return preprocessedNewVersionCodeCollection;
	}

	public void setPreprocessedNewVersionCodeCollection(ArtifactsCollection codeCollection) {
		preprocessedNewVersionCodeCollection = new ArtifactsCollection();
		for (String id : codeCollection.keySet()) {
			Artifact artifact = new Artifact(id, ArtifactPreprocessor.handleJavaFile(codeCollection.get(id).text));
			preprocessedNewVersionCodeCollection.put(id, artifact);
		}
	}

	public ArtifactsCollection getChangedMethodsCollection() {
		ArtifactsCollection changedMethods = new ArtifactsCollection();
		for (CodeElementChange elementChange : methodFieldsChangesList) {
			if (elementChange.getElementType().equals(ElementType.Method)) {
				Artifact artifact = new Artifact(elementChange.getElementName(),
						ArtifactPreprocessor.handleJavaFile(elementChange.getElementName()));
				changedMethods.put(elementChange.getElementName(), artifact);
			}
		}
		return changedMethods;
	}

	public void setElementsInGroup(Map<String, Set<String>> elementsInGroup) {
		this.elementsInGroup = elementsInGroup;
	}

	public Map<String, Set<String>> getElementsInGroupList() {
		return elementsInGroup;
	}

	public Set<String> getElementsInGroup(String group) {
		return elementsInGroup.get(group);
	}

	public String getNewVersionCodeDirPath() {
		return newVersionCodeDirPath;
	}

	public String getOldVersionCodeDirPath() {
		return oldVersionCodeDirPath;
	}

	// modified by yx
	public Map<String, String> getRecommentMethodsBodyCollection() {
		Map<String, String> recommentMethodsBodyCollection = new HashMap<String, String>();
		for (CodeElementChange elementChange : methodFieldsChangesList) {
			if (elementChange.getElementType().equals(ElementType.Method)) {
				String artName = elementChange.getElementName();
				if (newVersionCodeElements.getMethodBody(artName) != null)
					recommentMethodsBodyCollection.put(artName, newVersionCodeElements.getMethodBody(artName));
				else
					recommentMethodsBodyCollection.put(artName, oldVersionCodeElements.getMethodBody(artName));
			}
		}
		return recommentMethodsBodyCollection;
	}
}
