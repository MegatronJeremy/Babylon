package mjparser;

import java_cup.runtime.Symbol;

%%

%{
    // token position info
    private Symbol NewSymbol(int type) {
        return new Symbol(type, yyline+1, yycolumn);
    }

    // token position info
    private Symbol NewSymbol(int type, Object value) {
        return new Symbol(type, yyline+1, yycolumn, value);
    }

%}

%cup
%line
%column

%x COMMENT

%eofval{
    return NewSymbol(sym.EOF);
%eofval}

%%

" "     { }      // Ignore whitespace
"\b"    { }      // Ignore backspace
"\t"    { }      // Ignore tab
"\r\n"  { }      // Ignore carriage return + newline
"\f"    { }      // Ignore form feed

"program"   { return NewSymbol(sym.PROGRAM, yytext()); }
"break"     { return NewSymbol(sym.BREAK, yytext()); }
"class"     { return NewSymbol(sym.CLASS, yytext()); }
"else"      { return NewSymbol(sym.ELSE, yytext()); }
"const"     { return NewSymbol(sym.CONST, yytext()); }
"if"        { return NewSymbol(sym.IF, yytext()); }
"new"       { return NewSymbol(sym.NEW, yytext()); }
"print"     { return NewSymbol(sym.PRINT, yytext()); }
"read"      { return NewSymbol(sym.READ, yytext()); }
"return"    { return NewSymbol(sym.RETURN, yytext()); }
"void"      { return NewSymbol(sym.VOID, yytext()); }
"extends"   { return NewSymbol(sym.EXTENDS, yytext()); }
"continue"  { return NewSymbol(sym.CONTINUE, yytext()); }
"for"       { return NewSymbol(sym.FOR, yytext()); }
"static"    { return NewSymbol(sym.STATIC, yytext()); }
"namespace" { return NewSymbol(sym.NAMESPACE, yytext()); }

// For numeric constants
[0-9]+ { return NewSymbol(sym.NUM_CONST, Integer.parseInt(yytext())); }

// For boolean constants
"true" { return NewSymbol(sym.BOOL_CONST, true); }
"false" { return NewSymbol(sym.BOOL_CONST, false); }

// For identifiers
[a-zA-Z][a-zA-Z0-9_]* { return NewSymbol(sym.IDENT, yytext()); }

// For character constants (only include printable characters)
"'"[ -~]"'" {
    Character value = yytext().substring(1, 2).charAt(0);
        return NewSymbol(sym.CHAR_CONST, value);
}

// Generators for operators, e.g., "+", "-", "*", "/", etc.
"+"     { return NewSymbol(sym.PLUS, yytext()); }
"-"     { return NewSymbol(sym.MINUS, yytext()); }
"*"     { return NewSymbol(sym.MULTIPLY, yytext()); }
"/"     { return NewSymbol(sym.DIVIDE, yytext()); }
"%"     { return NewSymbol(sym.MODULO, yytext()); }
"=="    { return NewSymbol(sym.EQUAL, yytext()); }
"!="    { return NewSymbol(sym.NOT_EQUAL, yytext()); }
">"     { return NewSymbol(sym.GREATER, yytext()); }
">="    { return NewSymbol(sym.GREATER_EQUAL, yytext()); }
"<"     { return NewSymbol(sym.LESS, yytext()); }
"<="    { return NewSymbol(sym.LESS_EQUAL, yytext()); }
"&&"    { return NewSymbol(sym.AND, yytext()); }
"||"    { return NewSymbol(sym.OR, yytext()); }
"="     { return NewSymbol(sym.ASSIGN, yytext()); }
"++"    { return NewSymbol(sym.INCREMENT, yytext()); }
"--"    { return NewSymbol(sym.DECREMENT, yytext()); }
";"     { return NewSymbol(sym.SEMI, yytext()); }
":"     { return NewSymbol(sym.COLON, yytext()); }
","     { return NewSymbol(sym.COMMA, yytext()); }
"."     { return NewSymbol(sym.PERIOD, yytext()); }
"("     { return NewSymbol(sym.LPAREN, yytext()); }
")"     { return NewSymbol(sym.RPAREN, yytext()); }
"["     { return NewSymbol(sym.LBRACKET, yytext()); }
"]"     { return NewSymbol(sym.RBRACKET, yytext()); }
"{"     { return NewSymbol(sym.LBRACE, yytext()); }
"}"     { return NewSymbol(sym.RBRACE, yytext()); }

// Ignore comments
"//"    { yybegin(COMMENT); }

<COMMENT> "\r\n"      { yybegin(YYINITIAL); /* End of comment, return to initial state */ }
<COMMENT> [^\r\n]*    { /* Ignore characters within a comment */ }

// Log errors
. { System.err.println("Lexical error ("+yytext()+") in line "+(yyline+1)); }
