#!/usr/bin/env bash

# usage: gcc-mips.sh "some_file.c" X
# where X is an (optional) optimisation level eg "2" => -O2
# compiler will output to "some_file.s"
#
# requires `g++-5-mips-linux-gnu` package


FILE="$1"

# level of optimisation in $2. Default value given after :-
OPTIMISATION=${2:-0}
export OPTIMISATION

# change extension with bash parameter expansion
OUT="${FILE/%.*/.s}"
export OUT

# whether to alter the compiler output to make it work with Simulizer
FILTER=true

# to list flags
# mips-linux-gnu-g++-5 --help=common
# mips-linux-gnu-g++-5 --help=target
# also see: https://gcc.gnu.org/onlinedocs/gcc/MIPS-Options.html
#
# -fverbose-asm: add information to the output including compiler flags used
#


function compile {
    # flags which are closest to the architecture of the Simulizer CPU
    mips-linux-gnu-g++-5 -O$OPTIMISATION -Wall -Wpedantic -std=c++14 \
        -fno-exceptions -mno-explicit-relocs \
        -march=r3000 -meb -mgp32 -mfp32 -msoft-float \
        -mno-llsc -fno-stack-protector -fno-delayed-branch \
        -I./ -I"$(dirname "$OUT")" -S "$1" -o "$OUT"
    # -O0:           disable optimisations (to make output more readable)
    # -fno-exceptions: disabling exceptions removes some cruft added for bookkeeping
    # -mno-explicit-relocs: disables use of %hi() and %lo() to load from the
    #                   .data segment
    # -march=r3000:  compile for the R3000 processor (which Simulizer emulates)
    # -meb:          big endian (which Simulizer is)
    # -mgp32:        32-bit general purpose registers (as opposed to 64-bit)
    # -mfp32:        32-bit floating point registers (as opposed to 64-bit).
    #                   required for -march=r3000 however Simulizer currently
    #                   has no FPU
    # -msoft-float:  don't use hardware float instructions. Use library calls
    #                   instead (because Simulizer doesn't have a FPU)
    # -mno-llsc:     don't use ll,sc and sync instructions (atomic instructions)
    #                   because Simulizer does not support them
    # -mno-stack-protector: don't write stack canaries or other protections to
    #                   the stack
    # -fno-delayed-branch: don't exploit delayed branch slots because Simulizer
    #                   does not have them
    # -I./           include the current directory in the include path to search
    #                   for headers
    # -I$(...)       include the path that the input and output files reside in
    # -S:            generate assembly output
}


# by wrapping everything in extern "C" disables name mangling for all functions
TMP_FILE=$(mktemp /tmp/gcc-mips.tmp.XXXXX.c)
echo 'extern "C" {' > "$TMP_FILE"
cat "$FILE" >> "$TMP_FILE"
echo '}' >> "$TMP_FILE"


# the line numbers will be wrong but if piped to /dev/null you loose warning messages
compile "$TMP_FILE"

COMPILE_STATUS=$?

rm "$TMP_FILE"

if [ $COMPILE_STATUS -ne 0 ]; then
    RED='\033[1;31m'
    NO_COLOR='\033[0m'
    echo -e "\n\n${RED}Compilation Failed${NO_COLOR}\n"
    # compile the original file (will mangle function names)
    # to get better diagnostics (line numbers)
    compile "$FILE"

    # if it works without extern "C" surrounding it...
    if [ $? -eq 0 ]; then
        echo "something went wrong with the compiler"
    fi

    exit 1
fi


# no filtering
if ! $FILTER; then
    exit 0
fi

HEADER="\t# compiled using GCC with optimisation level $OPTIMISATION\n"
echo -e "$HEADER$(cat "$OUT")" > "$OUT"

# remove assembler directives that Simulizer doesn't understand
# .globl is understood, but ignored by Simulizer
# .align is understood, but ignored by Simulizer
# .rdata is not understood, but is useful to keep in so you can see where the
#        read-only data segment is so you can move it to the .data segment
# note that sometimes GCC places data in the .bss segment which is also not
#   supported by Simulizer
KNOWN_DIRECTIVES="(text|data|rdata|ascii|asciiz|byte|half|word|space)"


# if on a line matching Simulizer-compatible directives: print
# if on a line matching some other directive: skip
# remove #nop lines
# remove #APP and #NO_APP messages which surround asm() statements
# remove # 0 "" 2 and # XX "input.c" 1 lines which surround asm() statements
AWK_FILTER='
/\.'$KNOWN_DIRECTIVES'([^\w.]|$)/{print; next;}
/^\s*\.bss/{$0="\t.data"; print} # replace .bss with .data
/^\s*.section\s*\.text.startup/{$0="\t.text"; print} # .section .text.startup with .text
/^\s*\./{next}  # unknown directives

/^\s*#nop$/{print "\t# <hazard>"; next}
/^\s*#(NO_)?APP$/{next}
/^\s*# 0 "" 2$/{next}
/^\s*# [0-9]+ "'"${TMP_FILE//\//\\/}"'" 1/{next} # need to escape / in file path
/^\s*# [0-9]+ ".*libc-simulizer.h" 1/{next}

{print}
'
# explanations
# ([^\w.]|$)   -> when compiling with -O3 a directive gets generated: .text.startup
#                 so this checks that the next char after a known directive
#                 doesn't extend that directive

awk -i inplace "$AWK_FILTER" "$OUT"


# gcc uses labels of the form: $LXXX eg $L4 (where XXX is a unique number) for
# loops Simulizer does not understand these as they are confusing as they look
# like registers. (Note spim can handle these labels)
# eg $L4 --> LBL_4
sed --in-place='' 's/\(^[^#]*\)\$L\(\d*\)/\1LBL_\2/' "$OUT"

# when optimising, gcc creates labels of the form: functionName.constprop.XXX
# but Simulizer does not support . in label names
sed --in-place='' 's/\(^[^#]*[[:alpha:]]\+\)\./\1_/g' "$OUT"


# substitute mnemonic register names (personal preference)
sed --in-place='' 's/\$31/$ra/' "$OUT"


# gcc uses these macros which simulizer does not understand for a particular
# overloading, for example the 'move' instruction is used to move from memory to
# a register. The following program replaces these instructions with an
# appropriate replacement

AWK_FIX_OVERLOADS='
# match: "address as the second argument"
/^\s*move[^,]*,[^#]*\(/{$1="\tlw"; print $0; next;}

# match "not a register as the third argument"
/^\s*slt([^,]*,){2}[^\$]/{$1="\tslti"; print $0; next;}

# does not support mult which stores in mflo/mfhi so replace with mul $d $d $s
/^\s*mult\s+/{$1="\tmul"; r = gensub(/(\$[^,]*),(.*)/, "\\1,\\1,\\2", "g"); print r; next;}

# just comment out instances of mflo and hope that the above substitution takes
# care of it (should check by hand). Cannot do this with mfhi so just hope it is
# never used
/^\s*mflo/{$1="\t#mflo"; print $0; next;}

{print}
'
awk -i inplace "$AWK_FIX_OVERLOADS" "$OUT"

