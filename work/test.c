

// A playground for early testing of libc-simulizer



// .data


// use of static stops .globl directives from being generated

// const  => put in .rdata section
// char*  => string placed in .rdata with pointer in .data
// char[] => string and variable placed in .data
char input[] = "Enter the number of items:";
int globalA = 5;





// .text segment


#define NO_MALLOC
#include "libc-simulizer.h"


// this disables name mangling. Otherwise called: _Z6addTwoii or similar
int addTwo(short,char) asm("addTwo");

int addTwo(short a, char b) {
    return a + b;
}


int addTwoInts(int a, int b) {
    return a + b;
}

// with lots of optimisation (eg -O3) neither of these functions are found in
// the output however a warning is generated for the static function.
// without explicitly defining functions as inline, they will not be inlined because
// of the extern "C" placed around the whole file
static int addTwoIntsStatic(int a, int b) {
    return a + b;
}
inline int addMoreInts(int a, int b) {
    return a + b;
}

// extern "C" also disables name mangling (because it is intended for functions to be
// loaded at runtime as a library function)
extern "C" int doThing(short a, char b) { return b; }

// final solution to the name mangling problem was to wrap the whole input file
// with extern "C" { FILE } at compile time
int doOtherThing(int a, int b) { return a; }

void offsetTest() {
    char a = input[2]; // since in static segment, +2?
    char arr[5];
    int val;
    READ_INT(val);
    memset(input, val, 5);
    for(int i = 0; i < 5; ++i) {
        arr[i] = val+i;
    }
    C("b:");
    char b = arr[2]; // since on stack, -2?

    PRINT_CHAR(a);
    PRINT_CHAR(b);
}

// main does not have its name mangled
int main() {
    offsetTest();



    PRINT_STRING(input);
    int res;
    C("reading int");
    READ_INT(res);

    int b = addTwo(res, globalA);
    PRINT_INT(b);

    ANN("debug.alert('going to read a string!');");
    READ_STRING(input, 5);
    PRINT_STRING(input);


    C("sbrk:");
    char *heapBreak;
    SBRK(8, heapBreak);

    PRINT_INT(heapBreak);
    PRINT_INT(*heapBreak);


    EXIT(); // otherwise attempts to jr $ra where $ra == 0
}

