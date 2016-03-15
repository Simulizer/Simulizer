.data
.align 2

.text
.globl main

main:
	li $a1, 65;#@{log('passed')}@
	li $v0, 10;
	syscall;