package codegen.visitors;

import ast.*;
import rs.etf.pp1.mj.runtime.Code;
import rs.etf.pp1.symboltable.Tab;
import rs.etf.pp1.symboltable.concepts.Obj;
import rs.etf.pp1.symboltable.concepts.Struct;

import java.util.LinkedList;
import java.util.Queue;
import java.util.Stack;

public class StatementVisitor extends VisitorAdaptor {

    private final Stack<Queue<Integer>> nextCondFixupListStack = new Stack<>();
    private final Queue<Integer> ifBodyFixupList = new LinkedList<>();
    private final Stack<Integer> ifFixupStack = new Stack<>();
    private final Stack<Integer> elseFixupStack = new Stack<>();
    private final Stack<Queue<Integer>> forEndFixupListStack = new Stack<>();
    private final Stack<Integer> forBodyAddrStack = new Stack<>();
    boolean lastANDStatement = false;
    private Integer currentRelop;
    private Integer forBodyFixup;
    private CondFact forCondFact;

    private boolean fixedUpLeftoverAND = false;

    private void fixupLeftoverAND() {
        if (fixedUpLeftoverAND) {
            return;
        }

        fixedUpLeftoverAND = true;

        // ALSO fixup all leftover AND false jump conditions
        Queue<Integer> nextCondFixupList = nextCondFixupListStack.pop();
        while (!nextCondFixupList.isEmpty()) {
            Integer nextAddr = nextCondFixupList.poll();
            Code.fixup(nextAddr);
        }
    }

    private void visitCondTerm(CondTerm condTerm) {
        // centralized logic for putting jumps here
        // this is probably the best way to do this
        SyntaxNode parent = condTerm.getParent();

        if (parent.getClass() != ConditionCondTerm.class && parent.getClass() != ConditionOR.class) {
            // put false jump here
            Code.putFalseJump(currentRelop, 0);
            nextCondFixupListStack.peek().add(Code.pc - 2);
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
                elseFixupStack.push(Code.pc - 2); // at least this must always exist
            }
        }
    }

    public void visit(StatementIfElseEntry statementIfElseEntry) {
        // create new list
        nextCondFixupListStack.push(new LinkedList<>());
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
        ifFixupStack.push(Code.pc - 2);
        // fixup for jumping over else statement
        // need to use a list because of nested if/else blocks

        Code.fixup(elseFixupStack.pop()); // stack because of recursion

        fixupLeftoverAND(); // if else exists jump here -> NOT OVER
    }

    public void visit(StatementIfElse statementIfElse) {
        // done with everything -> just jump over else statement
        assert !ifFixupStack.isEmpty();

        Integer nextIfFixup = ifFixupStack.pop();
        Code.fixup(nextIfFixup);

        // ALSO fixup all leftover AND false jump conditions - in case there is no else branch
        fixupLeftoverAND();
        fixedUpLeftoverAND = false; // reset this - must be done here for current scope
    }

    public void visit(CondFactRelop condFactRelop) {
        currentRelop = condFactRelop.getRelop().integer;
    }

    public void visit(CondFactSingle condFactSingle) {
        // if not equal zero condition is true
        Code.loadConst(0);
        currentRelop = Code.ne;
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
            // Only PEAK here - not done yet
            Queue<Integer> nextCondFixupList = nextCondFixupListStack.peek();
            while (!nextCondFixupList.isEmpty()) {
                Integer nextAddr = nextCondFixupList.poll();
                Code.fixup(nextAddr);
            }
            lastANDStatement = false;
        }
    }

    public void visit(CondFactExists condFactExists) {
        Code.putFalseJump(currentRelop, 0); // first time check
        forEndFixupListStack.peek().add(Code.pc - 2);

        // jump into for body
        Code.putJump(0);
        forBodyFixup = Code.pc - 2;

        // set up the condition node for regeneration
        forCondFact = condFactExists.getCondFact();

        // Jump after to check for condition and do post-op
        forBodyAddrStack.push(Code.pc);
    }

    public void visit(CondFactEmpty condFactEmpty) {
        // jump into for body
        Code.putJump(0);
        forBodyFixup = Code.pc - 2;

        forCondFact = null;

        // Jump after to check for condition and do post-op
        forBodyAddrStack.add(Code.pc);
    }

    public void visit(ForEnter forEnter) {
        // make new list
        forEndFixupListStack.push(new LinkedList<>());
    }

    public void visit(ForPostOpExit forPostOpExit) {
        // regenerate the condition
        if (forCondFact != null) {
            forCondFact.traverseBottomUp(CodeGenerator.getInstance());

            Code.putFalseJump(currentRelop, 0);
            forEndFixupListStack.peek().add(Code.pc - 2);
        }
        // else there is no condition
    }

    public void visit(ForBodyEntry forBodyEntry) {
        Code.fixup(forBodyFixup);
    }

    public void visit(StatementFor statementFor) {
        // Use stacks here because of recursion

        // put unconditional jump to check
        Code.putJump(forBodyAddrStack.pop());

        // fixup addr for end of for statement
        Queue<Integer> forEndFixupList = forEndFixupListStack.pop();
        while (!forEndFixupList.isEmpty()) {
            Integer nextAddr = forEndFixupList.poll();
            Code.fixup(nextAddr);
        }
    }

    public void visit(StatementBreak statementBreak) {
        // jump out
        Code.putJump(0);
        forEndFixupListStack.peek().add(Code.pc - 2);
    }

    public void visit(StatementContinue statementContinue) {
        // jump to start
        Code.putJump(forBodyAddrStack.peek());
    }

    public void visit(StatementPrint statementPrint) {
        Struct type = statementPrint.getExpr().struct;

        if (type != Tab.charType) {
            Code.loadConst(5);
            Code.put(Code.print);
        } else {
            Code.loadConst(1);
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
