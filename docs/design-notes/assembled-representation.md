# Assembled Program Structure #
Because we are not writing a fully fledged assembler, the assembled program can
contain more information that makes it easier for the simulator to execute.

## Structure ##
Misc elements
- A hash to determine whether the source has been modified. This means that when
  the user clicks run, the program only has to be assembled if it has changed.
- An address offset for the program. All addresses in the program which point to
  memory inside the program should be relative to this offset so that the
  program can be placed anywhere in memory.
- The program size in bytes == initial value for the break address relative to
  the main offset of the program, which denotes the end of the heap. Should
  initially point to the first address past the data of the program. (altered by
  the sbrk system call)

# Data Segment #
An offset relative to the beginning of the program should be stored.

The data segment does not hold as many entries as the text segment in general.
Also each entry may be a different size. For these reasons I think that a hash
table mapping addresses to values would be appropriate for this section.

Each value could be an object which consists of its type and an array of bytes
which correspond to the value of the variable.

There should be functionality for stitching these binary values together into a
lump of memory so that memory can be copied from across variable boundaries.

# BSS Segment #


# Text Segment #
An offset relative to the beginning of the program should be stored.

Because each value here is exactly 4 bytes, I think that an array would be more
suitable for this section, because no extra metadata about each instruction is
required.

Each entry in the array could be an instruction object which stores the
instruction to execute (an Enum value?) and 3 operand objects.

An operand object can either be `NotUsed` or `RegisterOperand` or
`AddressOperand`, where `AddressOperand` has more information relating to the
different addressing modes we support.

# Labels and Addressing
Labels should be stored in a hash table with label names as values and relative
offsets as values. These relative offsets are translated into the address of an
instruction or piece of data by the simulator.

As with a real MIPS processor (I think), trying to address an instruction with
an address that is not 4-byte aligned (ie get me only the second byte of an
instruction (4 bytes total)) will fail and raise an exception because we do not
have a byte-accurate representation of an instruction.

