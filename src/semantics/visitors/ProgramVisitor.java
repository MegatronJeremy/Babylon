package semantics.visitors;

import ast.ProgName;
import ast.Program;
import ast.VisitorAdaptor;
import rs.etf.pp1.symboltable.concepts.Obj;
import semantics.TabExtended;

public class ProgramVisitor extends VisitorAdaptor {

    private final SemanticPass semanticPass;

    ProgramVisitor(SemanticPass semanticPass) {
        this.semanticPass = semanticPass;
    }

    public void visit(ProgName progName) {
        progName.obj = TabExtended.insert(Obj.Prog, progName.getProgName(), TabExtended.noType);
        TabExtended.openScope();
    }

    public void visit(Program program) {
        this.semanticPass.nVars = TabExtended.currentScope().getnVars();
        TabExtended.chainLocalSymbols(program.getProgName().obj);
        TabExtended.closeScope();
    }
}
