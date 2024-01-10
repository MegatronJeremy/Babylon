package codegen.adaptors;

import rs.etf.pp1.mj.runtime.Code;

public class CodeExtended extends Code {
    public static void put4(int pos, int x) {
        int stari = pc;
        pc = pos;
        put4(x);
        pc = stari;
    }

    public static void fixup4(int patchAdr, int value) {
        put4(patchAdr, value);
    }
}
