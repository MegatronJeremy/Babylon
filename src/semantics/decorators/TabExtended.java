package semantics.decorators;

import rs.etf.pp1.symboltable.Tab;
import rs.etf.pp1.symboltable.concepts.Obj;
import rs.etf.pp1.symboltable.concepts.Struct;

public class TabExtended extends Tab {
    public static final Struct boolType = new Struct(Struct.Bool);
    private static Obj helperA;
    private static Obj helperB;

    public static void init() {
        Tab.init();

        currentScope.addToLocals(new Obj(Obj.Type, "bool", boolType));
    }

    public static void dump() {
        dump(new DumpSymbolTableVisitorExtended());
    }

    public static Obj getHelperA() {
        return helperA;
    }

    public static Obj getHelperB() {
        return helperB;
    }

    public static void generateHelpers() {
        // open global scope and add two static variables to be used as helpers
        helperA = TabExtended.insert(Obj.Var, "@", TabExtended.intType);
        helperB = TabExtended.insert(Obj.Var, "#", TabExtended.intType);
    }
}
