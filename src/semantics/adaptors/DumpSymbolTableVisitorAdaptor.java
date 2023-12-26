package semantics.adaptors;

import rs.etf.pp1.symboltable.concepts.Struct;
import rs.etf.pp1.symboltable.visitors.DumpSymbolTableVisitor;

public class DumpSymbolTableVisitorAdaptor extends DumpSymbolTableVisitor {
    @Override
    public void visitStructNode(Struct structToVisit) {
        switch (structToVisit.getKind()) {
            case Struct.Bool:
                output.append("bool");
                return;
            case Struct.Array:
                output.append("Arr of ");
                if (structToVisit.getElemType().getKind() == Struct.Bool) {
                    output.append("bool");
                }
                return;
        }

        super.visitStructNode(structToVisit);
    }
}
