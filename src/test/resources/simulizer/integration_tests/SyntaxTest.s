.data
.align 2

.text
.globl main

main: #Author: Charlie Street
	add $t0, $t1, $t2;
	subi $a0, $a1, 5;
	li $v0, 10;
	syscall; #@{log('hi')}@
	