package codegen.visitors;

import ast.*;
import rs.etf.pp1.mj.runtime.Code;
import rs.etf.pp1.symboltable.Tab;
import rs.etf.pp1.symboltable.concepts.Obj;
import rs.etf.pp1.symboltable.concepts.Struct;

import java.util.ArrayList;
import java.util.List;

public class StatementVisitor extends VisitorAdaptor {

    private Integer currentRelop;

    private List<Integer> currentFixupList = new ArrayList<>();

    public void visit(CondFactRelop condFactRelop) {
        SyntaxNode secondParent = condFactRelop.getParent().getParent();
        SyntaxNode thirdParent = secondParent.getParent();
        if (thirdParent.getClass() == ConditionIf.class) {
            // this is the last node before if body, jump to else needs to be put here


        } else if (secondParent.getClass() == ConditionCondTerm.class || secondParent.getClass() == ConditionOR.class) {
            // this is the last node in the hierarchy, generate true jump
        } else {
            // put a false jump here and fixup later
        }
    }

    public void visit(CondFactSingle condFactSingle) {
        currentRelop = Code.eq;
    }

    public void visit(CondTermFact condTermFact) {
        Code.putFalseJump(currentRelop, 0);
        currentFixupList.add(Code.pc - 2);
    }

    public void visit(CondTermAND condTermAND) {

    }


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
