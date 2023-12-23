package semantics.visitors;

import ast.*;
import rs.etf.pp1.symboltable.concepts.Struct;
import semantics.adaptors.TabAdaptor;
import semantics.util.LogUtils;

import java.util.Objects;

public class ExprVisitor extends VisitorAdaptor {
    private final SemanticPass semanticPass;

    ExprVisitor(SemanticPass semanticPass) {
        this.semanticPass = semanticPass;
    }

    public void visit(ConditionOR conditionOR) {
        conditionOR.struct = TabAdaptor.boolType;
    }

    public void visit(ConditionCondTerm conditionCondTerm) {
        conditionCondTerm.struct = conditionCondTerm.getCondTerm().struct;
    }

    public void visit(ConditionError conditionError) {
        conditionError.struct = TabAdaptor.noType;
    }

    public void visit(CondTermAND condTermAND) {
        condTermAND.struct = TabAdaptor.boolType;
    }

    public void visit(CondTermFact condTermFact) {
        condTermFact.struct = condTermFact.getCondFact().struct;
    }

    public void visit(CondFactRelop condFactRelop) {
        Struct lType = condFactRelop.getExpr().struct;
        Struct rType = condFactRelop.getExpr1().struct;

        String relationOp = condFactRelop.getRelop().string;

        boolean errorFound = false;

        if (!lType.compatibleWith(rType)) {
            LogUtils.logError("Incompatible expression for types "
                            + LogUtils.structKindToString(lType.getKind())
                            + " and " + LogUtils.structKindToString(rType.getKind()),
                    condFactRelop);

            errorFound = true;
        } else if ((lType.getKind() == Struct.Class || lType.getKind() == Struct.Array) &&
                !Objects.equals(relationOp, "==") && !Objects.equals(relationOp, "!=")) {
            LogUtils.logError("Incompatible relation operator for type "
                            + LogUtils.structKindToString(lType.getKind()),
                    condFactRelop);

            errorFound = true;
        }

        condFactRelop.struct = errorFound ? TabAdaptor.noType : TabAdaptor.boolType;
    }

    public void visit(CondFactSingle condFactSingle) {
        condFactSingle.struct = condFactSingle.getExpr().struct;
    }

    public void visit(ExprAddop exprAddop) {
        Struct lType = exprAddop.getExpr().struct;
        Struct rType = exprAddop.getTerm().struct;

        if (lType.getKind() != Struct.Int || !lType.compatibleWith(rType)) {
            LogUtils.logError("Incompatible add expression for types "
                            + LogUtils.structKindToString(lType.getKind())
                            + " and " + LogUtils.structKindToString(rType.getKind()),
                    exprAddop);

            exprAddop.struct = TabAdaptor.noType;
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
            LogUtils.logError("Incompatible negation for type " + LogUtils.structKindToString(type.getKind()),
                    exprNeg);

            exprNeg.struct = TabAdaptor.noType;
        } else {
            exprNeg.struct = type;
        }
    }

    public void visit(TermMulop termMulop) {
        Struct lType = termMulop.getTerm().struct;
        Struct rType = termMulop.getFactor().struct;

        if (lType.getKind() != Struct.Int || !lType.compatibleWith(rType)) {
            LogUtils.logError("Incompatible mul expression for types "
                            + LogUtils.structKindToString(lType.getKind())
                            + " and " + LogUtils.structKindToString(rType.getKind()),
                    termMulop);

            termMulop.struct = TabAdaptor.noType;
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
        factorNumConst.struct = TabAdaptor.intType;
    }

    public void visit(FactorCharConst factorCharConst) {
        factorCharConst.struct = TabAdaptor.charType;
    }

    public void visit(FactorBoolConst factorBoolConst) {
        factorBoolConst.struct = TabAdaptor.boolType;
    }

    public void visit(FactorNewArray factorNewArray) {
        Struct exprType = factorNewArray.getExpr().struct;
        if (exprType.getKind() != Struct.Int) {
            LogUtils.logError("Incompatible array indexing with type "
                            + LogUtils.structKindToString(exprType.getKind()),
                    factorNewArray);

            factorNewArray.struct = TabAdaptor.noType;
        } else {
            // Create new array type
            factorNewArray.struct = new Struct(Struct.Array, factorNewArray.getType().struct);
        }
    }

    public void visit(FactorNewClass factorNewClass) {
        Struct type = factorNewClass.getType().struct;
        if (type.getKind() != Struct.Class) {
            LogUtils.logError("New operation called on a non-class type",
                    factorNewClass);

            factorNewClass.struct = TabAdaptor.noType;
        } else {
            factorNewClass.struct = type;
        }
    }

    public void visit(FactorExpr factorExpr) {
        factorExpr.struct = factorExpr.getExpr().struct;
    }


}
