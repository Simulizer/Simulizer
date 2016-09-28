
// .data segment


#include "libc-simulizer.h"

// .text segment

void example_if() {
    int a = 10;
    int b = 14;

    C("--- Example 1 ---");
    if(a > b) {
        A("log('a>b')");
    }

    C("--- Example 2 ---");
    if(a < b) {
        A("log('a<b')");
    } else {
        A("log('a>=b')");
    }

    C("--- Example 3 ---");
    if(a <= b) {
        A("log('a<=b')");
    } else if(a >= b){
        A("log('a>=b')");
    } else {
        A("log('a==b')");
    }
}

void example_while() {
    bool running = true;

    C("--- Example 1 ---");
    while(running) {
        C("loop body");
        running = false;
    }

    C("--- Example 2 ---");
    running = true;
    while(running) {
        C("loop body");
        running = false;

        while(true) {
            C("nested loop");
            break;
        }
    }
}

void example_for() {
    for(int i = 0; i < 10; ++i) {
        A_READ(i, "log('i = ' + %0.get());");
    }
}


int main() {
    example_if();
    example_while();
    example_for();
    EXIT();
}
