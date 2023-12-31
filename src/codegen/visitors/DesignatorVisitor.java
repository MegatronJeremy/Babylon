package codegen.visitors;

import ast.*;
import codegen.util.VisitorUtils;
import rs.etf.pp1.mj.runtime.Code;
import rs.etf.pp1.symboltable.concepts.Obj;
import semantics.decorators.TabExtended;

import java.util.LinkedList;
import java.util.Stack;

public class DesignatorVisitor extends VisitorAdaptor {

    private final Stack<Boolean> designatorExistsStack = new Stack<>();

    private Obj currentDesignatorClass = null;

    private void invokeVirtualFunction(Obj obj) {
        // before invoking function class address must be passed
        Obj vftp = currentDesignatorClass.getType().getMembersTable().searchKey("@vftp");
        Code.load(vftp);
        Code.put(Code.invokevirtual); // invoke virtual with function name
        for (char c : obj.getName().toCharArray()) {
            Code.put4(c);
        }
        Code.put4(-1);
    }

    public void visit(DesignatorOpCall designatorOpCall) {
        // ord, chr and len are inserted as inline methods
        Obj obj = designatorOpCall.obj;
        switch (obj.getName()) {
            case "ord":
            case "chr":
                // do nothing - implicitly cast
                break;
            case "len":
                Code.put(Code.arraylength);
                break;
            default:
                // actual function call generation
                if (obj.getLevel() != 0) {
                    // invoke virtual function
                    // but first regen designator
                    designatorOpCall.getDesignator().traverseBottomUp(CodeGenerator.getInstance());
                    invokeVirtualFunction(obj);
                } else {
                    // normal (non-virtual) function call
                    int offset = obj.getAdr() - Code.pc; // from the current instruction
                    Code.put(Code.call);
                    Code.put2(offset);
                }
        }
    }

    public void visit(DesignatorFunction designatorFunction) {
        // single function call
        Obj obj = designatorFunction.getDesignatorOpCall().obj;

        // pop if return value exists since it is not used at this point
        if (obj.getType() != TabExtended.noType) {
            Code.put(Code.pop);
        }
    }

    public void visit(DesignatorOpAssign designatorOpAssign) {
        Obj target = designatorOpAssign.getDesignator().obj;
        Code.store(target);
    }

    public void visit(DesignatorOpIncrement designatorOpIncrement) {
        Obj target = designatorOpIncrement.getDesignator().obj;
        VisitorUtils.generateIncrement(target, 1);
    }

    public void visit(DesignatorOpDecrement designatorOpDecrement) {
        Obj target = designatorOpDecrement.getDesignator().obj;
        VisitorUtils.generateIncrement(target, -1);
    }

    public void visit(DesignatorIndOpDot designatorIndOpDot) {
        // Two cases - designator is type and designator is class

        // same as array - later get-field will be generated
        Obj cls = designatorIndOpDot.getDesignator().obj;
        if (cls.getKind() != Obj.Type) {
            // load class for field access
            Code.load(cls);

            // TODO see if this can be done better
            currentDesignatorClass = cls;
        }
        // else field is statically accessed
    }

    public void visit(DesignatorArr designatorArr) {
        // this is correct -> just generate arr address and later load will do aload
        Obj arr = designatorArr.getDesignator().obj;
        Code.load(arr);
    }

    public void visit(DesignatorNoInd designatorNoInd) {
        Obj design = designatorNoInd.obj;
        if (design.getKind() == Obj.Fld || design.getKind() == Obj.Meth && design.getLevel() != 0) {
            // load implicit this of method if in class
            Code.put(Code.load_n); // load 0-th field

            currentDesignatorClass = CodeGenerator.getInstance().currentClass;
        }
    }

    // Designator assign list statement from here on out
    public void visit(DesignatorExists designatorExists) {
        designatorExistsStack.push(true);
    }

    public void visit(DesignatorEmpty designatorEmpty) {
        designatorExistsStack.push(false);
    }

    public void visit(DesignatorAssignListStmt designatorAssignListStmt) {
        // start from end - source array is already on top
        int startIndex = designatorExistsStack.size();

        Obj source = designatorAssignListStmt.getDesignator1().obj;
        Obj destination = designatorAssignListStmt.getDesignator().obj;

        // load dst array and the src array
        Code.load(destination);
        Code.load(source);

        // first check if you went over the limit
        Code.put(Code.dup); // duplicate array source
        Code.put(Code.arraylength);
        Code.loadConst(startIndex); // if (len > index) then ok
        Code.putFalseJump(Code.le, 0); // jump over trap if ok
        int fixupAddr = Code.pc - 2;

        // trap if failed condition check
        Code.put(Code.trap);
        Code.loadConst(2);
        Code.fixup(fixupAddr);

        // start after check
        // centralized logic for designator assignment list here
        Obj helperA = TabExtended.getHelperA();
        Obj helperB = TabExtended.getHelperB();

        // put start index in helper A (index of source)
        Code.loadConst(startIndex);
        Code.store(helperA);

        // put zero in helperB (index of destination)
        Code.loadConst(0);
        Code.store(helperB);

        // create a for loop to assign to the "rest" array
        int loopStart = Code.pc;

        Code.put(Code.dup); // duplicate source for length
        Code.put(Code.arraylength); // get array length
        Code.load(helperA);
        Code.putFalseJump(Code.gt, 0); // if len <= index jump out (index goes up)
        int loopEndFixup = Code.pc - 2;

        Code.put(Code.dup2); // duplicate both for next iteration

        // load source index and load source
        Code.load(helperA);
        VisitorUtils.generateArrLoad(source);

        // load dest index and store to destination
        Code.load(helperB);
        Code.put(Code.dup_x1);
        Code.put(Code.pop);
        VisitorUtils.generateArrStore(destination);

        // post-loop condition - increment helperA and helperB
        VisitorUtils.generateIncrement(helperA, 1);
        VisitorUtils.generateIncrement(helperB, 1);

        // jump to start of loop
        Code.putJump(loopStart);
        Code.fixup(loopEndFixup);

        // now store into the non-array elements on the stack
        // on top of stack: ..., arrD, arrS -> ...
        Code.put(Code.pop); // ..., arrS, arrD
        Code.put(Code.pop); // ..., arrS

        // get designator objects
        LinkedList<Obj> designList = designatorAssignListStmt.getDesignatorAssignList().objlist;

        // start index - 1 will be the first one to generate
        while (--startIndex >= 0) {
            boolean skipNext = !designatorExistsStack.pop();

            if (skipNext) continue;

            Code.load(source);
            Code.loadConst(startIndex);
            VisitorUtils.generateArrLoad(source);

            // do it in reverse order - get end of list
            Obj nextObj = designList.pollLast();
            assert nextObj != null;
            Code.store(nextObj);
        }
    }
}
