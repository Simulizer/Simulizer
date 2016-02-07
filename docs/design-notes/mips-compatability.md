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


# Instruction Set #
Functional groups as outlined in the microarchitecture spec:

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
- macros are not supported
- linking is not supported. Programs must be self-contained in a single file.

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
- SPIM allows `.word` directives inside the `.text` segment. Simulizer does not allow this.

The following SPIM directives are allowed, but ignored:
- `.align`
- `.data <addr>` or `.text <addr>` the address is ignored

The following SPIM directives are not supported and will not assemble:
- `.rdata`, `.sdata`, `.kdata`
- `.float`
- `.double`
- `.extern`
- `.ktext`
- `.set`

