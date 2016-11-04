
// .data segment
char prompt[] = "Enter a number: ";
char resultRec[] = "n! (recursive) = ";
char resultIt[]  = "n! (iterative) = ";
char nl = '\n';


#include "libc-simulizer.h"

// .text segment


int factorialRec(int n) {
    if(n <= 1) return 1;
    return n * factorialRec(n-1);
}

int factorialIt(int n) {
    int acc = 1;

    if(n > 0) {
        for(int i = 1; i <= n; ++i)
            acc *= i;
    }

    return acc;
}


int main() {
    C("get input");
    PRINT_STRING(prompt);
    int n;
    READ_INT(n);

    C("calculate results");
    int resRec = factorialRec(n);
    int resIt = factorialIt(n);

    C("print recursive result");
    PRINT_STRING(resultRec);
    PRINT_INT(resRec);
    PRINT_CHAR(nl);

    C("print iterative result");
    PRINT_STRING(resultIt);
    PRINT_INT(resIt);
    PRINT_CHAR(nl);

    EXIT();
}
