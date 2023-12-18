package mjparser;

import ast.Program;
import java_cup.runtime.Symbol;
import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;
import semantics.TabExtended;
import semantics.visitors.SemanticPass;
import util.Log4JUtils;

import java.io.*;

public class MJParserTest {

    static {
        DOMConfigurator.configure(Log4JUtils.instance().findLoggerConfigFile());
    }

    public static void main(String[] args) throws Exception {
        Logger log = Logger.getLogger(MJParserTest.class);

        Reader br = null;
        try {
            File sourceCode = new File(args[0]);
            log.info("Compiling source file: " + sourceCode.getAbsolutePath());

            br = new BufferedReader(new FileReader(sourceCode));
            Yylex lexer = new Yylex(br);

            MJParser p = new MJParser(lexer);
            Symbol s = p.parse();  //pocetak parsiranja

            Program program = (Program) (s.value);
            TabExtended.init();
            // ispis sintaksnog stabla
            log.info(program.toString(""));
            log.info("===================================");

            // ispis prepoznatih programskih konstrukcija
            SemanticPass v = new SemanticPass();
            program.traverseBottomUp(v);

            log.info("===================================");
            TabExtended.dump();

            log.info("===================================");
            if (!p.errorDetected && v.passed()) {
                log.info("Parsing and semantic analysis successful!");
            } else {
                log.error("Parsing and semantic analysis was not successful!");
            }
        } finally {
            if (br != null) try {
                br.close();
            } catch (IOException e1) {
                log.error(e1.getMessage(), e1);
            }
        }

    }
}
