package mjparser;

import ast.StatementPrint;
import ast.VarDeclClass;
import ast.VisitorAdaptor;
import org.apache.log4j.Logger;

public class RuleVisitor extends VisitorAdaptor {

    Logger log = Logger.getLogger(MJParser.class);
    int printCallCount = 0;
    int varDeclCount = 0;

    public void visit(VarDeclClass varDecl) {
        varDeclCount++;
    }

    public void visit(StatementPrint stmtPrint) {
        printCallCount++;
        log.info("Recognized print command!");
    }
}
