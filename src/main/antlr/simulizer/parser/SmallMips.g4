grammar SmallMips;

@header {
    package simulizer.parser;
}

/////////////////////////////////////////////////
// Notes on Terminology
/////////////////////////////////////////////////
//
// For general instruction set and MIPS terminology:
// see docs/glossary.md
//
//  Lexer:
//      Recognises words of the language.
//      Deals with regular grammar.
//
//  Parser:
//      Recognises structure of phrases in the language.
//      Deals with context-free grammar.
//


/////////////////////////////////////////////////
// Parser Rules
/////////////////////////////////////////////////

program
    : (line? EOL)+
    ;

dataSegment
    :
    ;

textSegment
    :
    ;

line
   : statement COMMENT?
   | COMMENT
   ;

statement
  : instruction3
  | instruction2
  | instruction3v
  | instruction2v
  | instruction0
  ;

instruction3
  : opcode3  register ',' register ',' register
  ;

instruction2
  : opcode2  register ',' register
  ;

instruction3v
  : opcode3v register ',' register ',' NUMBER
  ;

instruction2v
  : opcode2v register ',' NUMBER
  ;

instruction0
  : opcode0
  ;

opcode
  : opcode3 | opcode2 | opcode3v | opcode2v | opcode0
  ;

opcode3
  : OPCODE3
  ;

opcode2
  : OPCODE2
  ;

opcode3v
  : OPCODE3V
  ;

opcode2v
  : OPCODE2V
  ;

opcode0
  : OPCODE0
  ;

register
  : REGISTER
  ;



// ------ Lexer Rules ------

OPCODE3
  : 'add' | 'sub'
  ;

OPCODE2
  : 'mult' | 'div'
  ;

OPCODE3V
  : 'addi'
  ;

OPCODE2V
  : 'li'
  ;

OPCODE0
  : 'syscall'
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

REGISTER
  : '$' REGISTER_ID
  ;

ANNOTATION
  : '@' ~ [\r\n]*
  ;

NUMBER
  : [0-9]+
  ;

COMMENT
  : '#' ~ [\r\n]*
  ;

EOL
   : '\r'? '\n'
   ;

WS
   : [ \t] -> skip
   ;

