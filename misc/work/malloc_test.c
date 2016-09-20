
// .data segment

char arrow[] = "->";



// .text segment

#include "libc-simulizer.h"

struct list {
    struct list *next;
    int val;
};

struct list *alloc_list_item(int val) {
    struct list *l = (struct list*)malloc(sizeof(list));
    l->val = val;
    return l;
}

void print_list(struct list *l) {
    for(; l != NULL; l = l->next) {
        PRINT_INT(l->val);
        if(l->next != NULL)
            PRINT_STRING(arrow);
    }
    PRINT_CHAR('\n');
}

int main() {

    struct list *a, *b, *c;
    a = alloc_list_item(10);
    b = alloc_list_item(15);
    c = alloc_list_item(20);

    a->next = b;
    b->next = c;

    print_list(a);


    EXIT();
}


