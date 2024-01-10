package semantics.visitors;

import ast.*;
import rs.etf.pp1.symboltable.concepts.Obj;
import rs.etf.pp1.symboltable.concepts.Struct;
import semantics.adaptors.TabExtended;
import semantics.util.LogUtils;
import semantics.util.ObjList;

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
        Obj obj = TabExtended.insert(Obj.Con, qualifiedName, TabExtended.intType, constNum);
        obj.setAdr(constNum.getNumValue());

        TabExtended.currentScope().addToLocals(obj);
        constNum.obj = obj;
    }

    public void visit(ConstChar constChar) {
        String qualifiedName = semanticPass.getQualifiedNameDeclaration(constChar.getVarName());

        // Set address of const object
        Obj obj = TabExtended.insert(Obj.Con, qualifiedName, TabExtended.charType, constChar);
        obj.setAdr(constChar.getCharValue());

        TabExtended.currentScope().addToLocals(obj);
        constChar.obj = obj;
    }

    public void visit(ConstBool constBool) {
        String qualifiedName = semanticPass.getQualifiedNameDeclaration(constBool.getVarName());

        // Set address of const object
        Obj obj = TabExtended.insert(Obj.Con, qualifiedName, TabExtended.boolType, constBool);
        obj.setAdr(constBool.getBoolValue() ? 1 : 0);

        TabExtended.currentScope().addToLocals(obj);
        constBool.obj = obj;
    }
}
