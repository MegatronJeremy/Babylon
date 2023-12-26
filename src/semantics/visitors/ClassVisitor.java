package semantics.visitors;

import ast.*;
import rs.etf.pp1.symboltable.concepts.Obj;
import rs.etf.pp1.symboltable.concepts.Struct;
import semantics.adaptors.StructAdaptor;
import semantics.adaptors.TabAdaptor;
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
        TabAdaptor.chainLocalSymbols(semanticPass.currentClass.getType()); // this does everything
        TabAdaptor.closeScope();

        semanticPass.currentClass = null;
    }

    public void visit(ClassName className) {
        String qualifiedName = semanticPass.getQualifiedNameDeclaration(className.getClassName());

        // create a new user-defined type
        Struct classStruct = new StructAdaptor(Struct.Class);

        Obj classNode = TabAdaptor.insert(Obj.Type, qualifiedName, classStruct);
        if (classNode == TabAdaptor.noObj) {
            LogUtils.logError("Class with name " + qualifiedName + " already declared", className);
        }

        // Prepare for static variable declaration
        semanticPass.currentClass = classNode;
        TabAdaptor.openScope();
    }

    public void visit(ExtendsClauseExists extendsClauseExists) {
        Struct classType = extendsClauseExists.getType().struct;

        if (classType.getKind() != Struct.Class) {
            LogUtils.logError("Extends clause only allowed with class type", extendsClauseExists);
        } else {
            this.semanticPass.currentClass.getType().setElementType(classType);
            Collection<Obj> members = classType.getMembers();
            for (Obj obj : members) {
                Obj cloned = TabAdaptor.insert(obj.getKind(), obj.getName(), obj.getType());
                cloned.setLevel(obj.getLevel()); // ensure this is ok for methods

                TabAdaptor.openScope();
                // copy locals
                for (Obj local : obj.getLocalSymbols()) {
                    TabAdaptor.insert(local.getKind(), local.getName(), local.getType());
                }
                TabAdaptor.chainLocalSymbols(cloned);
                TabAdaptor.closeScope();
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