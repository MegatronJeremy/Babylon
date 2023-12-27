package codegen.visitors;

import ast.ConstDecl;
import ast.VisitorAdaptor;
import rs.etf.pp1.mj.runtime.Code;
import rs.etf.pp1.symboltable.concepts.Obj;

public class ConstDeclVisitor extends VisitorAdaptor {

    public void visit(ConstDecl constDecl) {
        for (Obj obj : constDecl.getConstList().objlist) {
            Code.load(obj);
        }
    }
}
