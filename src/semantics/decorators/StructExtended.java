package semantics.decorators;

import rs.etf.pp1.symboltable.concepts.Struct;
import rs.etf.pp1.symboltable.structure.SymbolDataStructure;

public class StructExtended extends Struct {
    private String name = null;

    public StructExtended(int kind, String name) {
        super(kind);
        this.name = name;
    }

    public StructExtended(int kind, Struct elemType, String name) {
        super(kind, elemType);
        this.name = name;
    }

    public StructExtended(int kind, Struct elemType) {
        super(kind, elemType);
    }

    public StructExtended(int kind, SymbolDataStructure members) {
        super(kind, members);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public boolean assignableTo(Struct dest) {
        // override special case when object of inherited class is assigned to base class
        if (this.getKind() == Class && dest.getKind() == Class) {
            // find if dest class exists in src hierarchy
            Struct currType = this;
            while (currType != TabExtended.noType) {
                if (currType == dest) return true;
                currType = currType.getElemType(); // go up the hierarchy
            }

            return false;
        } else {
            return super.assignableTo(dest);
        }
    }
}
