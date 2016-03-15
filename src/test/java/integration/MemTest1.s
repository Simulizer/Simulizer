.data
	teststr: .asciiz "Passed test"
.align 2

.text
.globl main

main:
	la $a0, teststr;
	li $v0, 4;
	syscall;
	li $v0, 10;
	syscall;