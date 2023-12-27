package semantics.visitors;

import ast.*;
import rs.etf.pp1.symboltable.concepts.Obj;
import rs.etf.pp1.symboltable.concepts.Struct;
import semantics.decorators.StructExtended;
import semantics.decorators.TabExtended;
import semantics.util.LogUtils;
import semantics.util.ObjList;

import java.util.Collection;
import java.util.HashSet;

public class ClassVisitor extends VisitorAdaptor {
    private final SemanticPass semanticPass;

    ClassVisitor(SemanticPass semanticPass) {
        this.semanticPass = semanticPass;
    }

    public void visit(ClassDecl classDecl) {
        // chain locals here and close scope
        TabExtended.chainLocalSymbols(semanticPass.currentClass.getType()); // this does everything
        TabExtended.closeScope();

        semanticPass.currentClass = null;
    }

    public void visit(ClassName className) {
        String qualifiedName = semanticPass.getQualifiedNameDeclaration(className.getClassName());

        // create a new user-defined type
        Struct classStruct = new StructExtended(Struct.Class);

        Obj classNode = TabExtended.insert(Obj.Type, qualifiedName, classStruct);
        if (classNode == TabExtended.noObj) {
            LogUtils.logError("Class with name " + qualifiedName + " already declared", className);
        }

        // Prepare for static variable declaration
        semanticPass.currentClass = classNode;
        TabExtended.openScope();
    }

    public void visit(ExtendsClauseExists extendsClauseExists) {
        Struct classType = extendsClauseExists.getType().struct;

        if (classType.getKind() != Struct.Class) {
            LogUtils.logError("Extends clause only allowed with class type", extendsClauseExists);
        } else {
            this.semanticPass.currentClass.getType().setElementType(classType);
            Collection<Obj> members = classType.getMembers();
            for (Obj obj : members) {
                Obj cloned = TabExtended.insert(obj.getKind(), obj.getName(), obj.getType());
                cloned.setLevel(obj.getLevel()); // ensure this is ok for methods

                TabExtended.openScope();
                // copy locals
                for (Obj local : obj.getLocalSymbols()) {
                    TabExtended.insert(local.getKind(), local.getName(), local.getType());
                }
                TabExtended.chainLocalSymbols(cloned);
                TabExtended.closeScope();
            }
        }
    }

    public void visit(StaticVarDeclListExists staticVarDeclList) {
        ObjList objList = staticVarDeclList.getVarDecl().objlist;
        String currentClass = semanticPass.currentClass.getName();

        semanticPass.staticClassFields.putIfAbsent(currentClass, new HashSet<>());
        HashSet<String> staticClasses = semanticPass.staticClassFields.get(currentClass);

        objList.forEach(obj -> staticClasses.add(obj.getName()));
    }

    public void visit(StaticVarDeclListEmpty staticVarDeclList) {
        String currentClass = semanticPass.currentClass.getName();

        semanticPass.staticClassFields.putIfAbsent(currentClass, new HashSet<>());
    }
}
