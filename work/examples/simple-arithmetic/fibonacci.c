
// global annotations

asm("# @{ function fibJs(x) {              }@");
asm("# @{  if(x <= 1) return x;            }@");
asm("# @{  return fibJs(x-1) + fibJs(x-2); }@");
asm("# @{ }                                }@");


// .data segment

char input[] = "enter value: ";
char rec[] = "recursive fibonacci of ";
char it[]  = "iterative fibonacci of ";
char js[]  = "javascript fibonacci of ";
char eq[]  = " = ";
char nl = '\n';

#include "../../libc-simulizer.h"

// .text segment


// recursive implementation
int fibRec(int x) {
    if(x <= 1)
        return x;

    return fibRec(x-1) + fibRec(x-2);
}
int fibIt(int x) {
    if(x <= 1)
        return x;

    int f1 = 0;
    int f2 = 1;
    int f;

    for(int i = 2; i <= x; ++i) {
        f = f1 + f2;
        f1 = f2;
        f2 = f;
    }
    return f;
}



int main() {
    C("get input");
    PRINT_STRING(input);
    int x;
    READ_INT(x);
    PRINT_CHAR(nl);

    C("recursive implementation");
    PRINT_STRING(rec);
    PRINT_INT(x);
    PRINT_STRING(eq);
    PRINT_INT(fibRec(x));
    PRINT_CHAR(nl);

    C("iterative implementation");
    PRINT_STRING(it);
    PRINT_INT(x);
    PRINT_STRING(eq);
    PRINT_INT(fibIt(x));
    PRINT_CHAR(nl);

    C("javascript implementation");
    PRINT_STRING(js);
    PRINT_INT(x);
    PRINT_STRING(eq);
    A_READ(x, "var js_x = %0.get();");
    int out;
    A_WRITE(out, "%0.set(fibJs(js_x));");
    PRINT_INT(out);
    PRINT_CHAR(nl);


    EXIT();
}
