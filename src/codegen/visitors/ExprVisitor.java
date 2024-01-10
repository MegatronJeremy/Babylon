package codegen.visitors;

import ast.*;
import rs.etf.pp1.mj.runtime.Code;
import rs.etf.pp1.symboltable.concepts.Obj;
import rs.etf.pp1.symboltable.concepts.Struct;

public class ExprVisitor extends VisitorAdaptor {
    public void visit(FactorDesignator factorDesignator) {
        Obj target = factorDesignator.getDesignator().obj;
        Code.load(target);
    }

    public void visit(ExprAddop exprAddop) {
        Code.put(exprAddop.getAddop().integer);
    }

    public void visit(ExprNeg exprNeg) {
        Code.put(Code.neg);
    }

    public void visit(TermMulop termMulop) {
        Code.put(termMulop.getMulop().integer);
    }

    public void visit(FactorNumConst factorNumConst) {
        Code.loadConst(factorNumConst.getValue());
    }

    public void visit(FactorCharConst factorCharConst) {
        Code.loadConst(factorCharConst.getValue());
    }

    public void visit(FactorBoolConst factorBoolConst) {
        Code.loadConst(factorBoolConst.getValue() ? 1 : 0);
    }

    public void visit(FactorNewArray factorNewArray) {
        Struct type = factorNewArray.getType().struct;
        Code.put(Code.newarray);
        if (type.getKind() == Struct.Char) {
            Code.put(0);
        } else {
            Code.put(1);
        }
    }

    public void visit(FactorNewClass factorNewClass) {
        Struct type = factorNewClass.getType().struct;
        Code.put(Code.new_);
        Code.put2(type.getNumberOfFields() * 4); // all fields are 4 bytes (one field - one word)

        // store vftp of class
        Integer vftpAddr = CodeGenerator.getInstance().vftpMap.get(type);

        Code.put(Code.dup); // duplicate address

        // put val on stack
        if (vftpAddr == null) {
            // save fixup for later
            Code.put(Code.const_);
            Code.put4(0);
            CodeGenerator.getInstance().vftpFixupQueue.add(Code.pc - 4);
        } else {
            Code.loadConst(vftpAddr);
        }

        Code.put(Code.putfield); // store vftp
        Code.put2(0);
    }
}
