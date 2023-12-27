package mjparser;

import ast.Program;
import codegen.visitors.CodeGenerator;
import java_cup.runtime.Symbol;
import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;
import rs.etf.pp1.mj.runtime.Code;
import semantics.decorators.TabExtended;
import semantics.visitors.SemanticPass;
import util.Log4JUtils;

import java.io.*;
import java.nio.file.Files;

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
            // TODO enable/disable this using args
//            log.info(program.toString(""));
            log.info("===================================");

            // ispis prepoznatih programskih konstrukcija
            SemanticPass semanticPass = SemanticPass.getInstance();
            program.traverseBottomUp(semanticPass);

            log.info("===================================");
            TabExtended.dump();

            log.info("===================================");
            if (!p.errorDetected && semanticPass.passed()) {
                log.info("Parsing and semantic analysis successful!");

                String outputFile = "output/program.obj";
                File objFile = new File(outputFile);

                // Check if the directory exists, if not, create it
                File outputDirectory = objFile.getParentFile();
                if (!outputDirectory.exists()) {
                    if (!outputDirectory.mkdirs()) {
                        log.error("Failed to create output directory");
                        // Handle the failure to create the directory as needed
                    }
                }

                if (objFile.exists() && objFile.delete()) {
                    log.info("Regenerating file " + outputFile);
                }

                CodeGenerator codeGenerator = CodeGenerator.getInstance();
                program.traverseBottomUp(codeGenerator);
                Code.dataSize = semanticPass.getnVars();
                Code.mainPc = codeGenerator.getMainPC();
                Code.write(Files.newOutputStream(objFile.toPath()));


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
