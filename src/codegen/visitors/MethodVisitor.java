package codegen.visitors;

import ast.MethodDecl;
import ast.MethodTypeName;
import ast.VisitorAdaptor;
import rs.etf.pp1.mj.runtime.Code;
import rs.etf.pp1.symboltable.concepts.Obj;

import java.util.Objects;

public class MethodVisitor extends VisitorAdaptor {

    public void visit(MethodTypeName methodTypeName) {
        if (Objects.equals(methodTypeName.getMethodName(), "main")) {
            CodeGenerator.getInstance().mainPC = Code.pc;
        }
        Obj obj = methodTypeName.obj;

        obj.setAdr(Code.pc);

        // Generate the entry
        Code.put(Code.enter);

        // Number of formal parameters
        Code.put(obj.getLevel());

        // Number of local variables
        Code.put(obj.getLocalSymbols().size());
    }

    public void visit(MethodDecl methodDecl) {
        Code.put(Code.exit);
        Code.put(Code.return_);
    }

}
