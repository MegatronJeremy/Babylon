package mjparser;

import java_cup.runtime.Symbol;
import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;
import util.Log4JUtils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;

public class MJLexerTest {

    static {
        DOMConfigurator.configure(Log4JUtils.instance().findLoggerConfigFile());
        Log4JUtils.instance().prepareLogFile(Logger.getRootLogger());
    }

    public static void main(String[] args) {
        Logger log = Logger.getLogger(MJLexerTest.class);

        String sourceFile = "test/program.mj";
        try (Reader br = new BufferedReader(new FileReader(sourceFile))) {
            log.info("Compiling source file: " + sourceFile);

            Yylex lexer = new Yylex(br);
            Symbol currToken;

            while ((currToken = lexer.next_token()).sym != sym.EOF) {
                if (currToken.value != null) {
                    log.info(currToken + " " + currToken.value);
                }
            }
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
    }
}
