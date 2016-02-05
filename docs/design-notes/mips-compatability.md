# MIPS Compatibility #
Simulizer aims to simulate a compatible subset of the MIPS I ISA, using the
MIPS32 ISA documentation as reference.

The MicroMIPS32 microarchitecture will be drastically simplified but still
accurate enough to drive a visualisation for teaching purposes.

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

# Added Features #
- configurable clock speed


# Instruction Set #
Functional groups as outlined in the microarchitecture spec:

## Load and Store ##

## Computational ##

## Jump and Branch ##

## Miscellaneous ##


# Addressing Modes #


# Memory #
## Variables ##
- `.data` is the only type of data segment supported
- the entirety of main memory is assumed to be cached and can be fetched in a
  single clock cycle.


## OS ##
The OS does not reside in the simulator's memory (as it is written in Java). Any
program which attempts to access the area of memory reserved for the OS will
crash.

## In-Memory Representation ##
The program will not be held in the main memory of the simulation. Any program
which attempts to access the `.text` segment of the program will crash.

## Stack ##
The stack will appear to be held in main memory but in fact it will not. This
should be transparent to the program.

# Pipeline #
- Simulizer supports a "Four-Deep Single-Completion Pipeline" as described in
  the MIPS32 ISA specification.

# Interupts and Exceptions #
The simulator only supports the following interupts and exceptions:
- System call exception