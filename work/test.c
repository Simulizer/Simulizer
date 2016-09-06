
// use of static stops .globl directives from being generated

// const  => put in .rdata section
// char*  => string placed in .rdata with pointer in .data
// char[] => string and variable placed in .data
char input[] = "Enter the number of items:";
char globalA = 5;


// this disables name mangling. Otherwise called: _Z6addTwoii
int addTwo(short,char) asm("addTwo");

int addTwo(short a, char b) {
    return a + b;
}


// main does not have its name mangled
int main() {
    int b = 14;
    addTwo(b, globalA);
}

