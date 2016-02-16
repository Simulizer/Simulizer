# Assembled Program Structure #
Because we are not writing a fully fledged assembler, the assembled program can
contain more information that makes it easier for the simulator to execute.

## Structure ##
Misc elements
- A hash to determine whether the source has been modified. This means that when
  the user clicks run, the program only has to be assembled if it has changed.

## Memory Layout ##
```
0xffffffff
    no access
0x7ffffffc  -   bottom of the stack
    no direct access
changeable  - top of the stack

    no access (no-mans land)

changeable  - break address (one byte past the dynamic data segment)
    allowed access
0x10040000  - initial break address (start of the heap)
    no access
constant    - end of the static data segment
    allowed access
0x10010000  - start of the user controlled part of the static data segment
    no access (used for storing .extern data)
0x10000000  - start of the static data segment
    no access
0x00400000  - start of the text segment
    no access
0x00000000  - Lowest address
```


