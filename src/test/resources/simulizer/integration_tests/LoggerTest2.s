.data
	test: .asciiz "This is a test"
	
.align 2

.text
.globl main

main: # Author: Charlie Street
	li $v0, 4;
	la $a0, test;
	syscall;
	li $v0, 10;
	syscall;