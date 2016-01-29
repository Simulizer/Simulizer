/*
BSD License

Copyright (c) 2013, Tom Everett
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions
are met:

1. Redistributions of source code must retain the above copyright
   notice, this list of conditions and the following disclaimer.
2. Redistributions in binary form must reproduce the above copyright
   notice, this list of conditions and the following disclaimer in the
   documentation and/or other materials provided with the distribution.
3. Neither the name of Tom Everett nor the names of its contributors
   may be used to endorse or promote products derived from this software
   without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
"AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/

grammar SmallMips;

@header {
package antlrprototype;
}

// ------ Parser Rules ------

prog
  : (line? EOL) +
  ;

line
   : instruction COMMENT?
   | COMMENT
   ;

instruction
  : opcode3 register ',' register ',' register
  | opcode2 register ',' register
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

/*
OPCODE
  : ADD | 'addi' | SUB | 'subi'
  | SUB | DIV
  | 'lw' | LI
  | 'sw' | 'and' | 'or' | 'xor' | 'nor' 
  | 'beq' | 'bne' | 'j' | 'jr' | 'jal' | 'not'
  | 'bgt' | 'blt' | 'bge' | 'ble'
  | 'blez' | 'bgtz' | 'bnez'
  | 'bltz' | 'bgez'
  ;
*/

REGISTERID
  : 'zero' 
  | 'at' 
  | 'v0' | 'v1'
  | 'a0' | 'a1' | 'a2' | 'a3'
  | 't0' | 't1' | 't2' | 't3' | 't4' | 't5' | 't6' | 't7' | 't8' | 't9' 
  | 's0' | 's1' | 's2' | 's3' | 's4' | 's5' | 's6' | 's7'
  | 'k0' | 'k1'
  | 'gp' 
  | 'fp' 
  | 'ra'
  ;

REGISTER
  : '$' REGISTERID
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

