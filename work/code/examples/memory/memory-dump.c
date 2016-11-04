
// .data segment
char *memA;
char *memB;


#define USE_MEMSET
#define USE_MALLOC
#define MIN_ALLOC_SIZE 0 // use sbrk on every call to malloc
#include "libc-simulizer.h"

// .text segment


int main() {
    // will allocate in multiples of the header size (assuming = 8)

    // size = ((5+8-1)/8)+1 = 2 => 2*8 = 16 bytes allocated
    C("allocate a and fill with 0xAB");
    memA = (char*) malloc(5);
    memset(memA, 0xAB, 5);

    // size = ((10+8-1)/8)+1 = 3 => 3*8 = 24 bytes allocated
    C("allocate b and fill with 0xCD");
    memB = (char*) malloc(10);
    memset(memB, 0xCD, 10);


    // the way malloc is implemented: memA should be above memB like this
    // heap start[[H][memA][16-(8+5)=3 bytes space][H][memB][24-(8+10)=6 bytes space]]heap break

    // even though this is a literal, the value will be placed into a register
    // to transfer to Javascript
    A_READ(sizeof(block_header), "var header_size = %0.get();");
    A("log('free list header size = ' + header_size + ' bytes');");

    C("transfer memA and memB to Javascript");
    A_READ(memA, "var memA = %0.get();");
    A_READ(memB, "var memB = %0.get();");

    // should be 16 bytes apart (see above)
    A("log('memA = ' + memA);");
    A("log('memB = ' + memB);");

    A("var data_start  = memA - header_size;");
    A("var data_length = 16 + 24; // see source code for explanation");
    A("var mem = sim.readBytesFromMem(data_start, data_length);");
    A("log('heap memory:');");
    A("log(debug.hex(mem, 8)); // spaces every 8 digits (4 bytes)");

    EXIT();
}
