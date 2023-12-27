package codegen.visitors;

import ast.DesignatorOpAssign;
import ast.DesignatorOpCall;
import ast.VisitorAdaptor;
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

}
