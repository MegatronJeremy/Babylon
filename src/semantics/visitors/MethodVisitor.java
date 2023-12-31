package semantics.visitors;

import ast.*;
import rs.etf.pp1.symboltable.concepts.Obj;
import rs.etf.pp1.symboltable.concepts.Struct;
import semantics.decorators.TabExtended;
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
            LogUtils.logError("Function "
                            + semanticPass.currentMethod.getName()
                            + " has no return expression",
                    methodDecl);
        } else if (returnFound && returnType == TabExtended.noType) {
            LogUtils.logError("Void function "
                            + semanticPass.currentMethod.getName()
                            + " has return expression",
                    methodDecl);
        }
        TabExtended.chainLocalSymbols(semanticPass.currentMethod);
        TabExtended.closeScope();

        // reset parameters
        semanticPass.returnFound = false;
        semanticPass.currentMethod = null;
    }

    public void visit(MethodTypeName methodTypeName) {
        // Opening method declaration
        String qualifiedName = semanticPass.getQualifiedNameDeclaration(methodTypeName.getMethodName());
        semanticPass.currentMethod = TabExtended.insert(Obj.Meth, qualifiedName, methodTypeName.getMethodType().struct);
        TabExtended.openScope();

        if (semanticPass.currentClass != null) {
            TabExtended.insert(Obj.Var, "this", semanticPass.currentClass.getType());
            semanticPass.currentMethod.setFpPos(1); // implicit formal parameter
        } else {
            semanticPass.currentMethod.setFpPos(0); // 0 formal parameters at first
        }

        methodTypeName.obj = semanticPass.currentMethod;

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
        semanticPass.currentMethod.setFpPos(semanticPass.currentMethod.getFpPos() + 1);
    }

    public void visit(FormParsListMultiple formParsList) {
        // Declare formal parameter
        VisitorUtils.declareVariable(formParsList.getVarName().obj, formParsList.getType().struct, formParsList);

        // Also increase formal parameter count
        semanticPass.currentMethod.setFpPos(semanticPass.currentMethod.getFpPos() + 1);
    }

    public void visit(FormParsListError formParsList) {
        // Declare formal parameter
        VisitorUtils.declareVariable(formParsList.getVarName().obj, formParsList.getType().struct, formParsList);

        // Also increase formal parameter count
        semanticPass.currentMethod.setFpPos(semanticPass.currentMethod.getFpPos() + 1);
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
