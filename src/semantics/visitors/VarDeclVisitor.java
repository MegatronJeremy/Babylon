package semantics.visitors;

import ast.*;
import rs.etf.pp1.symboltable.concepts.Obj;
import rs.etf.pp1.symboltable.concepts.Struct;
import semantics.adaptors.StructExtended;
import semantics.adaptors.TabExtended;
import semantics.util.ObjList;
import semantics.util.VisitorUtils;

public class VarDeclVisitor extends VisitorAdaptor {
    private final SemanticPass semanticPass;

    VarDeclVisitor(SemanticPass semanticPass) {
        this.semanticPass = semanticPass;
    }

    public void visit(VarScalar varScalar) {
        String qualifiedName = semanticPass.getQualifiedNameDeclaration(varScalar.getVarName());

        // Declare with noType
        int objKind = semanticPass.currentVarDeclObjKind();
        varScalar.obj = new Obj(objKind, qualifiedName, TabExtended.noType);
    }

    public void visit(VarArray varArray) {
        String qualifiedName = semanticPass.getQualifiedNameDeclaration(varArray.getVarName());

        // Set elemType to noType for now
        Struct varType = new StructExtended(Struct.Array, TabExtended.noType, "arr");

        int objKind = semanticPass.currentVarDeclObjKind();
        varArray.obj = new Obj(objKind, qualifiedName, varType);
    }

    public void visit(VarDeclCorrect varDecl) {
        Struct declType = varDecl.getType().struct;

        for (Obj obj : varDecl.getVarList().objlist) {
            VisitorUtils.declareVariable(obj, declType, varDecl);
        }

        varDecl.objlist = varDecl.getVarList().objlist;
    }

    public void visit(VarDeclError varDecl) {
        varDecl.objlist = new ObjList();
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
