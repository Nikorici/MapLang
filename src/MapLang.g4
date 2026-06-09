grammar MapLang;


// =======================
// PARSER RULES
// =======================

program
    : importStmt* partDecl* mapDecl EOF
    ;

importStmt
    : IMPORT STRING SEMI
    ;

partDecl
    : PART ID LBRACE letStmt* sizeDecl statement* RBRACE
    ;

mapDecl
    : MAP ID LBRACE mapBody RBRACE
    ;

mapBody
    : letStmt* sizeDecl statement*
    ;

sizeDecl
    : SIZE expr COMMA expr SEMI
    ;

statement
    : tileStmt
    | fillStmt
    | rowStmt
    | playerStmt
    | repeatStmt
    | randomStmt
    | letStmt
    | placeStmt
    | neighborStmt
    | portalStmt
    ;

tileStmt
    : TILE ID AT LPAREN expr COMMA expr RPAREN SEMI
    ;

fillStmt
    : FILL ID FROM LPAREN expr COMMA expr RPAREN TO LPAREN expr COMMA expr RPAREN SEMI
    ;

rowStmt
    : ROW ID FROM LPAREN expr COMMA expr RPAREN TO LPAREN expr COMMA expr RPAREN SEMI
    ;

playerStmt
    : PLAYER AT LPAREN expr COMMA expr RPAREN SEMI
    ;

repeatStmt
    : REPEAT ID FROM expr TO expr LBRACE statement* RBRACE
    ;

randomStmt
    : RANDOM ID COUNT expr IN LPAREN expr COMMA expr RPAREN TO LPAREN expr COMMA expr RPAREN SEMI
    ;

letStmt
    : LET ID ASSIGN expr SEMI
    ;

placeStmt
    : PLACE ID AT LPAREN expr COMMA expr RPAREN SEMI
    ;

neighborStmt
    : NEIGHBOR (NORTH | SOUTH | EAST | WEST) (STRING | BACK) SEMI
    ;

portalStmt
    : PORTAL AT LPAREN expr COMMA expr RPAREN ARROW (STRING | BACK) SEMI
    ;

// =======================
// EXPRESSIONS
// =======================

expr
    : expr op=(STAR | SLASH | MOD) expr     # MulDivMod
    | expr op=(PLUS | MINUS) expr           # AddSub
    | NUMBER                                 # Num
    | ID                                     # Var
    | LPAREN expr RPAREN                     # Paren
    ;

// =======================
// LEXER RULES
// =======================

// KEYWORDS (must come before ID)
MAP      : 'map';
SIZE     : 'size';
TILE     : 'tile';
FILL     : 'fill';
ROW      : 'row';
FROM     : 'from';
TO       : 'to';
PLAYER   : 'player';
AT       : 'at';
REPEAT   : 'repeat';
RANDOM   : 'random';
COUNT    : 'count';
IN       : 'in';
LET      : 'let';
PART     : 'part';
PLACE    : 'place';
IMPORT   : 'import';
NEIGHBOR : 'neighbor';
NORTH    : 'north';
SOUTH    : 'south';
EAST     : 'east';
WEST     : 'west';
PORTAL   : 'portal';
BACK     : 'back';

// OPERATORS
ASSIGN  : '=';
PLUS    : '+';
MINUS   : '-';
STAR    : '*';
SLASH   : '/';
MOD     : '%';

// SYMBOLS
LBRACE   : '{';
RBRACE   : '}';
LPAREN   : '(';
RPAREN   : ')';
LBRACKET : '[';
RBRACKET : ']';
COMMA    : ',';
SEMI     : ';';
ARROW    : '->';

// IDENTIFIERS
ID      : [a-zA-Z_][a-zA-Z0-9_]*;

// NUMBERS
NUMBER  : [0-9]+;

// STRING (double-quoted, no escapes — enough for filenames)
STRING  : '"' ~["\r\n]* '"';

// COMMENTS
COMMENT : '//' ~[\r\n]* -> skip;

// WHITESPACE
WS      : [ \t\r\n]+ -> skip;
