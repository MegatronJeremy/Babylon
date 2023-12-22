package semantics.visitors;

import ast.*;
import rs.etf.pp1.symboltable.concepts.Obj;
import rs.etf.pp1.symboltable.concepts.Struct;
import semantics.TabExtended;
import semantics.util.LogUtils;
import semantics.util.ObjList;
import semantics.util.VisitorUtils;

public class ConstDeclVisitor extends VisitorAdaptor {
    private final SemanticPass semanticPass;

    ConstDeclVisitor(SemanticPass semanticPass) {
        this.semanticPass = semanticPass;
    }

    public void visit(ConstDecl constDecl) {
        for (Obj obj : constDecl.getConstList().objlist) {
            Struct lType = constDecl.getType().struct;
            Struct rType = obj.getType();
            if (!rType.equals(lType)) {
                LogUtils.logError("Incompatible assignment expression for variable " + obj.getName(), constDecl);
            }
        }
    }

    public void visit(ConstListExists constList) {
        constList.objlist.add(constList.getConstType().obj);
    }

    public void visit(ConstListEmpty constList) {
        constList.objlist = new ObjList();
        constList.objlist.add(constList.getConstType().obj);
    }

    public void visit(ConstNum constNum) {
        String qualifiedName = semanticPass.getQualifiedName(constNum.getVarName());

        // Set address of const object
        Obj obj = TabExtended.insert(Obj.Con, qualifiedName, TabExtended.intType);
        obj.setAdr(constNum.getNumValue());

        TabExtended.currentScope().addToLocals(obj);
        constNum.obj = obj;

        VisitorUtils.checkAlreadyDeclared(obj, constNum);
    }

    public void visit(ConstChar constChar) {
        String qualifiedName = semanticPass.getQualifiedName(constChar.getVarName());

        // Set address of const object
        Obj obj = TabExtended.insert(Obj.Con, qualifiedName, TabExtended.charType);
        obj.setAdr(constChar.getCharValue());

        TabExtended.currentScope().addToLocals(obj);
        constChar.obj = obj;

        VisitorUtils.checkAlreadyDeclared(obj, constChar);
    }

    public void visit(ConstBool constBool) {
        String qualifiedName = semanticPass.getQualifiedName(constBool.getVarName());

        // Set address of const object
        Obj obj = TabExtended.insert(Obj.Con, qualifiedName, TabExtended.boolType);
        obj.setAdr(constBool.getBoolValue() ? 1 : 0);

        TabExtended.currentScope().addToLocals(obj);
        constBool.obj = obj;

        VisitorUtils.checkAlreadyDeclared(obj, constBool);
    }
}
