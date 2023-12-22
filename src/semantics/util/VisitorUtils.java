package semantics.util;

import ast.SyntaxNode;
import rs.etf.pp1.symboltable.concepts.Obj;
import rs.etf.pp1.symboltable.concepts.Struct;
import semantics.TabExtended;

public class VisitorUtils {
    public static void declareVariable(Obj obj, Struct declType, SyntaxNode syntaxNode) {
        Struct currentType = obj.getType();

        if (currentType.isRefType()) {
            currentType.setElementType(declType);
        } else {
            currentType = declType;
        }

        Obj objNode = TabExtended.insert(obj.getKind(), obj.getName(), currentType);
        TabExtended.currentScope().addToLocals(objNode);

        checkAlreadyDeclared(obj, syntaxNode);
    }

    public static void checkAlreadyDeclared(Obj obj, SyntaxNode syntaxNode) {
        if (obj == TabExtended.noObj) {
            LogUtils.logError("Variable with name "
                    + obj.getName() + " already declared", syntaxNode);
        } else {
            LogUtils.logInfo("Declared variable " + obj.getName(), syntaxNode);
        }
    }

    public static boolean checkAssignability(Struct lType, Struct rType, SyntaxNode syntaxNode) {
        if (!rType.assignableTo(lType)) {
            LogUtils.logError("Type " + LogUtils.kindToString(rType.getKind())
                            + " not assignable to type " + LogUtils.kindToString(lType.getKind()),
                    syntaxNode);

            return false;
        }

        return true;
    }
}
