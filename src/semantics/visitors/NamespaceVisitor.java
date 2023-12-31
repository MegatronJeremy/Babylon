package semantics.visitors;

import ast.NamespaceDecl;
import ast.NamespaceName;
import ast.VisitorAdaptor;

public class NamespaceVisitor extends VisitorAdaptor {
    private final SemanticPass semanticPass;

    NamespaceVisitor(SemanticPass semanticPass) {
        this.semanticPass = semanticPass;
    }

    public void visit(NamespaceName namespaceName) {
        this.semanticPass.currentNamespace = namespaceName.getNamespaceName();
    }

    public void visit(NamespaceDecl namespaceDecl) {
        this.semanticPass.currentNamespace = "";
    }
}
