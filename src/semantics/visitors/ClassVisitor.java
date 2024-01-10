package semantics.visitors;

import ast.*;
import rs.etf.pp1.symboltable.concepts.Obj;
import rs.etf.pp1.symboltable.concepts.Struct;
import semantics.adaptors.StructExtended;
import semantics.adaptors.TabExtended;
import semantics.util.LogUtils;

import java.util.Collection;

public class ClassVisitor extends VisitorAdaptor {
    private final SemanticPass semanticPass;

    ClassVisitor(SemanticPass semanticPass) {
        this.semanticPass = semanticPass;
    }

    public void visit(ClassDecl classDecl) {
        // chain locals here and close scope
        TabExtended.chainLocalSymbols(semanticPass.currentClass.getType()); // this does everything
        TabExtended.closeScope();

        // reset everything
        semanticPass.currentClass = null;
        semanticPass.currentClassCoreName = null;
        semanticPass.currentMethodsToOverload.clear();
    }

    public void visit(ClassName className) {
        String qualifiedName = semanticPass.getQualifiedNameDeclaration(className.getClassName());

        // create a new user-defined type
        Struct classStruct = new StructExtended(Struct.Class, qualifiedName);

        if (TabExtended.find(qualifiedName) != TabExtended.noObj) {
            LogUtils.logError("Class with name " + qualifiedName + " already declared", className);
        }
        Obj classNode = TabExtended.insert(Obj.Type, qualifiedName, classStruct);

        // Prepare for static variable declaration
        semanticPass.currentClass = classNode;

        // DO NOT OPEN NEW SCOPE YET - ONLY AT THE END OF THE STATIC BLOCK
        semanticPass.inStaticDef = true;

        // save this for later
        className.obj = classNode;
    }

    public void visit(ExtendsClauseExists extendsClauseExists) {
        // supertype exists
        Struct superClassType = extendsClauseExists.getType().struct;
        Struct currentClassType = semanticPass.currentClass.getType();
        String currentClassName = semanticPass.currentClass.getName();

        // set supertype and name for toString
        assert currentClassType.getClass() == StructExtended.class;
        ((StructExtended) currentClassType).setName(currentClassName);

        // Set supertype
        if (superClassType.getKind() != Struct.Class) {
            LogUtils.logError("Extends clause only allowed with class type", extendsClauseExists);
            currentClassType.setElementType(TabExtended.noType);
        } else {
            currentClassType.setElementType(superClassType);
        }
    }

    public void visit(ExtendsClauseEmpty extendsClauseEmpty) {
        // Set supertype
        Struct currentClassType = semanticPass.currentClass.getType();
        currentClassType.setElementType(TabExtended.noType);
    }

    public void visit(StaticVarDeclListExists staticVarDeclList) {
    }

    public void visit(StaticVarDeclListEmpty staticVarDeclList) {
    }

    public void visit(StaticInitializerStart staticInitializerStart) {
        // ensure you are not in static def mode
        semanticPass.inStaticDef = false;
    }

    public void visit(StaticInitListEmpty staticInitListEmpty) {
        // ensure this is false at this point as well in case it was skipped over
        semanticPass.inStaticDef = false;

        // end of static initialization - open class scope here
        TabExtended.openScope();

        // add virtual table function pointer
        TabExtended.insert(Obj.Fld, "@vftp", TabExtended.intType);

        Struct superClassType = semanticPass.currentClass.getType().getElemType();

        // do everything regarding extends clause here
        Collection<Obj> members = superClassType.getMembers();
        for (Obj obj : members) {
            if (obj.getKind() == Obj.Meth) {
                semanticPass.currentMethodsToOverload.put(obj.getName(), obj.getType());
            }
            TabExtended.insertChangeable(obj);
        }
    }
}
