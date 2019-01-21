package edu.nju.cs.inform.core.jda.type;

import edu.nju.cs.inform.util.JavaElement;
import edu.nju.cs.inform.util._;
import org.eclipse.jdt.core.dom.*;
import org.eclipse.core.runtime.IProgressMonitor;

import java.util.*;

/**
 * Created by niejia on 16/1/28.
 */
public class CallRelationAnalyser {
    private CompilationUnit root;
    private TypeDeclaration typeDec;

    private String packageName;
    private String className;
    private String classComment;

    private Set<JDAVariable> fieldsList;
    private Set<JDACallRelation> rukiaCallRelationsList;

    private Map<String, String> classPackageMap;


    public CallRelationAnalyser(String path) {
        String input = _.readFile(path);

        ASTParser parsert = ASTParser.newParser(AST.JLS3);
        parsert.setSource(input.toCharArray());
        this.root = (CompilationUnit) parsert.createAST(null);
        if (root == null || root.getPackage() == null) {
            System.out.println("23333");
        }
        packageName = root.getPackage().getName().getFullyQualifiedName();

        fieldsList = new LinkedHashSet<>();
        rukiaCallRelationsList = new LinkedHashSet<>();
        classPackageMap = new LinkedHashMap<>();

        parse();
        callRelationsFilter();
    }



    private void parse() {
        List types = root.types();

        if (!types.isEmpty()) {
            // types.get(0) is the first class in this file, in most case is the public class
            this.typeDec = (TypeDeclaration) types.get(0);
            className = typeDec.getName().toString();

            classComment = (typeDec.getJavadoc() != null) ? typeDec.getJavadoc().toString() : "";
        } else {
//            System.out.println(("No Class exists in this java file"));
            return;
        }

        // find import class-package mapping
        List<ImportDeclaration> importsList = root.imports();
        for (ImportDeclaration declaration : importsList) {
            String importName = declaration.getName().getFullyQualifiedName();
            String[] tokens = importName.split("\\.");

            String cName = JavaElement.getIdentifier(importName);
            String pName = JavaElement.getPackageName(importName);
            classPackageMap.put(cName, pName);
        }

        PackageDeclaration packetDec = root.getPackage();
        if (packetDec == null) {
            _.abort("PackageDeclaration is null");
        }

//        System.out.println(" packageName = " + packageName );
//        System.out.println(" className = " + className );
        analyseFields();
        analyseMethods();
    }

    private void analyseFields() {
        for (FieldDeclaration field : typeDec.getFields()) {
            Type type = field.getType();

            if (isUserDefinedType(type)) {
                for (Object o : field.fragments()) {
                    VariableDeclarationFragment fragment = (VariableDeclarationFragment) o;
                    String variableName = fragment.getName().getFullyQualifiedName();
                    JDAVariable rukiaVariable = new JDAVariable(type.toString(), variableName);
                    fieldsList.add(rukiaVariable);
                }
            }
        }
    }

    private void analyseMethods() {
        for (MethodDeclaration methodDeclaration : typeDec.getMethods()) {

            String methodName = methodDeclaration.getName().getFullyQualifiedName();
//            System.out.println("---------------------" + methodName + "---------------------");
            Block methodBodyBlock = methodDeclaration.getBody();

            Set<JDAVariable> variablesList = new LinkedHashSet<>();
            Map<String, String> identifierTypeMap = new HashMap<>();

            addFieldsToVariableList(variablesList, identifierTypeMap);
            addParametersToVariablesList(methodDeclaration, variablesList, identifierTypeMap);

            // find all statements in method body
            List<Statement> statementsList = new ArrayList<>();
            findStatementsListInMethodBody(methodBodyBlock, statementsList);

            for (Statement statement : statementsList) {

                if (statement instanceof VariableDeclarationStatement) {
                    VariableDeclarationStatement variableDeclarationStatement = (VariableDeclarationStatement) statement;
                    Type type = variableDeclarationStatement.getType();
                    if (isUserDefinedType(type)) {
                        for (Object o : variableDeclarationStatement.fragments()) {
                            VariableDeclarationFragment fragment = (VariableDeclarationFragment) o;
                            String variableName = fragment.getName().getFullyQualifiedName();
                            JDAVariable rukiaVariable = new JDAVariable(type.toString(), variableName);
                            variablesList.add(rukiaVariable);
                            identifierTypeMap.put(rukiaVariable.getIdentifer(), rukiaVariable.getType());

                            Expression expression = fragment.getInitializer();
                            if (expression instanceof MethodInvocation) {
                                handleMethodInvocation((MethodInvocation) expression, methodName, identifierTypeMap);

                            } else if (expression instanceof ClassInstanceCreation) {
                                Type t = ((ClassInstanceCreation) expression).getType();
                                if (isUserDefinedType(t)) {
                                    String tname = t.toString();
                                    createCallRelation(className, methodName, tname, "<init>");
                                }
                            }
                        }
                    }
                } else if (statement instanceof ExpressionStatement) {
                    ExpressionStatement expressionStatement = (ExpressionStatement) statement;
                    Expression expression = expressionStatement.getExpression();

                    if (expression instanceof Assignment) {
                        Assignment assignment = (Assignment) expression;
                        Expression leftSide = assignment.getLeftHandSide();
                        Expression rightSide = assignment.getRightHandSide();

                        if (rightSide instanceof MethodInvocation) {
                            handleMethodInvocation((MethodInvocation) rightSide, methodName, identifierTypeMap);

                        }
                    } else if (expression instanceof MethodInvocation) {
                        handleMethodInvocation((MethodInvocation) expression, methodName, identifierTypeMap);
                    }
                } else if (statement instanceof EnhancedForStatement) {
                    EnhancedForStatement enhancedForStatement = (EnhancedForStatement) statement;
                    SingleVariableDeclaration parameter = enhancedForStatement.getParameter();
                    Type type = parameter.getType();
                    if (isUserDefinedType(type)) {
                        String variableName = parameter.getName().getFullyQualifiedName();
                        JDAVariable rukiaVariable = new JDAVariable(type.toString(), variableName);
                        variablesList.add(rukiaVariable);
                        identifierTypeMap.put(variableName, type.toString());
                    }
                } else if (statement instanceof ReturnStatement) {
                    ReturnStatement returnStatement = (ReturnStatement) statement;
                    Expression expression = returnStatement.getExpression();

                    if (expression instanceof MethodInvocation) {
                        handleMethodInvocation((MethodInvocation) expression, methodName, identifierTypeMap);
                    }
                } else if (statement instanceof IfStatement) {
                    IfStatement ifStatement = (IfStatement) statement;
                    Expression expression = ifStatement.getExpression();

                    if (expression instanceof MethodInvocation) {
                        handleMethodInvocation((MethodInvocation) expression, methodName, identifierTypeMap);
                    }
                }
            }




            for (JDAVariable variable : variablesList) {
//                System.out.println(variable);
            }

//            System.out.println("------------------------------------------");
        }
    }

    private void handleMethodInvocation(MethodInvocation expression, String methodName, Map<String, String> identifierTypeMap) {
        MethodInvocation methodInvocation = expression;
        String calleeMethodName = methodInvocation.getName().getFullyQualifiedName();

        Expression e = methodInvocation.getExpression();
        if (e instanceof SimpleName) {
            String calleeVariableIdentifier = ((SimpleName) methodInvocation.getExpression()).getIdentifier();
            if (Character.isLowerCase(calleeVariableIdentifier.charAt(0))) {
                String calleeType = identifierTypeMap.get(calleeVariableIdentifier);
                createCallRelation(className, methodName, calleeType, calleeMethodName);
            } else if (Character.isUpperCase(calleeVariableIdentifier.charAt(0))) {
                createCallRelation(className, methodName, calleeVariableIdentifier, calleeMethodName);
            }

        } else if (e instanceof MethodInvocation) {
            MethodInvocation mi = (MethodInvocation) e;
            Expression subExp = mi.getExpression();
            if (subExp instanceof SimpleName) {
                String calleeVariableIdentifier = ((SimpleName) subExp).getIdentifier();
                String calleeType = identifierTypeMap.get(calleeVariableIdentifier);
                createCallRelation(className, methodName, calleeType, mi.getName().toString());
            }
        }
    }

    private void createCallRelation(String callerClassName, String callerMethodName, String calleeClassName, String calleeMethodName) {
        String caller = packageName + "." + callerClassName + "." + callerMethodName;
        String callee = classPackageMap.get(calleeClassName) + "." + calleeClassName + "." + calleeMethodName;
        JDACallRelation rukiaCallRelation = new JDACallRelation(caller, callee);
        rukiaCallRelationsList.add(rukiaCallRelation);
    }

    private void addFieldsToVariableList(Set<JDAVariable> variablesList, Map<String, String> identifierTypeMap) {
        for (JDAVariable rukiaVariable : fieldsList) {
            variablesList.add(rukiaVariable);
            identifierTypeMap.put(rukiaVariable.getIdentifer(), rukiaVariable.getType());
        }
    }

    private void addParametersToVariablesList(MethodDeclaration methodDeclaration, Set<JDAVariable> variablesList, Map<String, String> identifierTypeMap) {
        List<SingleVariableDeclaration> parametersList = methodDeclaration.parameters();
        for (SingleVariableDeclaration singleVariableDeclaration : parametersList) {
            Type type = singleVariableDeclaration.getType();
            if (isUserDefinedType(type)) {
                variablesList.add(new JDAVariable(type.toString(), singleVariableDeclaration.getName().getFullyQualifiedName()));
                identifierTypeMap.put(singleVariableDeclaration.getName().getFullyQualifiedName(), type.toString());
            }
        }
    }

    private void findStatementsListInMethodBody(Block methodBodyBlock, List<Statement> statementsList) {
        if (methodBodyBlock == null) return;
        for (Object o : methodBodyBlock.statements()) {
            List<Statement> subStatementsList = subStatementsRecursion((Statement) o);
            for (Statement s : subStatementsList) {
                statementsList.add(s);
            }
        }
    }

    public List<Statement> subStatementsRecursion(Statement statement) {
        List<Statement> subStatementsList = new ArrayList<>();
        subStatementsRecursion(subStatementsList, statement);
        return subStatementsList;
    }


    private void subStatementsRecursion(List<Statement> subStatementsList, Statement statement) {
        if (statement instanceof TryStatement) {
            TryStatement tryStatement = (TryStatement) statement;
            for (Object o : tryStatement.getBody().statements()) {
                subStatementsRecursion(subStatementsList, (Statement) o );
            }
        } else if (statement instanceof SynchronizedStatement) {
            SynchronizedStatement synchronizedStatement = (SynchronizedStatement) statement;
            for (Object o : synchronizedStatement.getBody().statements()) {
                subStatementsRecursion(subStatementsList, (Statement) o );
            }

        } else if (statement instanceof IfStatement) {
            IfStatement ifStatement = (IfStatement) statement;
            subStatementsList.add(ifStatement);
            Statement thenStatement = ifStatement.getThenStatement();

            if (thenStatement instanceof Block) {
                Block thenBlock = (Block) ifStatement.getThenStatement();
                for (Object o : thenBlock.statements()) {
                    subStatementsRecursion(subStatementsList, (Statement) o);
                }
            } else if (thenStatement instanceof ReturnStatement) {
                subStatementsList.add(thenStatement);
            }

            Statement s = ifStatement.getElseStatement();
            if (s instanceof Block) {
                Block elseBlock = (Block) ifStatement.getElseStatement();
                if (elseBlock != null) {
                    for (Object o : elseBlock.statements()) {
                        subStatementsRecursion(subStatementsList, (Statement) o);
                    }
                }
            } else if (s instanceof IfStatement) {
                subStatementsRecursion(subStatementsList, s);
            }
//            } else if (s instanceof ) {
//
//            }

        } else if (statement instanceof EnhancedForStatement) {
            subStatementsList.add(statement);
            EnhancedForStatement enhancedForStatement = (EnhancedForStatement) statement;
            if (enhancedForStatement.getBody() instanceof Block) {
                Block block = (Block) enhancedForStatement.getBody();
                for (Object o : block.statements()) {
                    subStatementsRecursion(subStatementsList, (Statement) o);
                }
            }

        } else if (statement instanceof Block) {
            Block block = (Block) statement;
            for (Object o : block.statements()) {
                subStatementsRecursion(subStatementsList, (Statement) o);
            }
        } else if (statement instanceof WhileStatement) {
            subStatementsList.add(statement);
            WhileStatement whileStatement = (WhileStatement) statement;

            Statement bodyStatement = whileStatement.getBody();

            if (bodyStatement instanceof Block) {
                Block body = (Block) whileStatement.getBody();
                for (Object o : body.statements()) {
                    subStatementsRecursion(subStatementsList, (Statement) o);
                }
            } else if (bodyStatement instanceof ExpressionStatement) {
                subStatementsList.add(bodyStatement);
            }

        } else {
            subStatementsList.add(statement);
        }
    }

    private boolean isUserDefinedType(Type type) {
        String str = type.toString();
//        System.out.println(" str = " + str );
//        if (str.startsWith("List<")) {
//            return false;
//        }
//
//        if (str.startsWith("Map<")) {
//            return false;
//        }
        return !type.isPrimitiveType();
    }

    private void callRelationsFilter() {
        Iterator iterator = rukiaCallRelationsList.iterator();
        while (iterator.hasNext()) {
            JDACallRelation rukiaCallRelation = (JDACallRelation) iterator.next();
            String caller = rukiaCallRelation.toString().split(" ")[0];
            String callee = rukiaCallRelation.toString().split(" ")[1];
            String[] calleeTokens = callee.split("\\.");
            String pName = calleeTokens[0];
            if (pName.equals("null")) {
                iterator.remove();
            }

            if (Character.isUpperCase(JavaElement.getIdentifier(caller).charAt(0))) {
                rukiaCallRelation.setCaller(caller + ".<init>");
            }
        }
    }

    public Set<JDACallRelation> getRukiaCallRelationsList() {
        return rukiaCallRelationsList;
    }

    public static void main(String[] args) {
//        String path = "data/AccessDAO.java";
        String path = "data/ViewPrescriptionRecordsAction.java";
//        String path = "data/OperationalProfileLoader.java";
//
        CallRelationAnalyser callRelationsStaticAnalyser = new CallRelationAnalyser(path);
//
//        String dir = "data/Connect";
//        File dirFile = new File(dir);
//
//        for (File f : dirFile.listFiles()) {
//            ClassInfo classInfo = new ClassInfo(f.getPath());
//        }
    }
}
