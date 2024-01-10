package semantics.decorators;

import rs.etf.pp1.symboltable.concepts.Obj;
import rs.etf.pp1.symboltable.concepts.Struct;
import rs.etf.pp1.symboltable.structure.SymbolDataStructure;
import rs.etf.pp1.symboltable.visitors.SymbolTableVisitor;

import java.util.Collection;

public class ObjDecorator extends Obj {
    Obj subject;
    Struct addedType;

    public ObjDecorator(Obj obj, Struct addedType) {
        super(0, null, null);
        this.subject = obj;
        this.addedType = addedType;
    }

    @Override
    public Struct getType() {
        return addedType;
    }

    public void setType(Struct type) {
        addedType = type;
    }

    public int getKind() {
        return subject.getKind();
    }

    public String getName() {
        return subject.getName();
    }

    public int getAdr() {
        return subject.getAdr();
    }

    public void setAdr(int adr) {
        subject.setAdr(adr);
    }

    public int getLevel() {
        return subject.getLevel();
    }

    public void setLevel(int level) {
        subject.setLevel(level);
    }

    public int getFpPos() {
        return subject.getFpPos();
    }

    public void setFpPos(int fpPos) {
        subject.setFpPos(fpPos);
    }

    public Collection<Obj> getLocalSymbols() {
        return subject.getLocalSymbols();
    }

    public void setLocals(SymbolDataStructure locals) {
        subject.setLocals(locals);
    }

    public boolean equals(Object o) {
        return subject.equals(o);
    }

    public void accept(SymbolTableVisitor stv) {
        super.accept(stv);
    }
}
