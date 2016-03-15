.data
.align 2

.text
.globl main

main:
	li $t0, 5;
	li $t1, 6;
	add $a0, $t0, $t1;
	li $v0, 1;
	syscall;
	li $v0, 10;
	syscall; 