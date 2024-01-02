package semantics.visitors;

import ast.*;
import rs.etf.pp1.symboltable.concepts.Obj;
import rs.etf.pp1.symboltable.concepts.Struct;
import rs.etf.pp1.symboltable.structure.SymbolDataStructure;
import semantics.decorators.TabExtended;
import semantics.util.LogUtils;
import semantics.util.StructList;
import semantics.util.VisitorUtils;

public class MethodVisitor extends VisitorAdaptor {
    private final SemanticPass semanticPass;

    MethodVisitor(SemanticPass semanticPass) {
        this.semanticPass = semanticPass;
    }

    private void declareFormParam(Obj formPar, Struct formParType, SyntaxNode formParsList) {
        // Declare formal parameter
        VisitorUtils.declareVariable(formPar, formParType, formParsList);

        // add type to function name
        semanticPass.currentMethodName += "$" + formParType.toString();

        // Also increase formal parameter count
        semanticPass.currentMethod.setFpPos(semanticPass.currentMethod.getFpPos() + 1);
    }

    public void visit(MethodDecl methodDecl) {
        // Closing method definition
        semanticPass.currentMethodName += "$"; // add terminating sign to differentiate from variable
        Obj currMeth = semanticPass.currentMethod;

        boolean returnFound = semanticPass.returnFound;
        Struct returnType = currMeth.getType();

        if (!returnFound && returnType != TabExtended.noType) {
            LogUtils.logError("Function "
                            + currMeth.getName()
                            + " has no return expression",
                    methodDecl);
        } else if (returnFound && returnType == TabExtended.noType) {
            LogUtils.logError("Void function "
                            + currMeth.getName()
                            + " has return expression",
                    methodDecl);
        }
        SymbolDataStructure methodLocals = TabExtended.currentScope.getLocals();
        TabExtended.closeScope();

        // insert true method decl
        Obj realMethod = TabExtended.insert(Obj.Meth, semanticPass.currentMethodName, currMeth.getType(), methodDecl);
        realMethod.setLocals(methodLocals);
        realMethod.setFpPos(currMeth.getFpPos());

        // set for later use
        semanticPass.currentMethodTypeName.obj = realMethod;

        // reset parameters
        semanticPass.returnFound = false;
        semanticPass.currentMethod = null;
        semanticPass.currentMethodTypeName = null;
    }

    public void visit(MethodTypeName methodTypeName) {
        // Opening method declaration
        String qualifiedName = semanticPass.getQualifiedNameDeclaration(methodTypeName.getMethodName());

        // delay insertion in symbol table until formal parameters are scanned and added to name
        semanticPass.currentMethod = new Obj(Obj.Meth, qualifiedName, methodTypeName.getMethodType().struct);
        semanticPass.currentMethodName = qualifiedName;
        semanticPass.currentMethodTypeName = methodTypeName;

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
        Obj formPar = formParsList.getVarName().obj;
        Struct formParType = formParsList.getType().struct;

        declareFormParam(formPar, formParType, formParsList);
    }

    public void visit(FormParsListMultiple formParsList) {
        // Declare formal parameter
        Obj formPar = formParsList.getVarName().obj;
        Struct formParType = formParsList.getType().struct;

        declareFormParam(formPar, formParType, formParsList);
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
