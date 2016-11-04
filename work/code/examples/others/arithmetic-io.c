
// .data segment

char nl         = '\n';
char plus[]     = "a + b = ";
char multiply[] = "a * b = ";
char nested[]   = "c * (a + b) = ";


#include "../libc-simulizer.h"

// .text segment

int main() {
    int a = 13;
    int b = 20;
    int c;

    C("test addition");

    c = a + b;
    PRINT_STRING(plus);
    PRINT_INT(c);
    PRINT_CHAR(nl);


    C("test multiplication");

    c = a * b;
    PRINT_STRING(multiply);
    PRINT_INT(c);
    PRINT_CHAR(nl);


    C("test nesting with brackets");

    c = 14;
    int d = c * (a + b);
    PRINT_STRING(nested);
    PRINT_INT(d);
    PRINT_CHAR(nl);

    EXIT();
}
