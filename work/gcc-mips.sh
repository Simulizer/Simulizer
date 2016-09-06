#!/bin/bash

# requires `g++-5-mips-linux-gnu` package

FILE="$1"
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
mips-linux-gnu-g++-5 -O0 -fverbose-asm -fno-leading-underscore \
    -march=r3000 -meb -mfp32 -msoft-float -mgp32 -S "$FILE" -o "$OUT"
# -O0:           disable optimisations (to make output more readable)
# -fverbose-asm: add information to the output
# -march=r3000:  compile for the R3000 processor (which Simulizer emulates)
# -meb:          big endian (which Simulizer is)
# -mfp32:        32-bit floating point registers (as opposed to 64-bit).
#                  required for -march=r3000 however Simulizer currently has no FPU
# -msoft-float:  don't use hardware float instructions. Use library calls
#                  instead (because Simulizer doesn't have a FPU)
# -mgp32:        32-bit general purpose registers (as opposed to 64-bit)
# -S:            generate assembly output

# remove assembler directives that Simulizer doesn't understand
sed --in-place='' '/^\s\./d' "$OUT"

