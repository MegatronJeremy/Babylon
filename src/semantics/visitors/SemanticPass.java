package semantics.visitors;

import ast.*;
import codegen.visitors.ExprCodeVisitor;
import rs.etf.pp1.symboltable.concepts.Obj;
import rs.etf.pp1.symboltable.concepts.Scope;
import rs.etf.pp1.symboltable.concepts.Struct;
import semantics.adaptors.TabExtended;
import semantics.util.LogUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

public class SemanticPass extends VisitorAdaptor {

    private static SemanticPass instance = null;
    // but nested calls are possible!
    final VisitorAdaptor programVisitor = new ProgramVisitor(this);
    final VisitorAdaptor namespaceVisitor = new NamespaceVisitor(this);
    final VisitorAdaptor constDeclVisitor = new ConstDeclVisitor(this);
    final VisitorAdaptor varDeclVisitor = new VarDeclVisitor(this);
    final VisitorAdaptor typeVisitor = new TypeVisitor(this);
    final VisitorAdaptor methodVisitor = new MethodVisitor(this);
    final VisitorAdaptor statementVisitor = new StatementVisitor(this);
    final VisitorAdaptor designatorVisitor = new DesignatorVisitor(this);
    final VisitorAdaptor exprVisitor = new ExprVisitor(this);
    final VisitorAdaptor classVisitor = new ClassVisitor(this);
    final VisitorAdaptor exprCodeVisitor = new ExprCodeVisitor();
    final Stack<String> currentMethodCalledStack = new Stack<>();
    final Stack<Boolean> currentMethodCallIsClassStack = new Stack<>();
    final Stack<DesignatorIndOpDot> currentMethodCallNodeStack = new Stack<>();
    final Map<String, Struct> currentMethodsToOverload = new HashMap<>();
    Obj currentMethod = null;
    // nested function declarations not possible
    String currentMethodName = null;
    MethodTypeName currentMethodTypeName = null;
    // no inner classes - no need for stack
    Obj currentClass = null;
    String currentClassCoreName = null;
    boolean returnFound = false;
    boolean inForLoop = false;
    boolean inStaticDef = false;
    int nVars = 0;
    String currentNamespace = "";
    Scope programScope = null;

    private SemanticPass() {
    }

    public static SemanticPass getInstance() {
        if (instance == null) {
            instance = new SemanticPass();
        }

        return instance;
    }

    public int getVars() {
        return nVars;
    }

    public boolean canDeclareMethod(String name, Struct rType) {
        if (currentMethodsToOverload.containsKey(name)) {
            Struct lType = currentMethodsToOverload.get(name);

            if (rType.assignableTo(lType)) {
                LogUtils.logInfo("Overloading function " + name + " with return type " + lType);
                currentMethodsToOverload.remove(name);

                return true;
            }
        }
        return false;
    }

    boolean designatorNameInFunctionCall(SyntaxNode designatorName) {
        return designatorName.getParent().getParent().getClass() == DesignatorOpCall.class;
    }

    boolean designatorIndOpInFunctionCall(SyntaxNode designatorIndOpDot) {
        return designatorIndOpDot.getParent().getClass() == DesignatorOpCall.class;
    }

    String getNamespaceQualifiedName(String name) {
        return currentNamespace.isEmpty() ? name : currentNamespace + "::" + name;
    }

    String getQualifiedNameDeclaration(String name) {
        if (inStaticDef) {
            assert currentClass != null;

            // extra qualifier for static fields
            return currentClass.getName() + "." + name;
        }

        if (TabExtended.currentScope == programScope) {
            // return name qualified to scope
            return getNamespaceQualifiedName(name);
        } else {
//            LogUtils.logInfo("Returning unqualified name");
            // return as unqualified name (local to an inner scope)
            return name;
        }
    }

    String getQualifiedNameLookup(String name) {
//        LogUtils.logInfo("Returning fully qualified name");

        if (currentClass != null && !inStaticDef) {
            // check if field is a static class field for all the classes along the hierarchy
            Struct currentClassTypeToCheck = currentClass.getType();
            while (currentClassTypeToCheck != TabExtended.noType) {
                String classQualifiedName = currentClassTypeToCheck + "." + name;

                if (TabExtended.find(classQualifiedName) != TabExtended.noObj) {
                    return classQualifiedName;
                }

                currentClassTypeToCheck = currentClassTypeToCheck.getElemType();
            }
        }

        // return non-static class fields
        return getNamespaceQualifiedName(name);
    }

    int currentVarDeclObjKind() {
        return currentClass != null && currentMethod == null && !inStaticDef ? Obj.Fld : Obj.Var;
    }

    public boolean passed() {
        return !LogUtils.isErrorDetected();
    }

    public void visit(ProgName progName) {
        programVisitor.visit(progName);
    }

    public void visit(Program program) {
        programVisitor.visit(program);
    }

    public void visit(NamespaceName namespaceName) {
        namespaceVisitor.visit(namespaceName);
    }

    public void visit(NamespaceDecl namespaceDecl) {
        namespaceVisitor.visit(namespaceDecl);
    }

    public void visit(ConstDecl constDecl) {
        constDeclVisitor.visit(constDecl);
    }

    public void visit(ConstListExists constList) {
        constDeclVisitor.visit(constList);
    }

    public void visit(ConstListEmpty constList) {
        constDeclVisitor.visit(constList);
    }

    public void visit(ConstNum constNum) {
        constDeclVisitor.visit(constNum);
    }

    public void visit(ConstChar constChar) {
        constDeclVisitor.visit(constChar);
    }

    public void visit(ConstBool constBool) {
        constDeclVisitor.visit(constBool);
    }

    public void visit(VarScalar varScalar) {
        varDeclVisitor.visit(varScalar);
    }

    public void visit(VarArray varArray) {
        varDeclVisitor.visit(varArray);
    }

    public void visit(VarDeclCorrect varDecl) {
        varDeclVisitor.visit(varDecl);
    }

    public void visit(VarDeclError varDecl) {
        varDeclVisitor.visit(varDecl);
    }

    public void visit(VarListMultiple varListMultiple) {
        varDeclVisitor.visit(varListMultiple);
    }

    public void visit(VarListSingle varListSingle) {
        varDeclVisitor.visit(varListSingle);
    }

    public void visit(VarListError varListError) {
        varDeclVisitor.visit(varListError);
    }

    public void visit(ClassDecl classDecl) {
        classVisitor.visit(classDecl);
    }

    public void visit(ClassName className) {
        classVisitor.visit(className);
    }

    public void visit(ExtendsClauseEmpty extendsClauseEmpty) {
        classVisitor.visit(extendsClauseEmpty);
    }

    public void visit(ExtendsClauseExists extendsClauseExists) {
        classVisitor.visit(extendsClauseExists);
    }

    public void visit(StaticVarDeclListExists staticVarDeclList) {
        classVisitor.visit(staticVarDeclList);
    }

    public void visit(StaticVarDeclListEmpty staticVarDeclList) {
        classVisitor.visit(staticVarDeclList);
    }

    public void visit(StaticInitializerStart staticInitializerStart) {
        classVisitor.visit(staticInitializerStart);
    }

    public void visit(StaticInitListEmpty staticInitListEmpty) {
        classVisitor.visit(staticInitListEmpty);
    }

    public void visit(StaticInitializer staticInitializer) {
        classVisitor.visit(staticInitializer);
    }

    public void visit(TypeNoNamespace typeNoNamespace) {
        typeVisitor.visit(typeNoNamespace);
    }

    public void visit(TypeNamespace typeNamespace) {
        typeVisitor.visit(typeNamespace);
    }

    public void visit(MethodDecl methodDecl) {
        methodVisitor.visit(methodDecl);
    }

    public void visit(MethodTypeName methodTypeName) {
        methodVisitor.visit(methodTypeName);
    }

    public void visit(MethodTypeExists methodTypeExists) {
        methodVisitor.visit(methodTypeExists);
    }

    public void visit(MethodTypeVoid methodTypeVoid) {
        methodVisitor.visit(methodTypeVoid);
    }

    public void visit(FormParsListSingle formParsListSingle) {
        methodVisitor.visit(formParsListSingle);
    }

    public void visit(FormParsListMultiple formParsListMultiple) {
        methodVisitor.visit(formParsListMultiple);
    }

    public void visit(FormParsListError formParsListError) {
        methodVisitor.visit(formParsListError);
    }

    public void visit(ActParsExists actParsExists) {
        methodVisitor.visit(actParsExists);
    }

    public void visit(ActParsEmpty actParsEmpty) {
        methodVisitor.visit(actParsEmpty);
    }

    public void visit(ActParsMultiple actParsMultiple) {
        methodVisitor.visit(actParsMultiple);
    }

    public void visit(ActParsSingle actParsSingle) {
        methodVisitor.visit(actParsSingle);
    }

    public void visit(ConditionIf conditionIf) {
        statementVisitor.visit(conditionIf);
    }

    public void visit(StatementBreak statementBreak) {
        statementVisitor.visit(statementBreak);
    }

    public void visit(StatementContinue statementContinue) {
        statementVisitor.visit(statementContinue);
    }

    public void visit(StatementRead statementRead) {
        statementVisitor.visit(statementRead);
    }

    public void visit(StatementPrint statementPrint) {
        statementVisitor.visit(statementPrint);
    }

    public void visit(ForEnter forEnter) {
        statementVisitor.visit(forEnter);
    }

    public void visit(StatementFor statementFor) {
        statementVisitor.visit(statementFor);
    }

    public void visit(StatementScoped statementScoped) {
        statementVisitor.visit(statementScoped);
    }

    public void visit(StatementScopedOpen statementScopedOpen) {
        statementVisitor.visit(statementScopedOpen);
    }

    public void visit(StatementReturnExprExists statementReturnExprExists) {
        statementVisitor.visit(statementReturnExprExists);
    }

    public void visit(DesignatorAssignListStmt designatorAssignListStmt) {
        designatorVisitor.visit(designatorAssignListStmt);
    }

    public void visit(DesignatorAssignListExists designatorAssignListExists) {
        designatorVisitor.visit(designatorAssignListExists);
    }

    public void visit(DesignatorAssignListEmpty designatorAssignListEmpty) {
        designatorVisitor.visit(designatorAssignListEmpty);
    }

    public void visit(DesignatorExists designatorExists) {
        designatorVisitor.visit(designatorExists);
    }

    public void visit(DesignatorEmpty designatorEmpty) {
        designatorVisitor.visit(designatorEmpty);
    }

    public void visit(DesignatorOpAssign designatorOpAssign) {
        designatorVisitor.visit(designatorOpAssign);
    }

    public void visit(DesignatorOpCall designatorOpCall) {
        designatorVisitor.visit(designatorOpCall);
    }

    public void visit(DesignatorOpIncrement designatorOpIncrement) {
        designatorVisitor.visit(designatorOpIncrement);
    }

    public void visit(DesignatorOpDecrement designatorOpDecrement) {
        designatorVisitor.visit(designatorOpDecrement);
    }

    public void visit(DesignatorIndOpDot designatorIndOp) {
        designatorVisitor.visit(designatorIndOp);
    }

    public void visit(DesignatorIndOpBracket designatorIndOp) {
        designatorVisitor.visit(designatorIndOp);
    }

    public void visit(DesignatorArr designatorArr) {
        designatorVisitor.visit(designatorArr);
    }

    public void visit(DesignatorNoInd designatorNoInd) {
        designatorVisitor.visit(designatorNoInd);
    }

    public void visit(DesignatorNameNamespace designator) {
        designatorVisitor.visit(designator);
    }

    public void visit(DesignatorNameNoNamespace designator) {
        designatorVisitor.visit(designator);
    }

    public void visit(ConditionOR conditionOR) {
        exprVisitor.visit(conditionOR);
    }

    public void visit(ConditionCondTerm conditionCondTerm) {
        exprVisitor.visit(conditionCondTerm);
    }

    public void visit(ConditionError conditionError) {
        exprVisitor.visit(conditionError);
    }

    public void visit(CondTermAND condTermAND) {
        exprVisitor.visit(condTermAND);
    }

    public void visit(CondTermFact condTermFact) {
        exprVisitor.visit(condTermFact);
    }

    public void visit(CondFactIf condFactIf) {
        exprVisitor.visit(condFactIf);
    }

    public void visit(CondFactRelop condFactRelop) {
        exprVisitor.visit(condFactRelop);
    }

    public void visit(CondFactSingle condFactSingle) {
        exprVisitor.visit(condFactSingle);
    }

    public void visit(ExprAddop exprAddop) {
        exprVisitor.visit(exprAddop);
    }

    public void visit(ExprPos exprPos) {
        exprVisitor.visit(exprPos);
    }

    public void visit(ExprNeg exprNeg) {
        exprVisitor.visit(exprNeg);
    }

    public void visit(TermMulop termMulop) {
        exprVisitor.visit(termMulop);
    }

    public void visit(TermFactor termFactor) {
        exprVisitor.visit(termFactor);
    }

    public void visit(FactorDesignator factorDesignator) {
        exprVisitor.visit(factorDesignator);
    }

    public void visit(FactorFunctionCall factorFunctionCall) {
        exprVisitor.visit(factorFunctionCall);
    }

    public void visit(FactorNumConst factorNumConst) {
        exprVisitor.visit(factorNumConst);
    }

    public void visit(FactorCharConst factorCharConst) {
        exprVisitor.visit(factorCharConst);
    }

    public void visit(FactorBoolConst factorBoolConst) {
        exprVisitor.visit(factorBoolConst);
    }

    public void visit(FactorNewArray factorNewArray) {
        exprVisitor.visit(factorNewArray);
    }

    public void visit(FactorNewClass factorNewClass) {
        exprVisitor.visit(factorNewClass);
    }

    public void visit(FactorExpr factorExpr) {
        exprVisitor.visit(factorExpr);
    }

    public void visit(RelopEqual relopEqual) {
        exprCodeVisitor.visit(relopEqual);
    }

    public void visit(RelopNotEqual relopNotEqual) {
        exprCodeVisitor.visit(relopNotEqual);
    }

    public void visit(RelopGreater relopGreater) {
        exprCodeVisitor.visit(relopGreater);
    }

    public void visit(RelopGreaterEqual relopGreaterEqual) {
        exprCodeVisitor.visit(relopGreaterEqual);
    }

    public void visit(RelopLess relopLess) {
        exprCodeVisitor.visit(relopLess);
    }

    public void visit(RelopLessEqual relopLessEqual) {
        exprCodeVisitor.visit(relopLessEqual);
    }

    public void visit(AddopPlus addopPlus) {
        exprCodeVisitor.visit(addopPlus);
    }

    public void visit(AddopMinus addopMinus) {
        exprCodeVisitor.visit(addopMinus);
    }

    public void visit(MulopMultiply mulopMultiply) {
        exprCodeVisitor.visit(mulopMultiply);
    }

    public void visit(MulopDivide mulopDivide) {
        exprCodeVisitor.visit(mulopDivide);
    }

    public void visit(MulopModulo mulopModulo) {
        exprCodeVisitor.visit(mulopModulo);
    }

}
