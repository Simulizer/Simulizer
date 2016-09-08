/*
    Toy libc for Simulizer.

    Provides an interface for interacting with Simulizer's hardware

    Author: mbway (Matthew Broadway <matthewbroadway@msn.com>)

*/


/* note on the GCC asm statement

asm statement signature:
asm(code, output operands, input operands, clobbered registers);

Operands are denoted %n
operands may have constraints placed on them
see: http://www.ibiblio.org/gferg/ldp/GCC-Inline-Assembly-HOWTO.html#ss6.1

"=" => _write_ only (old value discarded)
"r" => compiler can place the operand in any register it wants
"p" => pass as a register containing an address
*/


// comment to be written to the asm output
// eg C("doing thing..."); ---> # doing thing...
#define C(x) asm("# " x)

// annotation
// eg ANN("h.move(a, b);") ---> # @{ h.move(a, b); }@
#define ANN(x) asm("# @{ " x " }@")

// syscall 1
#define PRINT_INT(i)                \
    asm ("li\t$v0, 1 # print int\n\t" \
         "move\t$a0, %0\n\t"        \
         "syscall"                  \
        :       /*output operands*/ \
        : "r"(i) /*input operands*/ \
        : "$v0", "$a0" /*clobbers*/ \
        )

// syscall 4
#define PRINT_STRING(s)             \
    asm ("li\t$v0, 4 # print string\n\t" \
         "move\t$a0, %0\n\t"        \
         "syscall"                  \
        :       /*output operands*/ \
        : "p"(s) /*input operands*/ \
        : "$v0", "$a0" /*clobbers*/ \
        )

// syscall 5
#define READ_INT(i)                  \
    asm ("li\t$v0, 5 # read int\n\t" \
         "syscall\n\t"               \
         "move\t%0, $v0"             \
        : "=r"(i)/*output operands*/ \
        :         /*input operands*/ \
        : "$v0"         /*clobbers*/ \
        )

// syscall 8
#define READ_STRING(buf, len)                  \
    asm ("li\t$v0, 8 # read string\n\t"        \
         "move\t$a0, %0\n\t"                   \
         "move\t$a1, %1\n\t"                   \
         "syscall"                             \
        :                  /*output operands*/ \
        : "p"(buf), "r"(len)/*input operands*/ \
        : "$v0", "$a0", "$a1"     /*clobbers*/ \
        )

// syscall 9
// res stores the address of the start of the newly allocated block
#define SBRK(num, res)                 \
    asm ("li\t$v0, 9 # sbrk\n\t"       \
         "move\t$a0, %1\n\t"           \
         "syscall\n\t"                 \
         "move\t%0, $v0"               \
        : "=r"(res)/*output operands*/ \
        : "r"(num)  /*input operands*/ \
        : "$v0", "$a0"    /*clobbers*/ \
        )

// syscall 10
#define EXIT()                \
    asm ("li\t$v0, 10 # exit\n\t" \
         "syscall"            \
         :                    \
         :                    \
         : "$v0" /*clobbers*/ \
         )

// syscall 11
#define PRINT_CHAR(c)               \
    asm ("li\t$v0, 11 # print char\n\t" \
         "move\t$a0, %0\n\t"        \
         "syscall"                  \
        :       /*output operands*/ \
        : "r"(c) /*input operands*/ \
        : "$v0", "$a0" /*clobbers*/ \
        )

// syscall 12
#define READ_CHAR(c)                 \
    asm ("li\t$v0, 12 # read char\n\t" \
         "syscall\n\t"               \
         "move\t%0, $v0"             \
        : "=r"(c)/*output operands*/ \
        :         /*input operands*/ \
        : "$v0"         /*clobbers*/ \
        )

#if 0




// Malloc


// by default Simulizer allocates a maximum of 1MiB to the heap
#define MEM_SIZE (1024*1024)

struct metadata {
    unsigned int size;
    unsigned int available;
    struct metadata *next_block;
};

void *heap_start=NULL;
void *heap_last_block=NULL;


unsigned int align_size(unsigned int) asm();
unsigned int align_size()

#endif

