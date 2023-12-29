package codegen.visitors;

import ast.*;
import rs.etf.pp1.mj.runtime.Code;
import rs.etf.pp1.symboltable.concepts.Obj;

public class DesignatorVisitor extends VisitorAdaptor {

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
                int offset = obj.getAdr() - Code.pc; // from the current instruction
                Code.put(Code.call);
                Code.put2(offset);
                // TODO see what to do with this if it isn't used
//                if (obj.getType() != Tab.noType) {
//                    Code.put(Code.pop);
//                }
        }
    }

    public void visit(DesignatorOpAssign designatorOpAssign) {
        Obj target = designatorOpAssign.getDesignator().obj;
        Code.store(target);
    }

    public void visit(DesignatorOpIncrement designatorOpIncrement) {
        Obj target = designatorOpIncrement.getDesignator().obj;
        if (target.getLevel() != 0) {
            // local - use inc
            Code.put(Code.inc);
            Code.put(target.getAdr());
            Code.put(1);
        } else {
            // global
            Code.load(target);
            Code.loadConst(1);
            Code.put(Code.add);
            Code.store(target);
        }
    }

    public void visit(DesignatorOpDecrement designatorOpDecrement) {
        Obj target = designatorOpDecrement.getDesignator().obj;
        if (target.getLevel() != 0) {
            // local - use inc
            Code.put(Code.inc);
            Code.put(target.getAdr());
            Code.put(-1);
        } else {
            // global
            Code.load(target);
            Code.loadConst(-1);
            Code.put(Code.add);
            Code.store(target);
        }
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
