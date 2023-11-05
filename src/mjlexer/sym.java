package mjlexer;

public class sym {
    // Keywords
    public static final int PROGRAM = 1;
    public static final int BREAK = 2;
    public static final int CLASS = 3;
    public static final int ELSE = 4;
    public static final int CONST = 5;
    public static final int IF = 6;
    public static final int WHILE = 7;
    public static final int NEW = 8;
    public static final int PRINT = 9;
    public static final int READ = 10;
    public static final int RETURN = 11;
    public static final int VOID = 12;
    public static final int EXTENDS = 13;
    public static final int CONTINUE = 14;
    public static final int FOREACH = 15;

    // Token types
    public static final int IDENT = 16;
    public static final int NUM_CONST = 17;
    public static final int CHAR_CONST = 18;
    public static final int BOOL_CONST = 19;

    // Operators
    public static final int PLUS = 20;
    public static final int MINUS = 21;
    public static final int MULTIPLY = 22;
    public static final int DIVIDE = 23;
    public static final int MODULO = 24;
    public static final int EQUAL = 25;
    public static final int NOT_EQUAL = 26;
    public static final int GREATER = 27;
    public static final int GREATER_EQUAL = 28;
    public static final int LESS = 29;
    public static final int LESS_EQUAL = 30;
    public static final int AND = 31;
    public static final int OR = 32;
    public static final int ASSIGN = 33;
    public static final int INCREMENT = 34;
    public static final int DECREMENT = 35;
    public static final int SEMI = 36;
    public static final int COLON = 37;
    public static final int COMMA = 38;
    public static final int PERIOD = 39;
    public static final int LPAREN = 40;
    public static final int RPAREN = 41;
    public static final int LBRACKET = 42;
    public static final int RBRACKET = 43;
    public static final int LBRACE = 44;
    public static final int RBRACE = 45;
    public static final int ARROW = 46;

    // Special tokens
    public static final int EOF = 47;
}