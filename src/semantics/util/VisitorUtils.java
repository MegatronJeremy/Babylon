package semantics.util;

import ast.SyntaxNode;
import rs.etf.pp1.symboltable.concepts.Obj;
import rs.etf.pp1.symboltable.concepts.Struct;
import semantics.adaptors.TabExtended;
import semantics.visitors.SemanticPass;

public class VisitorUtils {

    public static void declareVariable(Obj obj, Struct declType, SyntaxNode syntaxNode) {
        Struct currentType = obj.getType();

        if (currentType.isRefType()) {
            currentType.setElementType(declType);
        } else {
            currentType = declType;
        }

        Obj objNode = TabExtended.insert(obj.getKind(), obj.getName(), currentType, syntaxNode);
        TabExtended.currentScope().addToLocals(objNode);
    }

    public static boolean checkAlreadyDeclared(String name, Struct type, SyntaxNode syntaxNode) {
        if (TabExtended.currentScope.findSymbol(name) != null && !SemanticPass.getInstance().canDeclareMethod(name, type)) {
            LogUtils.logError("Symbol with name "
                    + name + " already declared", syntaxNode);

            return true;
        } else {
            LogUtils.logInfo("Declared symbol " + name, syntaxNode);

            return false;
        }
    }

    public static boolean checkAssignability(Struct lType, Struct rType, SyntaxNode syntaxNode) {
        if (!rType.assignableTo(lType)) {
            LogUtils.logError("Type " + LogUtils.structKindToString(rType.getKind())
                            + " not assignable to type " + LogUtils.structKindToString(lType.getKind()),
                    syntaxNode);

            return false;
        }

        return true;
    }
}
