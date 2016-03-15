.data
.align 2

.text
.globl main

main:
	li $v0, 5;
	syscall; #in test, 7 will be entered
	move $a0, $v0;
	li $v0, 1;
	syscall;
	li $v0, 10;
	syscall;