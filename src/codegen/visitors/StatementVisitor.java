package codegen.visitors;

import ast.StatementPrint;
import ast.StatementReturn;
import ast.StatementReturnExprExists;
import ast.VisitorAdaptor;
import rs.etf.pp1.mj.runtime.Code;
import rs.etf.pp1.symboltable.Tab;
import rs.etf.pp1.symboltable.concepts.Struct;

public class StatementVisitor extends VisitorAdaptor {

    public void visit(StatementPrint statementPrint) {
        Struct type = statementPrint.getExpr().struct;

        if (type == Tab.intType) {
            Code.loadConst(5);
            Code.put(Code.print);
        } else {
            Code.loadConst(1);
            Code.put(Code.bprint);
        }
    }

    public void visit(StatementReturnExprExists statementReturnExprExists) {
        Code.put(Code.exit);
        Code.put(Code.return_);
    }
}
