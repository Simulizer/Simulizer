.data
.align 2

.text
.globl main

main: # Author: Charlie Street
	li $a1, 65;#@{log('passed')}@
	li $v0, 10;
	syscall;