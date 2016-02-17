grammar SmallMips;

@header {
    package simulizer.parser;
}

/////////////////////////////////////////////////
// Notes on Terminology
/////////////////////////////////////////////////
//
// For general instruction set and MIPS terminology:
// see docs/technology-research/mips/overview-notes.md
//
//  Lexer:
//      Recognises words of the language.
//      Deals with regular grammar.
//
//  Parser:
//      Recognises structure of phrases in the language.
//      Deals with context-free grammar.
//
// Notes on Antlr
// - if multiple lexer rules match, the first one is chosen
// - before parsing, the input must unambiguously be split into lexer tokens




/////////////////////////////////////////////////
// Parser Rules (grammar / syntax)
/////////////////////////////////////////////////

// lines are allowed outside either segment to allow for comments only.
program
    : (dataSegment | textSegment | line)* EOF?
    ;

dataSegment
    : dataDirective (EOL|line)+
    ;

textSegment
    : textDirective (EOL|line)+
    ;

dataDirective
    : '.' 'data' directiveOperandList?
    ;
textDirective
    : '.' 'text' directiveOperandList?
    ;

// the first string is parsed as a single line, the second as two
// add ; # comment
// add ; sub ; # comment
line
    : label? directive? statement? ';'? comment? (EOL|';')
    ;

// label
label
    : labelID ':'
    ;

// directive
directive
    : '.' DIRECTIVE_ID directiveOperandList?
    ;

directiveOperandList
    : directiveOperand (',' directiveOperand)*
    ;

directiveOperand
    : integer
    | string
    | address
    ;

// statement
statement
    : instruction operandList?
    ;


operandList
    : operand (',' operand)*
    ;

operand
    : register // register value
    | integer  // literal integer value
    | address
    ;

address
    : baseAddress                               // register addressing: register value as address
  //| integer                                   // immediate addressing: will never be matched by operand rule
    | integer baseAddress                       // base-offset addressing: register value +/- offset
    | labelID                                   // immediate addressing
    | labelID SIGN unsignedInteger baseAddress? // immediate addressing with offset
    ;

baseAddress
    : '(' register ')'
    ;


// comment
comment
    : COMMENT
    ;


// small components
instruction
    : IDENTIFIER
    ;
labelID
    : IDENTIFIER
    ;

register
    : '$' registerID
    ;
registerID
    : IDENTIFIER
    | unsignedInteger
    ;

string
    : STRING_LITERAL
    ;

integer
    : SIGN? decInt
    | SIGN? hexInt
    ;
unsignedInteger
    : decInt
    | hexInt
    ;
decInt
    : DEC_INT
    ;
hexInt
    : HEX_INT
    ;




/////////////////////////////////////////////////
// Lexer Rules (tokens/words)
/////////////////////////////////////////////////

// directive
// matched specifically because no ambiguety due to the preceeding dot
DIRECTIVE_ID
    : 'globl'
    | 'ascii' | 'asciiz'
    | 'byte'  | 'half' | 'word'   // 8, 16, 32 bits
    | 'space'
    | IGNORED_DIRECTIVE_ID
    ;

fragment IGNORED_DIRECTIVE_ID
    : 'align'
    ;


// generic identifier for labels and instructions
IDENTIFIER
    : [a-zA-Z_][a-zA-Z0-9_]*
    ;



COMMENT
    : '#' ~ [\r\n]*
    ;



// from http://stackoverflow.com/a/24559773
STRING_LITERAL
    : UNTERMINATED_STRING_LITERAL '"'
    ;
UNTERMINATED_STRING_LITERAL
    : '"' (~["\\\r\n] | '\\' (. | EOF))*
    ;


// decimal (base 10) integer with no +/- sign
DEC_INT
    : [0-9]+
    ;

// hexadecimal (base 16) integer with no +/- sign
HEX_INT
    : '0x' [0-9a-fA-F]+
    ;


SIGN
    : ('+'|'-')
    ;

EOL
    : '\r'? '\n'
    ;

WS
    : [ \t] -> skip
    ;

