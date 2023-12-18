package semantics.visitors;

import ast.*;
import rs.etf.pp1.symboltable.concepts.Struct;
import semantics.TabExtended;
import semantics.util.LogUtils;

import java.util.Objects;

public class ExprVisitor extends VisitorAdaptor {
    private final SemanticPass semanticPass;

    ExprVisitor(SemanticPass semanticPass) {
        this.semanticPass = semanticPass;
    }

    public void visit(CondFactRelop condFactRelop) {
        Struct lType = condFactRelop.getExpr().struct;
        Struct rType = condFactRelop.getExpr1().struct;

        String relationOp = condFactRelop.getRelop().string;

        if (!lType.compatibleWith(rType)) {
            LogUtils.logError("Error on line " +
                    condFactRelop.getLine() + ": incompatible expression for types " +
                    LogUtils.kindToString(lType.getKind())
                    + " and " + LogUtils.kindToString(rType.getKind()));
        } else if ((lType.getKind() == Struct.Class || lType.getKind() == Struct.Array) &&
                !Objects.equals(relationOp, "==") && !Objects.equals(relationOp, "!=")) {
            LogUtils.logError("Error on line " +
                    condFactRelop.getLine() + ": incompatible relation operator for type " +
                    LogUtils.kindToString(lType.getKind()));
        }
    }

    public void visit(ExprAddop exprAddop) {
        Struct lType = exprAddop.getExpr().struct;
        Struct rType = exprAddop.getTerm().struct;

        if (lType.getKind() != Struct.Int || !lType.compatibleWith(rType)) {
            LogUtils.logError("Error on line " +
                    exprAddop.getLine() + ": incompatible add expression for types " +
                    LogUtils.kindToString(lType.getKind())
                    + " and " + LogUtils.kindToString(rType.getKind()));

            exprAddop.struct = TabExtended.noType;
        } else {
            exprAddop.struct = lType;
        }
    }

    public void visit(ExprPos exprPos) {
        exprPos.struct = exprPos.getTerm().struct;
    }

    public void visit(ExprNeg exprNeg) {
        Struct type = exprNeg.getTerm().struct;
        if (type.getKind() != Struct.Int) {
            LogUtils.logError("Error on line " +
                    exprNeg.getLine() + ": incompatible negation for type " +
                    LogUtils.kindToString(type.getKind()));

            exprNeg.struct = TabExtended.noType;
        } else {
            exprNeg.struct = type;
        }
    }

    public void visit(TermMulop termMulop) {
        Struct lType = termMulop.getTerm().struct;
        Struct rType = termMulop.getFactor().struct;

        if (lType.getKind() != Struct.Int || !lType.compatibleWith(rType)) {
            LogUtils.logError("Error on line " +
                    termMulop.getLine() + ": incompatible mul expression for types " +
                    LogUtils.kindToString(lType.getKind())
                    + " and " + LogUtils.kindToString(rType.getKind()));

            termMulop.struct = TabExtended.noType;
        } else {
            termMulop.struct = lType;
        }
    }

    public void visit(TermFactor termFactor) {
        termFactor.struct = termFactor.getFactor().struct;
    }

    public void visit(FactorDesignator factorDesignator) {
        factorDesignator.struct = factorDesignator.getDesignator().obj.getType();
    }

    public void visit(FactorFunctionCall factorFunctionCall) {
        factorFunctionCall.struct = factorFunctionCall.getDesignatorOpCall().obj.getType();
    }

    public void visit(FactorNumConst factorNumConst) {
        factorNumConst.struct = TabExtended.intType;
    }

    public void visit(FactorCharConst factorCharConst) {
        factorCharConst.struct = TabExtended.charType;
    }

    public void visit(FactorBoolConst factorBoolConst) {
        factorBoolConst.struct = TabExtended.boolType;
    }

    public void visit(FactorNewArray factorNewArray) {
        Struct exprType = factorNewArray.getExpr().struct;
        if (exprType.getKind() != Struct.Int) {
            LogUtils.logError("Error on line " +
                    factorNewArray.getLine() + ": incompatible array indexing with type " +
                    LogUtils.kindToString(exprType.getKind()));

            factorNewArray.struct = TabExtended.noType;
        } else {
            // Create new array type
            factorNewArray.struct = new Struct(Struct.Array, factorNewArray.getType().struct);
        }
    }

    public void visit(FactorNewClass factorNewClass) {
        // TODO classes
    }

    public void visit(FactorExpr factorExpr) {
        factorExpr.struct = factorExpr.getExpr().struct;
    }


}
