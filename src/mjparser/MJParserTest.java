package mjparser;

import ast.Program;
import codegen.visitors.CodeGenerator;
import java_cup.runtime.Symbol;
import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;
import rs.etf.pp1.mj.runtime.Code;
import semantics.adaptors.TabExtended;
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

        Reader bufferedReader = null;
        try {
            File sourceCode = new File(args[0]);
            log.info("Compiling source file: " + sourceCode.getAbsolutePath());

            boolean dumpSyntaxTree = false, dumpSymbolTable = false; // Flag to enable in debug mode

            // Check command line arguments for debug flag
            if (args.length > 1 && args[1].equals("-debug")) {
                for (int i = 2; i < args.length; i++) {
                    if (args[i].equals("tree")) {
                        log.info("Syntax tree output enabled");
                        dumpSyntaxTree = true;
                    }
                    if (args[i].equals("table")) {
                        log.info("Symbol table output enabled");
                        dumpSymbolTable = true;
                    }
                }
            }

            bufferedReader = new BufferedReader(new FileReader(sourceCode));
            Yylex lexer = new Yylex(bufferedReader);

            MJParser parser = new MJParser(lexer);
            Symbol symbol = parser.parse();  //start parsing
            Program program = (Program) (symbol.value); // root of syntax tree

            if (dumpSyntaxTree) {
                // Write syntax tree
                log.info(program.toString(""));
                log.info("===================================");
            }

            if (lexer.errorDetected) {
                log.error("Parsing was not successful!");
                return;
            }

            // Do a semantic pass if lexing was succesful
            TabExtended.init();
            SemanticPass semanticPass = SemanticPass.getInstance();
            program.traverseBottomUp(semanticPass);


            log.info("===================================");
            if (dumpSymbolTable) {
                // Write symbol table
                TabExtended.dump();
            }

            log.info("===================================");
            if (parser.errorDetected || !semanticPass.passed()) {
                log.error("Semantic analysis was not successful!");
                return;
            } else {
                log.info("Parsing and semantic analysis successful!");
            }

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

            Code.dataSize = semanticPass.getVars(); // code size in number of GLOBAL variables

            CodeGenerator codeGenerator = CodeGenerator.getInstance();
            program.traverseBottomUp(codeGenerator);
            Code.mainPc = codeGenerator.getMainPC();

            Code.write(Files.newOutputStream(objFile.toPath()));
        } finally {
            if (bufferedReader != null) try {
                bufferedReader.close();
            } catch (IOException e1) {
                log.error(e1.getMessage(), e1);
            }
        }

    }
}
