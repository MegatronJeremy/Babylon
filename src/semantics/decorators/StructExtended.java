package semantics.decorators;

import rs.etf.pp1.symboltable.concepts.Struct;
import rs.etf.pp1.symboltable.structure.SymbolDataStructure;

public class StructExtended extends Struct {
    public StructExtended(int kind) {
        super(kind);
    }

    public StructExtended(int kind, Struct elemType) {
        super(kind, elemType);
    }

    public StructExtended(int kind, SymbolDataStructure members) {
        super(kind, members);
    }

    @Override
    public boolean assignableTo(Struct dest) {
        // override special case when object of inherited class is assigned to base class
        if (this.getKind() == Class && dest.getKind() == Class
                && this.getElemType() == dest)
            return true;

        return super.assignableTo(dest);
    }
}
