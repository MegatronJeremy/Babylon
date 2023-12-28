package codegen.visitors;

import ast.*;
import rs.etf.pp1.mj.runtime.Code;
import rs.etf.pp1.symboltable.concepts.Obj;

public class ExprVisitor extends VisitorAdaptor {
    public void visit(FactorDesignator factorDesignator) {
        Obj target = factorDesignator.getDesignator().obj;
        Code.load(target);
    }

    public void visit(ExprAddop exprAddop) {
        Code.put(exprAddop.getAddop().integer);
    }

    public void visit(ExprNeg exprNeg) {
        Code.put(Code.neg);
    }

    public void visit(TermMulop termMulop) {
        Code.put(termMulop.getMulop().integer);
    }

    public void visit(FactorNumConst factorNumConst) {
        Code.loadConst(factorNumConst.getValue());
    }

    public void visit(FactorCharConst factorCharConst) {
        Code.loadConst(factorCharConst.getValue());
    }

    public void visit(FactorBoolConst factorBoolConst) {
        Code.loadConst(factorBoolConst.getValue() ? 1 : 0);
    }
}
