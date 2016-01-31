# MIPS #
[Microprocessor without Interlocked Pipeline Stages](https://en.wikipedia.org/wiki/MIPS_instruction_set)

Features of MIPS32:
- Our simulator simulates the MIPS32 [instruction set architecture](#ISA)
- 32 bit architecture
- byte-addressed
- no SIMD
- no multithreading
- every instruction is encoded as 32 bits
- modified Harvard architecture
- data types: `.ascii, .asciiz, .byte, .halfword, .word, .space`
    - `.asciiz` is null terminated (z refers to zero)
    - `.space` is reserved empty space

# Basics #
- Main Memory: RAM
- ALU: Arithmetic Logic Unit
- Von Neumann architecture: a CPU where instructions and data share the same bus
- Harvard Architecture: a CPU where instructions and data have separate busses
- (processor) register: a small amount of storage available as part of the CPU
- instruction register (IR): a register storing the current instruction being
  decoded or executed
- program counter (PC): a register which points to the next instruction in
  memory to execute. It is incremented during the fetch of the instruction.
- data segment: denoted `.data` in MIPS assembly: generic data stored here
- text segment: denoted `.text` in MIPS assembly: instructions stored here
- `.global` makes a label accessible outside the assembly file (used for linking)
- coprocessor: A processor which supplements the CPU, eg FPU, GPU, I/O interface


# Operation / Instruction #
Eg: `add`

An atomic process to be performed on the machine. For our purposes this term
also applies to the mnemonic for a particular machine instruction.


# Operand #
Eg: in `addi $s0, $s1, 42` the first operand `$s0` is the destination operand
and the register `$s1` and `42` are the source operands

A parameter for an operation. Either a register, label, address in main memory
or an [immediate value](#immediate-value). An operand can either be a source or
a destination.




# Immediate Value #
eg in `li $s0, 42` the operand `42` is an immediate value

A constant value which is encoded as part of the instruction.


# Opcode #
eg. In MIPS: the opcode for all R-type operations (eg `add` and `sub`) is
`0x00`. `add` has a function code of `0x20` and `sub` has a function code of `0x22`

[see](http://www.eng.ucy.ac.cy/mmichael/courses/ECE314/LabsNotes/02/MIPS_Instruction_Coding_With_Hex.pdf)

Operation code: A numeric identifier for the 'kind' of operation when assembled
or compiled. Our simulator processes mnemonics directly and so does not deal
with any encoding for representing operations as opcodes.

Note: multiple instructions may share the same opcode. Where this happens a
second identifier called a function code is used to distinguish the different
operations


# Function Code #
eg. In MIPS: `add` has a function code of `0x20`

A numeric identifier for distinguishing between different operations with the
same opcode (see: [opcode](#opcode))



<a id="ISA"></a>
# Instruction Set / Instruction Set Architecture (ISA) #
The parts of a computer's architecture that is exposed through programming it.
Basically exactly the parts which the simulator must comply with.

According to [Wikipedia](https://en.wikipedia.org/wiki/Instruction_set) these
are concepts relating to a computer's ISA:
- RISC / CISC (MIPS is RISC)
- native data types
- instructions
- registers
- addressing modes
- memory architecture
- interrupt handling
- exception handling
- external I/O
- opcodes / machine language


# Microarchitecture #
The functional units which are required to implement an instruction set architecture.

The way that an [instruction set architecture](#ISA) is implemented for a
particular processor. This is basically exactly what needs to be visualised by
our program!

According to [Wikipedia](https://en.wikipedia.org/wiki/Microarchitecture) these
are the concepts relating to a computer's microarchitecture:
- [instruction cycle](#instruction-cycle) (fetch-decode-execute cycle)
- caching and memory hierarchy
- instruction set (already decided on MIPS)
- pipelining
- branch prediction / speculative execution (not used in our implementation)
- superscalar (not used in our implementation)
    - executing multiple operations in parallel in a single tick (not the same as pipelining)
    - eg [SIMD](https://en.wikipedia.org/wiki/SIMD) (Single Instruction Multiple Data operation)
- out-of-order execution (not used in our implementation)
    - when a cache miss occurs, attempt to execute other instructions while waiting
- register naming (already decided on MIPS, which has determined conventions for this)
- multiprocessor and multithreading (not used in our implementation)

# Instruction (Execution) Cycle #
AKA: fetch-decode-execute cycle

1. Fetch instruction from the address pointed to by the program counter (PC). Store
it in the instruction register (IR).
2. Decode the instruction (interpret the instruction in the IR)
3. read the [effective address](#effective-address) from main memory if
necessary, otherwise wait.
4. execute the instruction and repeat


# Instruction Pipelining #
A technique for executing instructions in a production line fashion. Not
executing in parallel, but by prepping several instructions while the current
instruction executes.

Each instruction is divided into steps where each step is executed by a
different part of the processor. Unfortunately this increases the latency for any
single instruction to be executed.

Note: MIPS is 'super-pipelined' meaning each instruction is divided into many
different steps, leading to simpler circuitry.

## Resources ##
- [pipelining](http://www.cim.mcgill.ca/~langer/273/16-pipelining.pdf)
- [classic RISC pipeline](https://en.wikipedia.org/wiki/Classic_RISC_pipeline)

## MIPS pipeline registers ##
hold information needed by instructions at each stage in the pipeline
- `IF/ID` register
- `ID/ALU` register
- `ALU/MEM` register
- `MEM/WB` register

## MIPS pipeline ##
TODO: research what happens at each stage and write separate notes
- `IF`: Instruction fetch
- `ID`: Instruction decode and register fetch
- `ALU`: ALU Execution
- `MEM`: Data memory access
- `WB`: Register write back

## Flushing the Pipeline ##
After a jump, everything in the pipeline is invalidated and so is cleared out.

## Hazards ##
A [hazard](https://en.wikipedia.org/wiki/Hazard_(computer_architecture)) is a
situation in which a pipeline could potentially produce incorrect results.

### Structural Hazards ###
TODO

### Data Hazards ###
TODO

### Control/Branching Hazards ###
TODO


## Solutions (To Hazards) ##
### pipeline stalls / pipeline bubbling ##
TODO

### Operand Forwarding ###
TODO

### Out-of-order execution ###
TODO


# Effective Address #
An operand which references memory. The actual value of the operand may need to
be calculated during the instruction cycle such as when using base addressing.


# Addressing Mode #
an aspect of an [ISA](#ISA). Defines how the [effective
address](#effective-address) of an [operands](#operand) of an instruction may be
calculated.

[see here for a MIPS addressing mode overview](https://www.cs.umd.edu/class/sum2003/cmsc311/Notes/Mips/addr.html)

Examples:
```
lw $s0, Label  # pseudo-direct: load the word from the address 0x12ab
lw $s0, ($s1)  # base(0 offset)/register:
               #    load the word from the address pointed to by s1
lw $s0, 4($s1) # base: load the word 4 bytes past the address pointed to by s1
li $s1, 42     # immediate: load the immediate value 42
```

## Register/Register-Direct/Register-Indirect Addressing ##
used by `jr` (jump register): eg `jr $s0` (jump to the address pointed to by `s0`)

## PC-Relative Addressing ##
used by branching instructions such as `beq $0, $3, Label`. Since the test takes
up space, only 16 bits of the instruction encoding can be used for addressing
the jump.  Therefore the address is interpreted as signed (two's complement) and
relative to the program counter.

Note: Since the program counter is always word (4-bytes) aligned, the offset is
stored as a multiple of 4 to achieve a larger range (of $\pm 128 KB$).

## Pseudo-Direct/Absolute Addressing ##
used by `j Label` or `j 0x1234` (jump). Because 6 bits are used for the opcode,
the remaining 26 bits are not enough to store an immediate absolute address of
anywhere in memory.

So the top 4 bits of the program counter is concatenated with the 26 immediate
address and then shifted left 2 bits (to get a full 32 bit address). This allows
jumps to 1/16 of the possible locations in memory (because the program counter
is not controlled).

## Base/Base-Offset/Base-Displacement/Displacement Addressing ##
used by `lw` (load word). `lw $s0, 4($s1)` an offset and register is specified.
The offset is 16 bits, signed and relative to the address pointed to by the
register.

## Immediate Addressing ##
eg `li $s0, 42`. The operand is embedded inside the encoded instruction.



# Categories of Instructions #
## By Function ##
- Data handling and memory
    - eg `li`
- Arithmetic and Logic
    - eg `add`
- Control Flow
    - eg `beq`
- Complex instructions
    - not found in RISC architectures

## Another By Function ##
- Load / Store
- Arithmetic
- Immediate Arithmetic
- Shift
- Multiply / Divide
- Jump / Branch
- Coprocessor
- Special


## Instruction Format ##
[MIPS Instruction formats](https://www.cs.umd.edu/class/sum2003/cmsc311/Notes/Mips/format.html)

FR-Type and FI-Type instructions are the corresponding floating point instructions.

### R-Type ###
register-type instructions:

\[destination=OP(source_1,\,source_2)\]

`add $d, $s1, $s2  --encoded-as-->  [opcode][s1][s2][d][shift][function]`

The shift bits are only used for shift/rotate instructions.

### I-Type ###
immediate-type instructions:

\[destination=OP(source_1,\,immediate)\]

`addi $d, $s1, 42  --encoded-as-->  [opcode][s1][d][immediate]`

### J-Type ###
jump-type instructions:

\[Jump(target)\]

`j target         --encoded-as-->  [opcode][target]`




# (Assembler) Directive #
eg `.text`

meta-instructions used by the assembler to determine the structure of the
program it is reading.

# Native Instruction #
eg `add`

An instruction that has a direct hardware implementation, as opposed to a
pseudo-instruction which does not.


# Pseudo-Instruction #
An instruction that actually assembles to multiple instructions due to quirks in
the hardware. Eg:

```
bne $s0, $s1
<next instruction>
```

`bne` is a pseudo-instruction because branching creates a '[delay
slot](https://en.wikipedia.org/wiki/Delay_slot)' which is an instruction that
runs regardless of which branch is taken. A sensible thing to do would be to
insert a `nop` in the delay slot. Assemblers usually do this automatically.

```
bne $s0, $s1
nop
<next instruction>
```
