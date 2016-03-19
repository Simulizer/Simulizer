.data
	teststr: .asciiz "hi there"
.align 2

.text
.globl main

main: # Author: Charlie Street
	la $a0, teststr;
	li $v0, 4;
	syscall;
	li $s0, 1;
	li $t0, 3;
	add $a0, $s0, $t0;
	li $v0, 1;
	syscall;
	li $v0, 10;
	syscall;