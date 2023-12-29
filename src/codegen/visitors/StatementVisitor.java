package codegen.visitors;

import ast.*;
import rs.etf.pp1.mj.runtime.Code;
import rs.etf.pp1.symboltable.Tab;
import rs.etf.pp1.symboltable.concepts.Obj;
import rs.etf.pp1.symboltable.concepts.Struct;

import java.util.LinkedList;
import java.util.Queue;

public class StatementVisitor extends VisitorAdaptor {

    private final Queue<Integer> nextCondFixupList = new LinkedList<>();
    private final Queue<Integer> ifBodyFixupList = new LinkedList<>();
    boolean lastANDStatement = false;
    private Integer currentRelop;
    private Integer elseFixup;
    private Integer ifFixup;

    private void visitCondTerm(CondTerm condTerm) {
        // centralized logic for putting jumps here
        // TODO see if there is a better way to do this
        SyntaxNode parent = condTerm.getParent();

        if (parent.getClass() != ConditionCondTerm.class && parent.getClass() != ConditionOR.class) {
            // put false jump here
            Code.putFalseJump(currentRelop, 0);
            nextCondFixupList.add(Code.pc - 2);
        } else {
            SyntaxNode secondParent = parent.getParent();

            if (secondParent.getClass() != ConditionIf.class) {
                // last one in and statement list
                Code.putFalseJump(Code.inverse[currentRelop], 0); // actually a true jump
                ifBodyFixupList.add(Code.pc - 2);

                lastANDStatement = true;
            } else {
                // last one in or statement list
                Code.putFalseJump(currentRelop, 0);
                elseFixup = Code.pc - 2; // at least this must always exist
            }
        }
    }

    public void visit(ConditionIf conditionIf) {
        // fixup for if statement body
        while (!ifBodyFixupList.isEmpty()) {
            Integer nextAddr = ifBodyFixupList.poll();
            Code.fixup(nextAddr);
        }
    }

    public void visit(ElseStatementEntry elseStatementEntry) {
        Code.putJump(0);
        ifFixup = Code.pc - 2; // fixup for jumping over else statement

        Code.fixup(elseFixup);
    }

    public void visit(StatementIfElse statementIfElse) {
        // done with everything -> just jump over else statement
        Code.fixup(ifFixup);
    }

    public void visit(CondFactRelop condFactRelop) {
        currentRelop = condFactRelop.getRelop().integer;
    }

    public void visit(CondFactSingle condFactSingle) {
        currentRelop = Code.eq;
    }

    public void visit(CondTermFact condTermFact) {
        visitCondTerm(condTermFact);
    }

    public void visit(CondTermAND condTermAND) {
        visitCondTerm(condTermAND);
    }

    public void visit(CondFactIfEntry condFactIfEntry) {
        // fixup
        if (lastANDStatement) {
            while (!nextCondFixupList.isEmpty()) {
                Integer nextAddr = nextCondFixupList.poll();
                Code.fixup(nextAddr);
            }
            lastANDStatement = false;
        }
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
