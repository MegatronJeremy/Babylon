package semantics.decorators;

import ast.SyntaxNode;
import rs.etf.pp1.symboltable.Tab;
import rs.etf.pp1.symboltable.concepts.Obj;
import rs.etf.pp1.symboltable.concepts.Scope;
import rs.etf.pp1.symboltable.concepts.Struct;
import semantics.util.VisitorUtils;

public class TabExtended extends Tab {
    public static final Struct intType = new StructExtended(Struct.Int, "int"),
            charType = new StructExtended(Struct.Char, "char"),
            boolType = new StructExtended(Struct.Bool, "bool");
    private static Obj helperA;
    private static Obj helperB;
    private static Obj helperC;

    public static void init() {
        // HACK - set currentLevel to -1
        Tab.currentScope = new Scope(null);
        Tab.closeScope();

        Scope universe = currentScope = new Scope(null);

        universe.addToLocals(new Obj(Obj.Type, "int", intType));
        universe.addToLocals(new Obj(Obj.Type, "char", charType));
        universe.addToLocals(new Obj(Obj.Type, "bool", boolType));
        universe.addToLocals(new Obj(Obj.Con, "eol", charType, 10, 0));
        universe.addToLocals(new Obj(Obj.Con, "null", nullType, 0, 0));

        {
            universe.addToLocals(chrObj = new Obj(Obj.Meth, "chr$int$", charType, 0, 0));
            openScope();
            currentScope.addToLocals(new Obj(Obj.Var, "i", intType, 0, 1));
            chrObj.setLocals(currentScope.getLocals());
            chrObj.setFpPos(1);
            closeScope();
        }

        {
            universe.addToLocals(ordObj = new Obj(Obj.Meth, "ord$char$", intType, 0, 0));
            openScope();
            currentScope.addToLocals(new Obj(Obj.Var, "ch", charType, 0, 1));
            ordObj.setLocals(currentScope.getLocals());
            ordObj.setFpPos(1); // one param
            closeScope();
        }

        {
            universe.addToLocals(lenObj = new Obj(Obj.Meth, "len$arr$", intType, 0, 0));
            openScope();
            currentScope.addToLocals(new Obj(Obj.Var, "arr", new StructExtended(Struct.Array, noType, "arr"), 0, 1));
            lenObj.setLocals(currentScope.getLocals());
            lenObj.setFpPos(1);
            closeScope();
        }
    }

    public static void dump() {
        dump(new DumpSymbolTableVisitorExtended());
    }

    public static Obj insert(int kind, String name, Struct type, SyntaxNode syntaxNode) {
        VisitorUtils.checkAlreadyDeclared(name, syntaxNode);
        return Tab.insert(kind, name, type);
    }

    public static Obj getHelperA() {
        return helperA;
    }

    public static Obj getHelperB() {
        return helperB;
    }

    public static Obj getHelperC() {
        return helperC;
    }

    public static void generateHelpers() {
        // open global scope and add two static variables to be used as helpers
        helperA = TabExtended.insert(Obj.Var, "@", TabExtended.intType);
        helperB = TabExtended.insert(Obj.Var, "#", TabExtended.intType);
        helperC = TabExtended.insert(Obj.Var, "^", TabExtended.intType);
    }
}
