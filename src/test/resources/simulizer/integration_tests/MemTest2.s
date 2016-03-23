.data
.align 2

.text
.globl main

main: # Author: Charlie Street
	li $a0, 4;
	li $v0, 9;
	syscall;
	li $a0, 7;
	sw $a0, ($v0);
	lw $t2, ($v0);
	li $v0, 1;
	move $a0, $t2;
	syscall;
	li $v0, 10;
	syscall;