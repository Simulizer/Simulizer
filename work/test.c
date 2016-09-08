
/*
executes:
li $v0 1
move $a0 (variable from c)
syscall

asm statement signature:
asm(code, output operands, input operands, clobbered registers);

Operands are denoted %n
operands may have constraints placed on them
see: http://www.ibiblio.org/gferg/ldp/GCC-Inline-Assembly-HOWTO.html#ss6.1

"=" => _write_ only (old value discarded)
"r" => compiler can place the operand in any register it wants
"m" => force operations to be flushed to memory rather than just to the register
"g" => not fussy about how operand is passed
*/

#define EXIT()             \
    asm ("li\t$v0, 10\n\t" \
         "syscall"         \
         :                 \
         :                 \
         : "$v0"           \
         )

#define PRINT_INT(i)                \
    asm ("li\t$v0, 1\n\t"           \
        "move\t$a0, %0\n\t"         \
        "syscall"                   \
        : /*output operands*/       \
        : "g"(i)/*input operands*/  \
        : "$v0", "$a0" /*clobbers*/ \
        )

// use of static stops .globl directives from being generated

// const  => put in .rdata section
// char*  => string placed in .rdata with pointer in .data
// char[] => string and variable placed in .data
char input[] = "Enter the number of items:a";
int globalA = 5;


// this disables name mangling. Otherwise called: _Z6addTwoii or similar
int addTwo(short,char) asm("addTwo");

int addTwo(short a, char b) {
    return a + b;
}


// main does not have its name mangled
int main() {
    int b = 14;
    b = addTwo(b, globalA);
    PRINT_INT(b);

    EXIT(); // otherwise attempts to jr $ra where $ra == 0
}

