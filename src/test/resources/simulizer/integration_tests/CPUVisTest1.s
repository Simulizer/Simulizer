.data
.align 2

.text
.globl main

main: # Author: Charlie Street
	add $t0, $t1, $t2;
	li $v0, 10;
	syscall;