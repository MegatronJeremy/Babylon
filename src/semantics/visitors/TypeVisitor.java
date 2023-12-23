package semantics.visitors;

import ast.Type;
import ast.TypeNamespace;
import ast.TypeNoNamespace;
import ast.VisitorAdaptor;
import rs.etf.pp1.symboltable.concepts.Obj;
import semantics.adaptors.TabAdaptor;
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
            LogUtils.logError("Type " + typeName + " is not a type.", type);
            type.struct = TabAdaptor.noType;
        }
    }

    private void assignType(String typeName, Type type) {
        Obj typeNode = TabAdaptor.find(typeName);

        if (typeNode == TabAdaptor.noObj) {
            LogUtils.logError("Type " + typeName + " not declared.", type);
            type.struct = TabAdaptor.noType;
        } else {
            assignWithTypeCoherency(typeName, typeNode, type);
        }
    }

    public void visit(TypeNoNamespace typeNoNamespace) {
        String qualifiedName = semanticPass.getQualifiedNameLookup(typeNoNamespace.getTypeName());
        Obj typeNode = TabAdaptor.find(qualifiedName);

        if (typeNode == TabAdaptor.noObj) {
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
