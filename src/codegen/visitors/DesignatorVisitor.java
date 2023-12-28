package codegen.visitors;

import ast.*;
import rs.etf.pp1.mj.runtime.Code;
import rs.etf.pp1.symboltable.Tab;
import rs.etf.pp1.symboltable.concepts.Obj;

public class DesignatorVisitor extends VisitorAdaptor {

    public void visit(DesignatorOpCall designatorOpCall) {
        Obj obj = designatorOpCall.obj;
        int offset = obj.getAdr() - Code.pc; // from the current instruction
        Code.put(Code.call);

        Code.put2(offset);

        if (obj.getType() != Tab.noType) {
            Code.put(Code.pop);
        }
    }

    public void visit(DesignatorOpAssign designatorOpAssign) {
        Obj target = designatorOpAssign.getDesignator().obj;
        Code.store(target);
    }

    public void visit(DesignatorOpIncrement designatorOpIncrement) {
        Obj target = designatorOpIncrement.getDesignator().obj;
        Code.put(Code.inc);
        Code.put(target.getAdr());
        Code.put(1);
    }

    public void visit(DesignatorOpDecrement designatorOpDecrement) {
        Obj target = designatorOpDecrement.getDesignator().obj;
        Code.put(Code.inc);
        Code.put(target.getAdr());
        Code.put(-1);
    }

    public void visit(DesignatorIndOpDot designatorIndOpDot) {
        Obj cls = designatorIndOpDot.getDesignator().obj;
        Code.load(cls);
    }
    public void visit(DesignatorArr designatorArr) {
        Obj arr = designatorArr.getDesignator().obj;
        Code.load(arr);
    }

}
