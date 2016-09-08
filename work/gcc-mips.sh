#!/bin/bash

# requires `g++-5-mips-linux-gnu` package

FILE="$1"
# level of optimisation in $2. Default value: 1
OPTIMISATION=${2:-1}
# change extension with bash parameter expansion
OUT="${FILE/%.*/.s}"

# to list flags
# mips-linux-gnu-g++-5 --help=common
# mips-linux-gnu-g++-5 --help=target
# also see: https://gcc.gnu.org/onlinedocs/gcc/MIPS-Options.html
#
# -fverbose-asm: add information to the output including compiler flags used
#

# flags which are closest to the architecture of the Simulizer CPU
mips-linux-gnu-g++-5 -O$OPTIMISATION  -fno-exceptions \
    -mno-explicit-relocs \
    -march=r3000 -meb -mgp32 -mfp32 -msoft-float \
    -S "$FILE" -o "$OUT"
# -O0:           disable optimisations (to make output more readable)
# -fno-exceptions: disabling exceptions removes some cruft added for bookkeeping
# -mno-explicit-relocs: disables use of %hi() and %lo() to load from the .data segment
# -march=r3000:  compile for the R3000 processor (which Simulizer emulates)
# -meb:          big endian (which Simulizer is)
# -mgp32:        32-bit general purpose registers (as opposed to 64-bit)
# -mfp32:        32-bit floating point registers (as opposed to 64-bit).
#                  required for -march=r3000 however Simulizer currently has no FPU
# -msoft-float:  don't use hardware float instructions. Use library calls
#                  instead (because Simulizer doesn't have a FPU)
# -S:            generate assembly output

if [ $? -ne 0 ]; then
    echo "compilation failed"
    exit 1
fi

# remove assembler directives that Simulizer doesn't understand
# .globl is understood, but ignored by Simulizer
# .rdata is not understood, but is useful to keep in so you can see where the
#        read-only data segment is so you can move it to the .data segment
KNOWN_DIRECTIVES="(text|data|rdata|ascii|asciiz|byte|half|word|space)"

# if on a line matching Simulizer-compatible directives: print
# if on a line matching some other directive: skip
# otherwise print
awk -i inplace '/\.'$KNOWN_DIRECTIVES'/{print} /^\s\./{next} {print}' "$OUT"

# replace j $31 with jr $31
#sed --in-place='' 's/j\t\$/jr\t\$/' "$OUT"
