package codegen.visitors;

import ast.*;
import rs.etf.pp1.mj.runtime.Code;
import rs.etf.pp1.symboltable.Tab;
import rs.etf.pp1.symboltable.concepts.Obj;
import rs.etf.pp1.symboltable.concepts.Struct;

public class StatementVisitor extends VisitorAdaptor {

    public void visit(StatementPrint statementPrint) {
        Struct type = statementPrint.getExpr().struct;

        if (type != Tab.charType) {
            Code.loadConst(5);
            Code.put(Code.print);
        } else {
            Code.loadConst(2);
            Code.put(Code.bprint);
        }
    }

    public void visit(StatementRead statementRead) {
        Obj target = statementRead.getDesignator().obj;
        Struct type = target.getType();

        if (type != Tab.charType) {
            Code.put(Code.read);
        } else {
            Code.put(Code.bread);
        }
        Code.store(target);
    }


    public void visit(StatementReturnExprExists statementReturnExprExists) {
        Code.put(Code.exit);
        Code.put(Code.return_);
    }
}
