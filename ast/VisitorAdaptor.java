// generated with ast extension for cup
// version 0.8
// 5/10/2023 21:58:9


package ast;

public abstract class VisitorAdaptor implements Visitor { 

    public void visit(Mulop Mulop) { }
    public void visit(Relop Relop) { }
    public void visit(Addop Addop) { }
    public void visit(Factor Factor) { }
    public void visit(Designator Designator) { }
    public void visit(DesignatorStatement DesignatorStatement) { }
    public void visit(Decl Decl) { }
    public void visit(Statement Statement) { }
    public void visit(ClassDecl ClassDecl) { }
    public void visit(ConstructorOrMethodDecl ConstructorOrMethodDecl) { }
    public void visit(VarType VarType) { }
    public void visit(ConstType ConstType) { }
    public void visit(MulopModulo MulopModulo) { visit(); }
    public void visit(MulopDivide MulopDivide) { visit(); }
    public void visit(MulopMultiply MulopMultiply) { visit(); }
    public void visit(AddopMinus AddopMinus) { visit(); }
    public void visit(AddopPlus AddopPlus) { visit(); }
    public void visit(RelopLessEqual RelopLessEqual) { visit(); }
    public void visit(RelopLess RelopLess) { visit(); }
    public void visit(RelopGreaterEqual RelopGreaterEqual) { visit(); }
    public void visit(RelopGreater RelopGreater) { visit(); }
    public void visit(RelopNotEqual RelopNotEqual) { visit(); }
    public void visit(RelopEqual RelopEqual) { visit(); }
    public void visit(Assignop Assignop) { visit(); }
    public void visit(Label Label) { visit(); }
    public void visit(DesignatorIndex DesignatorIndex) { visit(); }
    public void visit(DesignatorDot DesignatorDot) { visit(); }
    public void visit(DesignatorSingle DesignatorSingle) { visit(); }
    public void visit(FactorExpr FactorExpr) { visit(); }
    public void visit(FactorNewActParsOpt FactorNewActParsOpt) { visit(); }
    public void visit(FactorNewArray FactorNewArray) { visit(); }
    public void visit(FactorNew FactorNew) { visit(); }
    public void visit(FactorBoolConst FactorBoolConst) { visit(); }
    public void visit(FactorCharConst FactorCharConst) { visit(); }
    public void visit(FactorNumConst FactorNumConst) { visit(); }
    public void visit(FactorDesignatorActParsOpt FactorDesignatorActParsOpt) { visit(); }
    public void visit(FactorDesignator FactorDesignator) { visit(); }
    public void visit(NoMulopFactorList NoMulopFactorList) { visit(); }
    public void visit(MulopFactorList MulopFactorList) { visit(); }
    public void visit(Term Term) { visit(); }
    public void visit(NoAddopTermList NoAddopTermList) { visit(); }
    public void visit(AddopTermList AddopTermList) { visit(); }
    public void visit(ExprNeg ExprNeg) { visit(); }
    public void visit(Expr Expr) { visit(); }
    public void visit(CondFactRelop CondFactRelop) { visit(); }
    public void visit(CondFact CondFact) { visit(); }
    public void visit(CondTermFact CondTermFact) { visit(); }
    public void visit(CondTerm CondTerm) { visit(); }
    public void visit(ConditionCondTerm ConditionCondTerm) { visit(); }
    public void visit(Condition Condition) { visit(); }
    public void visit(NoActParsOpt NoActParsOpt) { visit(); }
    public void visit(ActParsOpt ActParsOpt) { visit(); }
    public void visit(ActParsExpr ActParsExpr) { visit(); }
    public void visit(ActPars ActPars) { visit(); }
    public void visit(NoDesignatorList NoDesignatorList) { visit(); }
    public void visit(DesignatorList DesignatorList) { visit(); }
    public void visit(DesignatorListComma DesignatorListComma) { visit(); }
    public void visit(DesignatorAssignSkipFirst DesignatorAssignSkipFirst) { visit(); }
    public void visit(DesignatorAssign DesignatorAssign) { visit(); }
    public void visit(DesignatorDec DesignatorDec) { visit(); }
    public void visit(DesignatorInc DesignatorInc) { visit(); }
    public void visit(DesignatorActParsOpt DesignatorActParsOpt) { visit(); }
    public void visit(DesignatorAssign DesignatorAssign) { visit(); }
    public void visit(NoConstNumList NoConstNumList) { visit(); }
    public void visit(ConstNumList ConstNumList) { visit(); }
    public void visit(StatementScoped StatementScoped) { visit(); }
    public void visit(StatementForEach StatementForEach) { visit(); }
    public void visit(StatementPrint StatementPrint) { visit(); }
    public void visit(StatementRead StatementRead) { visit(); }
    public void visit(StatementReturnExpr StatementReturnExpr) { visit(); }
    public void visit(StatementReturn StatementReturn) { visit(); }
    public void visit(StatementContinue StatementContinue) { visit(); }
    public void visit(StatementBreak StatementBreak) { visit(); }
    public void visit(StatementWhile StatementWhile) { visit(); }
    public void visit(StatementIfElse StatementIfElse) { visit(); }
    public void visit(StatementIf StatementIf) { visit(); }
    public void visit(StatementDesignator StatementDesignator) { visit(); }
    public void visit(NoStatementList NoStatementList) { visit(); }
    public void visit(StatementList StatementList) { visit(); }
    public void visit(Type Type) { visit(); }
    public void visit(NoFormList NoFormList) { visit(); }
    public void visit(FormList FormList) { visit(); }
    public void visit(FormPars FormPars) { visit(); }
    public void visit(NoFormParsOpt NoFormParsOpt) { visit(); }
    public void visit(FormParsOpt FormParsOpt) { visit(); }
    public void visit(MethodVoid MethodVoid) { visit(); }
    public void visit(MethodType MethodType) { visit(); }
    public void visit(MethodDecl MethodDecl) { visit(); }
    public void visit(NoMethodDeclList NoMethodDeclList) { visit(); }
    public void visit(MethodDeclList MethodDeclList) { visit(); }
    public void visit(MethodDecl MethodDecl) { visit(); }
    public void visit(ConstructorDecl ConstructorDecl) { visit(); }
    public void visit(NoConstructorOrMethodDeclList NoConstructorOrMethodDeclList) { visit(); }
    public void visit(ConstructorOrMethodDeclList ConstructorOrMethodDeclList) { visit(); }
    public void visit(NoExtendsClause NoExtendsClause) { visit(); }
    public void visit(ExtendsClause ExtendsClause) { visit(); }
    public void visit(OperationDeclBody OperationDeclBody) { visit(); }
    public void visit(ClassBody ClassBody) { visit(); }
    public void visit(ClassDeclList ClassDeclList) { visit(); }
    public void visit(VarArray VarArray) { visit(); }
    public void visit(VarScalar VarScalar) { visit(); }
    public void visit(NoVarList NoVarList) { visit(); }
    public void visit(VarList VarList) { visit(); }
    public void visit(VarDecl VarDecl) { visit(); }
    public void visit(NoVarDeclList NoVarDeclList) { visit(); }
    public void visit(VarDeclList VarDeclList) { visit(); }
    public void visit(ConstBool ConstBool) { visit(); }
    public void visit(ConstChar ConstChar) { visit(); }
    public void visit(ConstNum ConstNum) { visit(); }
    public void visit(NoConstList NoConstList) { visit(); }
    public void visit(ConstList ConstList) { visit(); }
    public void visit(ConstDecl ConstDecl) { visit(); }
    public void visit(DeclClass DeclClass) { visit(); }
    public void visit(DeclVar DeclVar) { visit(); }
    public void visit(DeclConst DeclConst) { visit(); }
    public void visit(NoDecl NoDecl) { visit(); }
    public void visit(DeclList DeclList) { visit(); }
    public void visit(Program Program) { visit(); }


    public void visit() { }
}
