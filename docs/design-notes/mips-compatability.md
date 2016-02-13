# MIPS Compatibility #
Simulizer aims to simulate a compatible subset of the MIPS32 ISA, using the
official documentation as reference.

The MicroMIPS32 microarchitecture will be drastically simplified but still
accurate enough to drive a visualisation for teaching purposes.

Simulizer will aim to be moderately compatible with the [SPIM
simulator](http://spimsimulator.sourceforge.net/) in functionality.

This document aims to highlight the differences in our simplification and the
real thing.

- [MIPS32 Homepage](https://imgtec.com/mips/architectures/mips32/)
- [MIPS32 ISA Specification v6.01](https://imgtec.com/?do-download=4278)
- [MIPS32 Instruction set v6.04](https://imgtec.com/?do-download=4287)
- [MicroMIPS32 Microarchitecture Specification v6.0](https://imgtec.com/?do-download=6102)

# Considering To Change #
- unlimited jump distance
- ignoring `.align` directive
- pseudo-ops interpreted as single instructions


# Completely Removed Features #
- coprocessors and related instructions
    - FPU and floating point variables
- virtual memory and access control
- superpipeline
- superscalar pipeline
- (like SPIM) no delayed branches
- (unlike SPIM) dots (`.`) are not allowed in identifiers (labels)

# Added Features #
- configurable clock speed
- jumps may be any distance because they are stored as full 32 bit or 64 bit
  offsets.


# Instruction Set #
Functional groups as outlined in the microarchitecture spec:

Note: Prioritising the SPIM instruction set over the official MIPS32 spec as
recent revisions have removed some useful instructions like `addi`.

## Load and Store ##

## Computational ##

## Jump and Branch ##

## Miscellaneous ##





# Memory #
- (like SPIM) the entirety of main memory is assumed to be cached and can be
  fetched in a single clock cycle.


## OS ##
The OS does not reside in the simulator's memory (as it is written in Java). Any
program which attempts to access the area of memory reserved for the OS will
crash.

## Text Segment ##
The program will not be held in the main memory of the simulation. Any program
which attempts to access the `.text` segment of the program will crash.

## Static Data Segment ##

## Dynamic Data Segment ##

## Stack Segment ##
The stack will appear to be held in main memory but in fact it will not. This
should be transparent to the program.




# Pipeline #
- Simulizer supports both the "One-Deep Single-Completion Instruction Pipeline"
  and the "Four-Deep Single-Completion Pipeline" as described in the MIPS32 ISA
  specification.

# Interrupts and Exceptions #
The simulator only supports the following interrupts and exceptions:
- System call exception

## System Calls ##
Compared to the SPIM simulator, Simulizer only supports these system calls:
- 1:  `print_int`
- 4:  `print_string`
- 5:  `read_int`
- 8:  `read_string`
- 9:  `sbrk`
- 10: `exit`
- 11: `print_char`
- 12: `read_char`




# Assembler #
Compared to the SPIM simulator's assembler.

## Syntax Differences ##
Simulizer supports these escape sequences:
- `\t`     tab
- `\n`     newline
- `\"`     `"`
- `\\`     `\`
- `\nnn`   octal ASCII character code (1-3 digits)
- `\xhh`   hexadecimal ASCII character code (2 digits)

differences:
- Spim allowed unmatched `\` characters to be treated literally whereas Simulizer
  requires that they be properly escaped
- Spim handled octal escape characters incorrectly
- Spim requires all octal escape sequences to have 3 digits
- Spim has some bugs regarding escaping (see spim behaviour tests)
    - SPIM parses `.asciiz "abc\\"` as the string: `abc\"`
    - SPIM parses `.asciiz "\101"` (effects all codes above `\077`) as the octal
      code `\011` which is incorrect.

## Different From SPIM ##
- macros are not supported
- linking is not supported. Programs must be self-contained in a single file.
- pseudo-operations are considered to be single instructions.
- arguments *must* be separated by a comma whereas SPIM allows these to be omitted.
- (//TODO: untested) Only big endian byte order (SPIM supports both). So
  `.byte 0,1,2,3` produces `lowest address [0, 1, 2, 3] highest address`
- do not use the `lo` and `hi` registers for storing results of multiplication or division
- negative arguments to `sbrk` are allowed
- character literals for initialising `.byte` is not supported
- when providing integer literals that are too big for the size of the variable,
  SPIM will silently allow it, while giving incorrect answers. Simulizer
  instead refuses to proceed.
- SPIM does not allow a uppercase `0X` to denote a hex number

## Intentionally The Same As SPIM ##
These behaviours have been tested to hold in SPIM.

- SPIM's OS jumps to the label 'main' so this is always the entry point for the
  program. Like SPIM this label does not necessarily have to be declared `.globl`
- label names must be unique
- The assembler inserts a newline at the end of every program to correctly parse
  programs that do not end in a newline already.
- registers must have brackets when doing base/offset addressing even when the
  offset is zero eg the brackets here are necessary: `lw $a0, ($s0)`.
- The register must not be bracketed when doing register addressing. eg this is
  not allowed: `jr ($s0)`
- register names must be lower case
- instruction names must be lower case
- registers may be referred to by either a mnemonic or a number eg `$v0 == $2`
  Simulizer assigns the same numbers as SPIM does.
- Simulizer favours the instruction set supported by SPIM to the official MIPS32
  specification. This is because recent revisions of MIPS32 have removed some
  useful instructions such as `addi`
- (//TODO: untested) jumping to an address that is not 4-byte aligned will crash.
- arguments to `sbrk` must be a multiple of 4, since the resulting addresses
  must be 4-byte aligned as per the specification.


## Addressing Modes ##
Simulizer supports all addressing modes supported by SPIM:
- `(register)`
- `imm`
- `imm (register)`
- `label`
- `label +/- imm`
- `label +/- imm (register)`
    - `address of label +/- (imm + register contents)`



## Directives ##
- `.data` is the only type of data segment supported (see below)
- SPIM allows `.word` directives inside the `.text` segment. Simulizer does not
  allow this.

The following SPIM directives are allowed, but ignored:
- `.align`
- `.globl`
- for the directives: `.data <addr>` and `.text <addr>` the addresses are ignored

The following SPIM directives are not supported and will not assemble:
- `.rdata`, `.sdata`, `.kdata`
- `.float`
- `.double`
- `.extern`
- `.ktext`
- `.set`


## Instructions ##

