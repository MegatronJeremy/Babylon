package semantics.visitors;

import ast.*;
import rs.etf.pp1.symboltable.concepts.Obj;
import rs.etf.pp1.symboltable.concepts.Struct;
import semantics.adaptors.TabAdaptor;
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
        constList.objlist = constList.getConstList().objlist;
        constList.objlist.add(constList.getConstType().obj);
    }

    public void visit(ConstListEmpty constList) {
        constList.objlist = new ObjList();
        constList.objlist.add(constList.getConstType().obj);
    }

    public void visit(ConstNum constNum) {
        String qualifiedName = semanticPass.getQualifiedNameDeclaration(constNum.getVarName());

        // Set address of const object
        Obj obj = TabAdaptor.insert(Obj.Con, qualifiedName, TabAdaptor.intType);
        obj.setAdr(constNum.getNumValue());

        TabAdaptor.currentScope().addToLocals(obj);
        constNum.obj = obj;

        VisitorUtils.checkAlreadyDeclared(obj, constNum);
    }

    public void visit(ConstChar constChar) {
        String qualifiedName = semanticPass.getQualifiedNameDeclaration(constChar.getVarName());

        // Set address of const object
        Obj obj = TabAdaptor.insert(Obj.Con, qualifiedName, TabAdaptor.charType);
        obj.setAdr(constChar.getCharValue());

        TabAdaptor.currentScope().addToLocals(obj);
        constChar.obj = obj;

        VisitorUtils.checkAlreadyDeclared(obj, constChar);
    }

    public void visit(ConstBool constBool) {
        String qualifiedName = semanticPass.getQualifiedNameDeclaration(constBool.getVarName());

        // Set address of const object
        Obj obj = TabAdaptor.insert(Obj.Con, qualifiedName, TabAdaptor.boolType);
        obj.setAdr(constBool.getBoolValue() ? 1 : 0);

        TabAdaptor.currentScope().addToLocals(obj);
        constBool.obj = obj;

        VisitorUtils.checkAlreadyDeclared(obj, constBool);
    }
}
