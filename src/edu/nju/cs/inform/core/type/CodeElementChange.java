package edu.nju.cs.inform.core.type;

/**
 * Created by niejia on 16/3/15.
 */
public class CodeElementChange {

    private String elementName;
    private ElementType elementType;
    private ChangeType changeType;

    private String keywords;

    public CodeElementChange(String elementName, ElementType elementType, ChangeType changeType) {
        this.elementName = elementName;
        this.elementType = elementType;
        this.changeType = changeType;
    }

    public String getElementName() {
        return elementName;
    }

    public ElementType getElementType() {
        return elementType;
    }

    public ChangeType getChangeType() {
        return changeType;
    }
}
