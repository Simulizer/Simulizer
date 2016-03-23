.data
.align 2

.text
.globl main

main: # Author: Charlie Street
	li $a1, 19;
	sw $a1, ($sp);
	lw $a0, ($sp);
	li $v0, 1;
	syscall;
	li $v0, 10;
	syscall;