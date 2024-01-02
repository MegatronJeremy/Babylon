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

        // reset everything
        semanticPass.currentClass = null;
        semanticPass.currentClassCoreName = null;
        semanticPass.currentClassSupertype = null;
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
        semanticPass.currentClassSupertype = extendsClauseExists.getType().struct;

        // supertype exists
        Struct currentClassType = semanticPass.currentClass.getType();
        Struct superClassType = semanticPass.currentClassSupertype;

        String coreClassName = semanticPass.classCoreNames.get(superClassType);
        assert coreClassName != null;

        semanticPass.classCoreNames.put(currentClassType, coreClassName);
        semanticPass.currentClassCoreName = coreClassName;

        // set name for toString to core class name
        ((StructExtended) currentClassType).setName(coreClassName);
    }

    public void visit(ExtendsClauseEmpty extendsClauseEmpty) {
        Struct currentClassType = semanticPass.currentClass.getType();
        String currentClassName = semanticPass.currentClass.getName();
        semanticPass.classCoreNames.put(currentClassType, currentClassName);
        // core class name for base class is the name itself

        semanticPass.currentClassCoreName = currentClassName;
    }

    public void visit(StaticVarDeclListExists staticVarDeclList) {
        ObjList objList = staticVarDeclList.getVarDecl().objlist;
        String currentClass = semanticPass.currentClassCoreName;

        semanticPass.staticClassFields.putIfAbsent(currentClass, new HashSet<>());
        HashSet<String> staticClasses = semanticPass.staticClassFields.get(currentClass);

        objList.forEach(obj -> staticClasses.add(obj.getName()));
    }

    public void visit(StaticVarDeclListEmpty staticVarDeclList) {
        String currentClass = semanticPass.currentClassCoreName;

        semanticPass.staticClassFields.putIfAbsent(currentClass, new HashSet<>());
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

        Struct currentClassType = this.semanticPass.currentClass.getType();
        Struct superClassType = semanticPass.currentClassSupertype;
        if (superClassType == null) {
            currentClassType.setElementType(TabExtended.noType); // root class
            return;
        }

        // do everything regarding extends clause here
        if (superClassType.getKind() != Struct.Class) {
            LogUtils.logError("Extends clause only allowed with class type", staticInitListEmpty);
        } else {
            currentClassType.setElementType(superClassType); // set type from inherited class

            Collection<Obj> members = superClassType.getMembers();
            for (Obj obj : members) {
                if (obj.getKind() == Obj.Meth) {
                    semanticPass.currentMethodsToOverload.add(obj.getName());
                }

                Obj cloned = TabExtended.insert(obj.getKind(), obj.getName(), obj.getType());
                cloned.setFpPos(obj.getFpPos()); // ensure this is ok for methods

                TabExtended.openScope();
                // copy locals for methods
                for (Obj local : obj.getLocalSymbols()) {
                    TabExtended.insert(local.getKind(), local.getName(), local.getType());
                }
                TabExtended.chainLocalSymbols(cloned);
                TabExtended.closeScope();
            }
        }
    }
}
