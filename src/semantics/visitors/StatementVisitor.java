package semantics.visitors;

import ast.*;
import rs.etf.pp1.symboltable.concepts.Struct;
import semantics.TabExtended;
import semantics.util.LogUtils;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class StatementVisitor extends VisitorAdaptor {
    private final SemanticPass semanticPass;
    private final Set<Integer> validPrintResults = new HashSet<>(Arrays.asList(Struct.Int, Struct.Char, Struct.Bool));

    StatementVisitor(SemanticPass semanticPass) {
        this.semanticPass = semanticPass;
    }

    public void visit(StatementPrint statementPrint) {
        int kind = statementPrint.getExpr().struct.getKind();

        if (!validPrintResults.contains(kind)) {
            LogUtils.logError("Error on line " + statementPrint.getLine()
                    + ": print expression not allowed with type "
                    + LogUtils.kindToString(kind));
        }
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
        this.semanticPass.returnFound = true;
        Struct currentMethodType = this.semanticPass.currentMethod.getType();
        Struct returnType = statementReturnExprExists.getExpr().struct;

        if (!currentMethodType.compatibleWith(returnType)) {
            String kind = LogUtils.kindToString(returnType.getKind());

            LogUtils.logError("Error on line " + statementReturnExprExists.getLine()
                    + ": return expression type " + kind + " is not compatible "
                    + "with the return type of function "
                    + this.semanticPass.currentMethod.getName());
        }
    }
}
