package codegen.visitors;

import ast.*;

public class CodeGenerator extends VisitorAdaptor {
    private static CodeGenerator instance = null;
    private final VisitorAdaptor statementVisitor = new StatementVisitor();
    private final VisitorAdaptor methodVisitor = new MethodVisitor();
    private final VisitorAdaptor designatorVisitor = new DesignatorVisitor();
    private final VisitorAdaptor exprVisitor = new ExprVisitor();
    int mainPC;

    private CodeGenerator() {
    }

    public static CodeGenerator getInstance() {
        if (instance == null) {
            instance = new CodeGenerator();
        }

        return instance;
    }

    public int getMainPC() {
        return mainPC;
    }

    public void visit(StatementIfElseEntry statementIfElseEntry) {
        statementVisitor.visit(statementIfElseEntry);
    }

    public void visit(ConditionIf conditionIf) {
        statementVisitor.visit(conditionIf);
    }

    public void visit(ElseStatementEntry elseStatementEntry) {
        statementVisitor.visit(elseStatementEntry);
    }

    public void visit(CondFactIfEntry condFactIfEntry) {
        statementVisitor.visit(condFactIfEntry);
    }

    public void visit(StatementIfElse statementIfElse) {
        statementVisitor.visit(statementIfElse);
    }

    public void visit(CondFactRelop condFactRelop) {
        statementVisitor.visit(condFactRelop);
    }

    public void visit(CondFactSingle condFactSingle) {
        statementVisitor.visit(condFactSingle);
    }

    public void visit(CondTermFact condTermFact) {
        statementVisitor.visit(condTermFact);
    }

    public void visit(CondTermAND condTermAND) {
        statementVisitor.visit(condTermAND);
    }

    public void visit(StatementPrint statementPrint) {
        statementVisitor.visit(statementPrint);
    }

    public void visit(StatementRead statementRead) {
        statementVisitor.visit(statementRead);
    }

    public void visit(StatementReturnExprExists statementReturnExprExists) {
        statementVisitor.visit(statementReturnExprExists);
    }

    public void visit(CondFactExists condFactExists) {
        statementVisitor.visit(condFactExists);
    }

    public void visit(CondFactEmpty condFactEmpty) {
        statementVisitor.visit(condFactEmpty);
    }

    public void visit(ForEnter forEnter) {
        statementVisitor.visit(forEnter);
    }

    public void visit(ForBodyEntry forBodyEntry) {
        statementVisitor.visit(forBodyEntry);
    }

    public void visit(ForPostOpExit forPostOpExit) {
        statementVisitor.visit(forPostOpExit);
    }

    public void visit(StatementFor statementFor) {
        statementVisitor.visit(statementFor);
    }

    public void visit(StatementBreak statementBreak) {
        statementVisitor.visit(statementBreak);
    }

    public void visit(StatementContinue statementContinue) {
        statementVisitor.visit(statementContinue);
    }

    public void visit(MethodTypeName methodTypeName) {
        methodVisitor.visit(methodTypeName);
    }

    public void visit(MethodDecl methodDecl) {
        methodVisitor.visit(methodDecl);
    }

    public void visit(DesignatorOpCall designatorOpCall) {
        designatorVisitor.visit(designatorOpCall);
    }

    public void visit(DesignatorFunction designatorFunction) {
        designatorVisitor.visit(designatorFunction);
    }

    public void visit(DesignatorOpAssign designatorOpAssign) {
        designatorVisitor.visit(designatorOpAssign);
    }

    public void visit(DesignatorOpIncrement designatorOpIncrement) {
        designatorVisitor.visit(designatorOpIncrement);
    }

    public void visit(DesignatorOpDecrement designatorOpDecrement) {
        designatorVisitor.visit(designatorOpDecrement);
    }

    public void visit(DesignatorIndOpDot designatorIndOpDot) {
        designatorVisitor.visit(designatorIndOpDot);
    }

    public void visit(DesignatorArr designatorArr) {
        designatorVisitor.visit(designatorArr);
    }

    public void visit(DesignatorExists designatorExists) {
        designatorVisitor.visit(designatorExists);
    }

    public void visit(DesignatorEmpty designatorEmpty) {
        designatorVisitor.visit(designatorEmpty);
    }

    public void visit(DesignatorAssignListStmt designatorAssignListStmt) {
        designatorVisitor.visit(designatorAssignListStmt);
    }

    public void visit(FactorDesignator factorDesignator) {
        exprVisitor.visit(factorDesignator);
    }

    public void visit(FactorNewArray factorNewArray) {
        exprVisitor.visit(factorNewArray);
    }

    public void visit(ExprAddop exprAddop) {
        exprVisitor.visit(exprAddop);
    }

    public void visit(ExprNeg exprNeg) {
        exprVisitor.visit(exprNeg);
    }

    public void visit(TermMulop termMulop) {
        exprVisitor.visit(termMulop);
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


}
