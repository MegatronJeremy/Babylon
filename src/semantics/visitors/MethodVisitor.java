package semantics.visitors;

import ast.*;
import rs.etf.pp1.symboltable.concepts.Obj;
import rs.etf.pp1.symboltable.concepts.Struct;
import semantics.TabExtended;
import semantics.util.LogUtils;
import semantics.util.StructList;
import semantics.util.VisitorUtils;

public class MethodVisitor extends VisitorAdaptor {
    private final SemanticPass semanticPass;

    MethodVisitor(SemanticPass semanticPass) {
        this.semanticPass = semanticPass;
    }

    public void visit(MethodDecl methodDecl) {
        // Closing method definition
        boolean returnFound = semanticPass.returnFound;
        Struct returnType = semanticPass.currentMethod.getType();

        if (!returnFound && returnType != TabExtended.noType) {
            LogUtils.logError("Error on line " + methodDecl.getLine() + ": function "
                    + semanticPass.currentMethod.getName() + " has no return expression");
        } else if (returnFound && returnType == TabExtended.noType) {
            LogUtils.logError("Error on line " + methodDecl.getLine() + ": void function "
                    + semanticPass.currentMethod.getName() + " has return expression");
        }
        TabExtended.chainLocalSymbols(semanticPass.currentMethod);
        TabExtended.closeScope();

        semanticPass.returnFound = false;
        semanticPass.currentMethod = null;
    }

    public void visit(MethodTypeName methodTypeName) {
        // Opening method declaration
        String qualifiedName = semanticPass.getQualifiedName(methodTypeName.getMethodName());
        semanticPass.currentMethod = TabExtended.insert(Obj.Meth, qualifiedName, methodTypeName.getMethodType().struct);
        semanticPass.currentMethod.setLevel(0); // 0 formal parameters at first
        TabExtended.openScope();

        LogUtils.logInfo("Processing function " + qualifiedName, methodTypeName);
    }

    public void visit(MethodTypeExists methodTypeExists) {
        methodTypeExists.struct = methodTypeExists.getType().struct;
    }

    public void visit(MethodTypeVoid methodTypeVoid) {
        methodTypeVoid.struct = TabExtended.noType;
    }

    public void visit(FormParsListSingle formParsList) {
        // Declare formal parameter
        VisitorUtils.declareVariable(formParsList.getVarName().obj, formParsList.getType().struct, formParsList);

        // Also increase formal parameter count
        semanticPass.currentMethod.setLevel(semanticPass.currentMethod.getLevel() + 1);
    }

    public void visit(FormParsListMultiple formParsList) {
        // Declare formal parameter
        VisitorUtils.declareVariable(formParsList.getVarName().obj, formParsList.getType().struct, formParsList);

        // Also increase formal parameter count
        semanticPass.currentMethod.setLevel(semanticPass.currentMethod.getLevel() + 1);
    }

    public void visit(FormParsListError formParsList) {
        // Declare formal parameter
        VisitorUtils.declareVariable(formParsList.getVarName().obj, formParsList.getType().struct, formParsList);

        // Also increase formal parameter count
        semanticPass.currentMethod.setLevel(semanticPass.currentMethod.getLevel() + 1);
    }

    public void visit(ActParsExists actParsExists) {
        actParsExists.structlist = actParsExists.getActPars().structlist;
    }

    public void visit(ActParsEmpty actParsEmpty) {
        actParsEmpty.structlist = new StructList();
    }

    public void visit(ActParsMultiple actParsMultiple) {
        actParsMultiple.structlist = actParsMultiple.getActPars().structlist;
        actParsMultiple.structlist.add(actParsMultiple.getExpr().struct);
    }

    public void visit(ActParsSingle actParsSingle) {
        actParsSingle.structlist = new StructList();
        actParsSingle.structlist.add(actParsSingle.getExpr().struct);
    }
}
