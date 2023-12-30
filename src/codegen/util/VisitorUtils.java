package codegen.util;

import rs.etf.pp1.mj.runtime.Code;
import rs.etf.pp1.symboltable.concepts.Obj;
import rs.etf.pp1.symboltable.concepts.Struct;

public class VisitorUtils {
    public static void generateIncrement(Obj target, int inc) {
        if (target.getLevel() != 0) {
            // local - use inc
            Code.put(Code.inc);
            Code.put(target.getAdr());
            Code.put(inc);
        } else {
            // global
            Code.load(target);
            Code.loadConst(inc);
            Code.put(Code.add);
            Code.store(target);
        }
    }

    public static void generateArrStore(Obj target) {
        if (target.getType().getKind() == Struct.Char) Code.put(Code.bastore);
        else Code.put(Code.astore);
    }

    public static void generateArrLoad(Obj target) {
        if (target.getType().getKind() == Struct.Char) Code.put(Code.baload);
        else Code.put(Code.aload);
    }
}
