grammar MapLang;


// =======================
// PARSER RULES
// =======================

program
    : mapDecl EOF
    ;

mapDecl
    : MAP ID LBRACE mapBody RBRACE
    ;

mapBody
    : sizeDecl statement*
    ;

sizeDecl
    : SIZE NUMBER COMMA NUMBER SEMI
    ;

statement
    : tileStmt
    | playerStmt
    ;

tileStmt
    : TILE ID AT LPAREN NUMBER COMMA NUMBER RPAREN SEMI
    ;

playerStmt
    : PLAYER AT LPAREN NUMBER COMMA NUMBER RPAREN SEMI
    ;

// =======================
// LEXER RULES
// =======================

// KEYWORDS
MAP     : 'map';
SIZE    : 'size';
TILE    : 'tile';
PLAYER  : 'player';
AT      : 'at';

// SYMBOLS
LBRACE  : '{';
RBRACE  : '}';
LPAREN  : '(';
RPAREN  : ')';
COMMA   : ',';
SEMI    : ';';

// IDENTIFIERS
ID      : [a-zA-Z_][a-zA-Z0-9_]*;

// NUMBERS
NUMBER  : [0-9]+;

// WHITESPACE
WS      : [ \t\r\n]+ -> skip;