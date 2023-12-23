package semantics.visitors;

import ast.ProgName;
import ast.Program;
import ast.VisitorAdaptor;
import rs.etf.pp1.symboltable.concepts.Obj;
import semantics.adaptors.TabAdaptor;
import semantics.util.LogUtils;

public class ProgramVisitor extends VisitorAdaptor {

    private final SemanticPass semanticPass;

    ProgramVisitor(SemanticPass semanticPass) {
        this.semanticPass = semanticPass;
    }

    public void visit(ProgName progName) {
        progName.obj = TabAdaptor.insert(Obj.Prog, progName.getProgName(), TabAdaptor.noType);
        TabAdaptor.openScope();

        semanticPass.programScope = TabAdaptor.currentScope;
    }

    public void visit(Program program) {
        this.semanticPass.nVars = TabAdaptor.currentScope().getnVars();

        // Check for main function validity
        Obj mainFunc = TabAdaptor.find("main");

        if (mainFunc == TabAdaptor.noObj) {
            LogUtils.logError("Global namespace main function declaration not found");
        } else if (mainFunc.getType() != TabAdaptor.noType) {
            LogUtils.logError("Main function has non-void return type");
        }

        TabAdaptor.chainLocalSymbols(program.getProgName().obj);
        TabAdaptor.closeScope();
    }
}
