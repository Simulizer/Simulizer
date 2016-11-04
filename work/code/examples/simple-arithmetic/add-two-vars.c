
// .data segment

int res;
int a = 18;
int b = 4;
int multiplier = 13;


#include "libc-simulizer.h"

// .text segment


int main() {

    C("assigning to a new local variable");
    int c = a + b;
    // %0 is replaced with the appropriate register by the compiler
    A_READ(c, "print('c = ' + %0.get());");


    C("assigning to a global variable");
    res = a - b;
    A_READ(res, "print('res = ' + %0.get());");


    C("print using assembly IO rather than Javascript annotations");
    PRINT_INT(res);


    C("more than 2 operands");
    int large = a + b + 33 + res;
    A_READ(large, "var l = %0.get();");
    A("print('large = ' + l + ' = ' + binString(l) + ' = ' + hexString(l));");


    EXIT();
}
