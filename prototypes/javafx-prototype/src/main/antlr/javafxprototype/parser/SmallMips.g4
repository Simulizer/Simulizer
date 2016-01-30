grammar SmallMips;

@header {
    package javafxprototype.parser;
}

// ------ Parser Rules ------

program
  : (line? EOL)+
  ;

line
   : instruction COMMENT?
   | COMMENT
   ;

instruction
  : opcode3  register ',' register ',' register
  | opcode2  register ',' register
  | opcode3v register ',' register ',' NUMBER
  | opcode2v register ',' NUMBER
  ;

opcode
  : opcode3 | opcode2 | opcode3v | opcode2v
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
  : '@' ~ [\r\n]* -> skip
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

