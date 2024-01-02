package semantics.visitors;

import ast.ProgName;
import ast.Program;
import ast.VisitorAdaptor;
import rs.etf.pp1.symboltable.concepts.Obj;
import semantics.decorators.TabExtended;
import semantics.util.LogUtils;

public class ProgramVisitor extends VisitorAdaptor {

    private final SemanticPass semanticPass;

    ProgramVisitor(SemanticPass semanticPass) {
        this.semanticPass = semanticPass;
    }

    public void visit(ProgName progName) {
        progName.obj = TabExtended.insert(Obj.Prog, progName.getProgName(), TabExtended.noType);
        TabExtended.openScope();

        semanticPass.programScope = TabExtended.currentScope;

        TabExtended.generateHelpers();
    }

    public void visit(Program program) {
        this.semanticPass.nVars = TabExtended.currentScope().getnVars();

        // Check for main function validity
        Obj mainFunc = TabExtended.find("main$");

        if (mainFunc == TabExtended.noObj) {
            LogUtils.logError("Global namespace main function declaration not found");
        } else if (mainFunc.getType() != TabExtended.noType) {
            LogUtils.logError("Main function has non-void return type");
        }

        TabExtended.chainLocalSymbols(program.getProgName().obj);
        TabExtended.closeScope();
    }
}
