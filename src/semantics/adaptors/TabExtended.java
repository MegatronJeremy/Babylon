package semantics.adaptors;

import ast.SyntaxNode;
import rs.etf.pp1.symboltable.Tab;
import rs.etf.pp1.symboltable.concepts.Obj;
import rs.etf.pp1.symboltable.concepts.Scope;
import rs.etf.pp1.symboltable.concepts.Struct;
import semantics.decorators.ObjDecorator;
import semantics.util.VisitorUtils;

public class TabExtended extends Tab {
    public static final Struct intType = new StructExtended(Struct.Int, "int"),
            charType = new StructExtended(Struct.Char, "char"),
            boolType = new StructExtended(Struct.Bool, "bool");
    private static Obj helperA;
    private static Obj helperB;
    private static Obj helperC;

    private static int currentLevel;

    public static void init() {
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

        currentLevel = -1;
    }

    private static void copyLocalSymbols(Obj src, Obj dst) {
        dst.setFpPos(src.getFpPos()); // ensure this is ok for methods

        TabExtended.openScope();
        // copy locals for methods
        for (Obj local : src.getLocalSymbols()) {
            TabExtended.insert(local.getKind(), local.getName(), local.getType());
        }
        TabExtended.chainLocalSymbols(dst);
        TabExtended.closeScope();
    }

    public static void dump() {
        dump(new DumpSymbolTableVisitorExtended());
    }

    public static Obj insertChangeable(int kind, String name, Struct type) {
        // create a new Object node with kind, name, type
        Obj newObj = new Obj(kind, name, type, 0, ((currentLevel != 0) ? 1 : 0));
        Obj toInsert = new ObjDecorator(newObj, type);

        // append the node to the end of the symbol list
        if (!currentScope.addToLocals(toInsert)) {
            Obj res = currentScope.findSymbol(name);
            return (res != null) ? res : noObj;
        } else
            return newObj;
    }

    public static Obj insertChangeable(Obj obj) {
        // clone object node and return this
        Obj cloned = TabExtended.insertChangeable(obj.getKind(), obj.getName(), obj.getType());
        copyLocalSymbols(obj, cloned);

        return cloned;
    }

    public static Obj insert(int kind, String name, Struct type, SyntaxNode syntaxNode) {
        if (!VisitorUtils.checkAlreadyDeclared(name, type, syntaxNode)) {
            Obj returned = TabExtended.insert(kind, name, type);
            if (returned.getType() != type) {
                assert returned.getClass() == ObjDecorator.class; // must be true
                ((ObjDecorator) returned).setType(type);
            }

            return returned;
        } else {
            return TabExtended.noObj;
        }
    }

    public static Obj insert(int kind, String name, Struct type) {
        // create a new Object node with kind, name, type
        Obj newObj = new Obj(kind, name, type, 0, ((currentLevel != 0) ? 1 : 0));

        // append the node to the end of the symbol list
        if (!currentScope.addToLocals(newObj)) {
            Obj res = currentScope.findSymbol(name);
            return (res != null) ? res : noObj;
        } else
            return newObj;
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

    public static void openScope() {
        currentScope = new Scope(currentScope);
        currentLevel++;
    }

    public static void closeScope() {
        currentScope = currentScope.getOuter();
        currentLevel--;
    }
}
