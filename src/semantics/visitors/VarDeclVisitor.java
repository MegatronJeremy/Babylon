package semantics.visitors;

import ast.*;
import rs.etf.pp1.symboltable.concepts.Obj;
import rs.etf.pp1.symboltable.concepts.Struct;
import semantics.TabExtended;
import semantics.util.ObjList;
import semantics.util.VisitorUtils;

public class VarDeclVisitor extends VisitorAdaptor {
    private final SemanticPass semanticPass;

    VarDeclVisitor(SemanticPass semanticPass) {
        this.semanticPass = semanticPass;
    }

    public void visit(VarScalar varScalar) {
        String qualifiedName = semanticPass.getQualifiedName(varScalar.getVarName());

        // Declare with noType
        varScalar.obj = new Obj(Obj.Var, qualifiedName, TabExtended.noType);
    }

    public void visit(VarArray varArray) {
        String qualifiedName = semanticPass.getQualifiedName(varArray.getVarName());

        // Set elemType to noType for now
        Struct varType = new Struct(Struct.Array, TabExtended.noType);
        varArray.obj = new Obj(Obj.Var, qualifiedName, varType);
    }

    public void visit(VarDeclCorrect varDecl) {
        Struct declType = varDecl.getType().struct;

        for (Obj obj : varDecl.getVarList().objlist) {
            VisitorUtils.declareVariable(obj, declType, varDecl);
        }
    }

    public void visit(VarListMultiple varListMultiple) {
        varListMultiple.objlist = varListMultiple.getVarList().objlist;
        varListMultiple.objlist.add(varListMultiple.getVarName().obj);
    }

    public void visit(VarListSingle varList) {
        varList.objlist = new ObjList();
        varList.objlist.add(varList.getVarName().obj);
    }

    public void visit(VarListError varList) {
        // alternative start
        varList.objlist = new ObjList();
        varList.objlist.add(varList.getVarName().obj);
    }
}