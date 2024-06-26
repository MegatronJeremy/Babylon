package semantics.visitors;

import ast.*;
import rs.etf.pp1.symboltable.concepts.Obj;
import rs.etf.pp1.symboltable.concepts.Struct;
import semantics.adaptors.TabExtended;
import semantics.util.LogUtils;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class StatementVisitor extends VisitorAdaptor {
    private final SemanticPass semanticPass;
    private final Set<Integer> validIOOperands = new HashSet<>(Arrays.asList(Struct.Int, Struct.Char, Struct.Bool));
    private final Set<Integer> validReadObjTypes = new HashSet<>(Arrays.asList(Obj.Var, Obj.Elem, Obj.Fld));

    StatementVisitor(SemanticPass semanticPass) {
        this.semanticPass = semanticPass;
    }

    public void visit(ConditionIf conditionIf) {
        int conditionKind = conditionIf.getCondition().struct.getKind();

        if (conditionKind != Struct.Bool) {
            LogUtils.logError("If statement condition type "
                            + LogUtils.structKindToString(conditionKind)
                            + " is not allowed",
                    conditionIf);
        }
    }

    public void visit(StatementBreak statementBreak) {
        if (!semanticPass.inForLoop) {
            LogUtils.logError("Break usage not allowed outside of for loop",
                    statementBreak);
        }
    }

    public void visit(StatementContinue statementContinue) {
        if (!semanticPass.inForLoop) {
            LogUtils.logError("Continue usage not allowed outside of for loop",
                    statementContinue);
        }
    }

    public void visit(StatementRead statementRead) {
        int designatorKind = statementRead.getDesignator().obj.getKind();
        int designatorType = statementRead.getDesignator().obj.getType().getKind();

        if (!validReadObjTypes.contains(designatorKind)) {
            LogUtils.logError("Invalid designator type "
                            + LogUtils.objKindToString(designatorKind)
                            + " for read operation",
                    statementRead);
            return;
        }

        if (!validIOOperands.contains(designatorType)) {
            LogUtils.logError("Read expression not allowed with type "
                            + LogUtils.structKindToString(designatorType),
                    statementRead);
        }
    }

    public void visit(StatementPrint statementPrint) {
        int kind = statementPrint.getExpr().struct.getKind();

        if (!validIOOperands.contains(kind)) {
            LogUtils.logError("Print expression not allowed with type "
                            + LogUtils.structKindToString(kind),
                    statementPrint);
        }
    }

    public void visit(ForEnter forEnter) {
        // entering for loop
        semanticPass.inForLoop = true;
    }

    public void visit(StatementFor statementFor) {
        // exiting for loop
        this.semanticPass.inForLoop = false;
    }

    public void visit(StatementScoped statementScoped) {
        // Close scope and destroy local variables
        TabExtended.closeScope();
    }

    public void visit(StatementScopedOpen statementScopedOpen) {
        // Open new scope for local block
        TabExtended.openScope();
    }

    public void visit(StatementReturnExprExists statementReturnExprExists) {
        if (this.semanticPass.currentMethod == null) {
            LogUtils.logError("Return expression only allowed within a function",
                    statementReturnExprExists);

            return;
        }

        this.semanticPass.returnFound = true;

        Struct currentMethodType = this.semanticPass.currentMethod.getType();
        Struct returnType = statementReturnExprExists.getExpr().struct;

        if (!returnType.assignableTo(currentMethodType)) {
            LogUtils.logError("Return type "
                            + returnType
                            + " is not assignable to the return type "
                            + currentMethodType
                            + " of function "
                            + this.semanticPass.currentMethod.getName(),
                    statementReturnExprExists);
        }
    }

}
