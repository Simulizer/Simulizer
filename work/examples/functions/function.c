
// .data segment


#include "libc-simulizer.h"

// .text segment

int addArgs(int a, int b, int c) {
    return a + b + c;
}



int main() {
    int res = addArgs(1, 2, 3);

    PRINT_INT(res);

    EXIT();
}
