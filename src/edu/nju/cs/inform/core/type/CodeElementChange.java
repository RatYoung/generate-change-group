package edu.nju.cs.inform.core.type;

import java.io.Serializable;

/**
 * Created by niejia on 16/3/15.
 */
public class CodeElementChange implements Serializable{
	private static final long serialVersionUID = -2955880028576209692L;
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
