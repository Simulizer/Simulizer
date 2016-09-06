#!/bin/bash

# requires `g++-5-mips-linux-gnu` package

FILE="$1"
# change extension with bash parameter expansion
OUT="${FILE/%.*/.s}"

# flags which are closest to the architecture of the Simulizer CPU
# with no optimisation to create 'plain' / readable assembly
mips-linux-gnu-g++-5 -O0 -march=r3000 -meb -mfp32 -mgp32 -S "$FILE" -o "$OUT"

# remove assembler directives that Simulizer doesn't understand
sed --in-place='' '/^\s\./d' "$OUT"

