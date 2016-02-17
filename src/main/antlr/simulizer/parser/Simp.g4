grammar Simp;

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
// - inside lexer rules, whitespace is still counted, but not inside parser rules
// - within rules, the first versions of the rule take precedence if the input is ambiguous




/////////////////////////////////////////////////
// Parser Rules (grammar / syntax)
/////////////////////////////////////////////////

// lines are allowed outside either segment to allow for comments only.
program
    : (dataSegment | textSegment | line)* EOF?
    ;

dataSegment
    : dataDirective (EOL|';'|line)+
    ;

textSegment
    : textDirective (EOL|';'|line)+
    ;

dataDirective
    : '.data' directiveOperandList?
    ;
textDirective
    : '.text' directiveOperandList?
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

// attempts the longest rules first if the string is ambiguous
// mylabel + 10($s0) parses as a single address rather than
// [mylabel] [+10] [($s0)]
address
    : labelID SIGN unsignedInteger baseAddress? // immediate addressing with offset
    | integer baseAddress                       // base-offset addressing: register value +/- offset
  //| integer                                   // immediate addressing: will never be matched by operand rule
    | baseAddress                               // register addressing: register value as address
    | labelID                                   // immediate addressing
    ;

baseAddress
    : '(' register ')'
    ;




// directive
directive
    : DIRECTIVE_ID directiveOperandList?
    ;

// optional commas make the language context-sensitive
// consider the string: ".directive mylabel + 10"
// [mylabel+10] and [mylabel] [+10] (where [] denotes a matched address)
// are both valid interpretations
directiveOperandList
    : directiveOperand (','? directiveOperand)*
    ;

directiveOperand
    : integer
    | string
    | address
    ;

// statement
statement
    : instruction statementOperandList?
    ;


// optional commas make the language context-sensitive
// consider the string: "instruction mylabel + 10"
// [mylabel+10] and [mylabel] [+10] (where [] denotes a matched address)
// are both valid interpretations
statementOperandList
    : statementOperand (','? statementOperand)*
    ;

statementOperand
    : register // register value
    | address
    | integer  // literal integer value
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
    : NAMED_REGISTER
    | NUMBERED_REGISTER
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
    : '.globl'
    | '.ascii' | '.asciiz'
    | '.byte'  | '.half' | '.word'   // 8, 16, 32 bits
    | '.space'
    | IGNORED_DIRECTIVE_ID
    ;

IGNORED_DIRECTIVE_ID
    : '.align'
    ;


// generic identifier for labels and instructions
IDENTIFIER
    : [a-zA-Z_][a-zA-Z0-9_]*
    ;


// no whitespace in-between
NAMED_REGISTER
    : '$' IDENTIFIER
    ;
NUMBERED_REGISTER
    : '$' DEC_INT
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
// leading zeroes are allowed
DEC_INT
    : [0-9]+
    ;

// hexadecimal (base 16) integer with no +/- sign
HEX_INT
    : HEX_PREFIX [0-9a-fA-F]+
    ;
HEX_PREFIX
    : '0x'
    | '0X'
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

