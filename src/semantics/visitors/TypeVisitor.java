package semantics.visitors;

import ast.Type;
import ast.TypeNamespace;
import ast.TypeNoNamespace;
import ast.VisitorAdaptor;
import rs.etf.pp1.symboltable.concepts.Obj;
import semantics.TabExtended;
import semantics.util.LogUtils;

public class TypeVisitor extends VisitorAdaptor {
    private final SemanticPass semanticPass;

    TypeVisitor(SemanticPass semanticPass) {
        this.semanticPass = semanticPass;
    }

    private void assignWithTypeCoherency(String typeName, Obj typeNode, Type type) {
        if (Obj.Type == typeNode.getKind()) {
            type.struct = typeNode.getType();
        } else {
            LogUtils.logError("Error on line " +
                    type.getLine() + ": type " + typeName + " is not a type.");
            type.struct = TabExtended.noType;
        }
    }

    private void assignType(String typeName, Type type) {
        Obj typeNode = TabExtended.find(typeName);

        if (typeNode == TabExtended.noObj) {
            LogUtils.logError("Error on line " +
                    type.getLine() + ": type " + typeName + " not declared.");
            type.struct = TabExtended.noType;
        } else {
            assignWithTypeCoherency(typeName, typeNode, type);
        }
    }

    public void visit(TypeNoNamespace typeNoNamespace) {
        String qualifiedName = semanticPass.getQualifiedName(typeNoNamespace.getTypeName());
        Obj typeNode = TabExtended.find(qualifiedName);

        if (typeNode == TabExtended.noObj) {
            // Not found, look for global (non-qualified) name
            assignType(typeNoNamespace.getTypeName(), typeNoNamespace);
        } else {
            // Check for type coherency with qualified name
            assignWithTypeCoherency(qualifiedName, typeNode, typeNoNamespace);
        }
    }

    public void visit(TypeNamespace typeNamespace) {
        String qualifiedName = typeNamespace.getTypeNamespace() + "::" + typeNamespace.getTypeName();
        assignType(qualifiedName, typeNamespace);
    }
}
