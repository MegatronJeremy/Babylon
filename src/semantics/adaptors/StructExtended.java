package semantics.adaptors;

import rs.etf.pp1.symboltable.concepts.Struct;
import rs.etf.pp1.symboltable.structure.SymbolDataStructure;

import java.util.Objects;

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

    private boolean checkClassAssignability(Struct src, Struct dst) {
        // find if dest class exists in src hierarchy
        Struct currType = src;
        while (currType != TabExtended.noType) {
            // compare names in hierarchy
            if (Objects.equals(currType.toString(), dst.toString()))
                return true;
            currType = currType.getElemType(); // go up the hierarchy
        }

        return false;
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

    public boolean compatibleWith(Struct other) {
        if (this.getKind() == Class && other.getKind() == Class &&
                (checkClassAssignability(this, other) || checkClassAssignability(other, this))) {
            return true;
        }

        return super.compatibleWith(other);
    }

    @Override
    public boolean assignableTo(Struct dest) {
        // override special case when object of inherited class is assigned to base class
        if (this.getKind() == Class && dest.getKind() == Class && checkClassAssignability(this, dest)) {
            return true;
        }

        return super.assignableTo(dest);
    }
}
