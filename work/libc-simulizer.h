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
// res stores the address of the start of the newly allocated block (the old break)
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



// always defined
#define NULL 0
#define size_t unsigned int



// .data segment


#ifndef NO_MALLOC

// by default Simulizer allocates a maximum of 1MiB to the heap
#define HEAP_SIZE (1024*1024)
// CPU can more efficiently work on data that is aligned to 2^n where n depends
// on the CPU in question. int may only has to be 4-aligned but long may have to
// be 8-aligned to be efficient.
// In Simulizer, this isn't really a concern so only ensure that data is
// word-aligned (4 bytes)
#define MOST_RESTRICTIVE_TYPE int
// allocate at least this amount when calling sbrk
// a real implementation would allocate something like 1024 bytes at a time
#define MIN_ALLOC_SIZE 64


// to simplify memory management:
// - blocks are multiples of the header size
// - header is aligned to largest data type (word)
typedef union block_header {
    struct {
        block_header *next;
        size_t size;        // in units of sizeof(block_header)
    } d; // header data
    MOST_RESTRICTIVE_TYPE align_padding; // make sure all allocations are aligned
} block_header;

block_header malloc_first_block = {0};
block_header *malloc_free_list = NULL;

#endif






// .text segment




#ifndef NO_UTILS

void memset(void *ptr, unsigned char value, size_t num) {
    unsigned char *ptrc = (unsigned char*)ptr;
    unsigned char *end = ptrc + num;
    while(ptrc != end)
        *ptrc++ = value;
}


#endif



#ifndef NO_MALLOC

// malloc / free implementation based on the K&R implementation
// also referenced: https://www.cs.princeton.edu/courses/archive/fall06/cos217/lectures/14Memory-2x2.pdf



void *malloc(size_t);
void free(void*);
block_header *request_mem(size_t);


// request an allocation of arbitrary size
void *malloc(size_t size) {
    // number of memory units (blocks, the size of the header) needed to hold
    // 'size' bytes of user data.
    // allocate_size/block_size rounds down so is one less block than required,
    // except when size is already a multiple of the block size.
    // (allocate_size-1)/block_size is always one less block than is needed
    // so ((allocate_size-1)/block_size)+1 is the number of units needed
    // note if size = 0 then num_units = 1
    size_t num_units = ((size + sizeof(block_header) - 1) / sizeof(block_header)) + 1;

    // setup the circular list of free blocks, using the statically allocated
    // first_block as a starting point
    if(malloc_free_list == NULL) {
        malloc_first_block.d.next = &malloc_first_block;
        malloc_first_block.d.size = 0;

        malloc_free_list = &malloc_first_block;
    }

    block_header *prev_block = malloc_free_list;
    block_header *cur_block  = prev_block->d.next;

    // first attempt to find a consecutive block in the free list large enough
    // to accommodate the requested allocation, before requesting more memory
    // from the heap
    for(; ; prev_block = cur_block,
            cur_block  = cur_block->d.next) {

        // found a block in the free list large enough
        if(cur_block->d.size >= num_units) {

            if(cur_block->d.size == num_units) {
                // exactly right size: just remove from free list and return it
                // to the user

                // wouldn't work if only had 1 block on the free list but since
                // the first_block has size 0 we never get to this point
                // since 1 is the minimum value for num_units
                prev_block->d.next = cur_block->d.next;
            } else {
                // too big, split into 2 blocks. Make the second block just the
                // right size and return it to the user
                cur_block->d.size -= num_units;

                // this block is not part of the free list and does not have it's
                // next pointer set
                cur_block += cur_block->d.size; // location for new block header
                cur_block->d.size = num_units; // interpret the new location as a header
            }

            malloc_free_list = prev_block;

            // return memory just past header (+1*sizeof(header))
            return (void*) (cur_block + 1);
        }

        // have wrapped around the entire free list
        if(cur_block == malloc_free_list) {
            cur_block = request_mem(num_units);

            // out of memory
            // don't handle this case because Simulizer throws an exception
            // when out of memory
            //if(cur_block == NULL) return NULL;
        }
        // continue the for loop onto the newly allocated block the newly
        // allocated block may be larger than needed so treat like any other
        // block, cannot just assume its the correct size and handle with a
        // special case
    }
}

// allocate at least num_units*sizeof(header) bytes and add the new block to the
// free list, returning the block immediately preceding the new block
block_header *request_mem(size_t num_units) {

    if(num_units < MIN_ALLOC_SIZE) num_units = MIN_ALLOC_SIZE;

    void *old_break;
    int alloc_size = num_units * sizeof(block_header);
    SBRK(alloc_size, old_break);
    // sbrk throws an exception when out of memory so no need to check for failure
    // on POSIX sbrk() returns -1 on error
    // if(new_break == (void*)-1) return NULL;


    // set the block to occupy the whole of the newly allocated space.
    // malloc will then take a chunk for the requested allocation
    block_header *new_block = (block_header*) old_break;
    new_block->d.size = num_units;

    // add the new block to the free list so malloc can use it
    // +1 to move past the header
    free((void*)(new_block+1));

    // free_list is re-assigned by free, so passing back to malloc
    return malloc_free_list;
}

// add a block to the free list
// the given pointer must lie at the start of a block!
void free(void *ptr) {
    // block header, given a pointer to the beginning of the user-data of the block
    // block to insert
    block_header *ins_block = ((block_header*) ptr) - 1;

    block_header *cur_block = malloc_free_list;

    // find place in the free list to insert the block
    // 3 cases: block is far right, far left or in the middle somewhere
    for(; ; cur_block = cur_block->d.next) {

        // block to insert is the far left or far right
        // if(cur_block is the last block &&
        //      (ins_block is past the end || ins_block is before the beginning)
        if(cur_block >= cur_block->d.next &&
                (cur_block < ins_block || ins_block < cur_block->d.next))
            break;

        // block to insert lies between cur_block and the next block
        if(cur_block < ins_block && ins_block < cur_block->d.next)
            break;
    }

    // cur_block is now the block that ins_block should be placed after in the list

    // if the end of ins_block borders a block which is in the free list, expand
    // ins_block to consume that block as well
    if(ins_block + ins_block->d.size == cur_block->d.next) {
        ins_block->d.size += cur_block->d.next->d.size;
        ins_block->d.next  = cur_block->d.next->d.next;
    } else {
        // the end of ins_block is not bordering a block in the free list
        // so have it point to the next block after it in the free list
        ins_block->d.next = cur_block->d.next;
    }

    // if the end of cur_block borders ins_block then expand cur_block to
    // consume ins_block
    if(cur_block + cur_block->d.size == ins_block) {
        cur_block->d.size += ins_block->d.size;
        cur_block->d.next  = ins_block->d.next;
    } else {
        // cur_block is not adjacent to ins_block so just point to it
        cur_block->d.next = ins_block;
    }


    // good choice of starting point for malloc since it starts searching at
    // free_list->next. Also good choice if called by request_mem.
    malloc_free_list = cur_block;
}


#endif

