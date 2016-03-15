.data 
.align 2

.text
.globl main

main:
	li $t3, 8;
	li $v1, 7;
	add $v0, $t3, $v1;
	li $v0, 10;
	syscall;
	