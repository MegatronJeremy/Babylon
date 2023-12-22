package semantics.util;

import ast.SyntaxNode;
import mjparser.MJParser;
import org.apache.log4j.Logger;
import rs.etf.pp1.symboltable.concepts.Obj;
import rs.etf.pp1.symboltable.concepts.Struct;

public class LogUtils {

    private static final Logger log = Logger.getLogger(MJParser.class);
    private static boolean errorDetected = false;

    private static String prepareMessage(String message, SyntaxNode info) {
        int line = (info == null) ? 0 : info.getLine();
        StringBuilder msg = new StringBuilder();

        if (line != 0)
            msg.append("Line ").append(line).append(":\t");
        msg.append(message);

        return msg.toString();
    }

    public static boolean isErrorDetected() {
        return errorDetected;
    }

    public static String structKindToString(int kind) {
        switch (kind) {
            case Struct.None:
                return "None";
            case Struct.Int:
                return "Int";
            case Struct.Char:
                return "Char";
            case Struct.Array:
                return "Array";
            case Struct.Class:
                return "Class";
            case Struct.Bool:
                return "Bool";
            case Struct.Enum:
                return "Enum";
            case Struct.Interface:
                return "Interface";
            default:
                return "Unknown"; // Handle unknown values if necessary
        }
    }

    public static String objKindToString(int value) {
        switch (value) {
            case Obj.Con:
                return "Con";
            case Obj.Var:
                return "Var";
            case Obj.Type:
                return "Type";
            case Obj.Meth:
                return "Meth";
            case Obj.Fld:
                return "Fld";
            case Obj.Elem:
                return "Elem";
            case Obj.Prog:
                return "Prog";
            default:
                return "Unknown";
        }
    }

    public static void logError(String message) {
        logError(message, null);
    }

    public static void logError(String message, SyntaxNode info) {
        errorDetected = true;
        String msg = prepareMessage(message, info);

        log.error(msg);
    }

    public static void logInfo(String message) {
        logInfo(message, null);
    }

    public static void logInfo(String message, SyntaxNode info) {
        String msg = prepareMessage(message, info);

        log.info(msg);
    }
}
