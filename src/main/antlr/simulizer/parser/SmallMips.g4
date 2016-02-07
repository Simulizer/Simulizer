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

program
    : (dataSegment | textSegment | EOL)* EOF?
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
    : LABEL_ID ':'
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
    : register    // register value
    | integer     // literal integer value
    | address
    ;

address
    : registerAddress                  // register addressing: register value as address
    | integer                          // immediate addressing: will never be matched by operand rule
    | integer registerAddress          // base-offset addressing: register value +/- offset
    | LABEL_ID                         // immediate addressing
    | LABEL_ID SIGN unsignedInteger registerAddress? // immediate addressing with offset
    ;

registerAddress
    : '(' register ')'
    ;


// comment
comment
    : COMMENT
    ;


// small components
instruction
    : INSTRUCTION_ID
    ;

register
    : '$' REGISTER_ID
    ;

string
    : STRING_LITERAL
    ;

integer
    : SIGN? denInt
    | SIGN? hexInt
    ;

unsignedInteger
    : denInt
    | hexInt
    ;
denInt
    : DEN_INT
    ;
hexInt
    : HEX_INT
    ;




/////////////////////////////////////////////////
// Lexer Rules (tokens/words)
/////////////////////////////////////////////////

// instruction
INSTRUCTION_ID
    : R_TYPE_INSTRUCTION | I_TYPE_INSTRUCTION
    | J_TYPE_INSTRUCTION | MISC_INSTRUCTION
    ;

R_TYPE_INSTRUCTION // register-type instruction
    : 'add' | 'sub'
    ;

I_TYPE_INSTRUCTION // immediate-type instruction
    : 'addi' | 'subi'
    | 'li'
    ;

J_TYPE_INSTRUCTION // jump-type instruction
    : 'jal'
    ;

MISC_INSTRUCTION
    : 'syscall'
    ;



// directive
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




// http://logos.cs.uic.edu/366/notes/mips%20quick%20tutorial.htm
// in order of 'register number'. Some names are synonyms.
REGISTER_ID
  : 'zero'
  | 'at'
  | 'v0' | 'v1'
  | 'a0' | 'a1' | 'a2' | 'a3'
  | 't0' | 't1' | 't2' | 't3' | 't4' | 't5' | 't6' | 't7'
  | 's0' | 's1' | 's2' | 's3' | 's4' | 's5' | 's6' | 's7'
  | 't8' | 't9'
  | 'k0' | 'k1'
  | 'gp'
  | 'sp'
  | 's8' | 'fp'
  | 'ra'
  ;




// unlike SPIM, not allowing dot as a valid character as this might be confusing
LABEL_ID
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


// denary (base 10) integer with no +/- sign
DEN_INT
    : [0-9]+
    ;

// hex (base 16) integer with no +/- sign
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

