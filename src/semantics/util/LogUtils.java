package semantics.util;

import ast.SyntaxNode;
import mjparser.MJParser;
import org.apache.log4j.Logger;

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

    public static String kindToString(int kind) {
        switch (kind) {
            case 0:
                return "None";
            case 1:
                return "Int";
            case 2:
                return "Char";
            case 3:
                return "Array";
            case 4:
                return "Class";
            case 5:
                return "Bool";
            case 6:
                return "Enum";
            case 7:
                return "Interface";
            default:
                return "Unknown"; // Handle unknown values if necessary
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
