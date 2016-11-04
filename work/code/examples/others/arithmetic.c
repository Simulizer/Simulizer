
// .data segment

char ina[] = "enter a number: a=";
char inb[] = "enter a number: b=";

#include "../libc-simulizer.h"

// .text segment

int main() {
    int a;
    PRINT_STRING(ina);
    READ_INT(a);

    int b;
    PRINT_STRING(inb);
    READ_INT(b);

    int c;

    C("test addition");

    c = a + b;
    // %0 is converted to the correct register by the compiler
    A_READ(c, "print('a + b = ' + %0.get());");


    C("test multiplication");

    c = a * b;
    A_READ(c, "print('a * b = ' + %0.get());");


    C("test nesting with brackets");

    int d = c * (a + b);
    A_READ(d, "print('c * (a + b) = ' + %0.get());");

    EXIT();
}
