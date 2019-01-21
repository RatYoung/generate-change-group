package edu.nju.cs.inform.core.jdt;

import edu.nju.cs.inform.core.diff.SourceCodeElements;
import edu.nju.cs.inform.util._;
import org.eclipse.jdt.core.dom.*;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by niejia on 15/11/8.
 */
public class JavaElementsParser {

    private final CompilationUnit root;
    private TypeDeclaration typeDec;
    /*
    Struct elements in a java file
     */
    private String packageName;
    private String className;
    private String classDoc;
    private List<JMethod> jMethodList;
    private List<JField> jFieldList;

    private String strContent;

    private SourceCodeElements sourceCodeElements;


    // For convenient export parsed file
    private Map<String, String> methodNameContentMap;
    private String classContent;

    public JavaElementsParser(String javaSource, SourceCodeElements sourceCodeElements) {

        this.strContent = extractStrContent(javaSource);
        this.sourceCodeElements = sourceCodeElements;

        ASTParser parsert = ASTParser.newParser(AST.JLS3);
        parsert.setSource(javaSource.toCharArray());
        this.root = (CompilationUnit) parsert.createAST(null);

        packageName = root.getPackage().getName().getFullyQualifiedName();
        sourceCodeElements.addPackage(packageName);
        // types represent all class in this file, includes public and non-public class

        List types = root.types();

        if (!types.isEmpty()) {
            // types.get(0) is the first class in this file, in most case is the public class
            this.typeDec = (TypeDeclaration) types.get(0);

            className = typeDec.getName().toString();
            sourceCodeElements.addClass(packageName + "." + className);
//            System.out.println(" packageName + className = " + packageName + "." + className );
//            sourceCodeElements.addClass(className);

            if (typeDec.getJavadoc() != null) {

                classDoc = typeDec.getJavadoc().toString();
            } else {
                classDoc = "";
            }

            sourceCodeElements.addClassComments(packageName + "." + className, classDoc);

            parseClass();
        } else {
            System.out.println(("No Class exists in this java file"));
            return;
        }
    }

    /**
     * Handle field like a = new B();
     * @param s
     * @return
     */
    private String handleFiledFormat(String s) {
        boolean isContainsEqual = false;
        for (int i = 0; i < s.length(); i++) {
            if (s.charAt(i) == '=') {
                isContainsEqual = true;
            }
        }

        if (isContainsEqual) {
            return s.split("=")[0];
        } else return s;
    }

    private void parseClass() {
//        System.out.printf("Parsing %s...\n", className);
        jFieldList = new ArrayList<>();

        for (FieldDeclaration field : typeDec.getFields()) {
            String type = field.getType().toString();

            for (Object fragment : field.fragments()) {
                JField jf = new JField();
                jf.setTypeName(field.getType().toString());

                String fieldName = fragment.toString();
                if (fieldName.endsWith("=null")) {
                    fieldName = fieldName.split("=null")[0];
                }

                String f = handleFiledFormat(fieldName);

                jf.setFieldName(handleFiledFormat(fieldName));
                jFieldList.add(jf);
            }
        }

//        System.out.printf("%d fields in %s.\n", jFieldList.size(), className);

        parseMethod();

        StringBuilder sb = new StringBuilder();
        sb.append(className);
        sb.append(" ");

        for (JField field : jFieldList) {
            sb.append(field.getFieldName());
            sb.append(" ");
            sourceCodeElements.addField(packageName + "." + className + "." + field.getFieldName());
//            sourceCodeElements.addField(className + "." + field.getFieldName());
        }

        for (JMethod method : jMethodList) {
            sb.append(method.getMethodName());
            sb.append(" ");

            StringBuffer parameters = new StringBuffer();
            for (String p : method.getParaNameList()) {
                sb.append(p);
                sb.append(" ");
                parameters.append(p);
                parameters.append(" ");
            }
//            sourceCodeElements.addMethod(packageName + "." + className + "." + method.getMethodName());
//            System.out.println(" packageName  + className + method.getMethodName() = " + packageName + "." + className + "." + method.getMethodName() );
            sourceCodeElements.addMethod(method.getMethodName());
            sourceCodeElements.addMethodBody(method.getMethodName(),method.getMethodBody());
            sourceCodeElements.addMethodComments(method.getMethodName(), method.getDoc());
            sourceCodeElements.addMethodParameters(method.getMethodName(), parameters.toString());
        }

        sb.append("\n");

        for (JMethod method : jMethodList) {
            sb.append(method.getDoc());
            sb.append(" ");
        }

        sb.append(classDoc);
        sb.append("\n");

//        sb.append(strContent);

//        System.out.println(sb);
        classContent = sb.toString();
    }

    private void parseMethod() {

        methodNameContentMap = new LinkedHashMap<>();
        // is this check required ?
        PackageDeclaration packetDec = root.getPackage();
        if (packetDec == null) {
            _.abort("PackageDeclaration is null");
        }

        jMethodList = new ArrayList<>();

        for (MethodDeclaration method : typeDec.getMethods()) {
            JMethod jm = new JMethod();
            jm.setClassName(className);
            String myMethodName = method.getName().toString();
            if (myMethodName.equals(className)) {
                myMethodName = "<init>";
            }
            jm.setMethodName(packageName + "." + className + "." + myMethodName);
            jm.setMethodBody(method.getBody()==null?"null":method.getBody().toString());
            for (Object obj : method.parameters()) {
                SimpleName paraName = ((SingleVariableDeclaration) obj).getName();
                jm.addParaName(paraName.toString());
            }
            if (method.getJavadoc() != null) {
                jm.setDoc(method.getJavadoc().toString());
            }
//            System.out.println(jm);
                    methodNameContentMap.put(className + "#" + jm.getMethodName(), jm.toString());
            jMethodList.add(jm);
        }

//        System.out.printf("%d methods in %s.\n", jMethodList.size(), className);
    }

    private String extractPureMethodName(String name) {
        String tokens[] = name.split("\\.");
        return tokens[tokens.length - 1];
    }

    private String extractStrContent(String input) {
        StringBuffer sb = new StringBuffer();

        Pattern p = Pattern.compile("\\\".*\\\"");
        Matcher m = p.matcher(input);

        while (m.find()) {
            String content = m.group().trim();
            sb.append(content.substring(1, content.length()-1));
            sb.append(" ");
        }
        return sb.toString();
    }

    public static void main(String[] args) {
    }

    public static void parse(String contentInCode, SourceCodeElements sourceCodeElements) {

    }
}
