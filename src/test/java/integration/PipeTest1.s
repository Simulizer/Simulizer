.data
.align 2

.text
.globl main

main: # Author: Charlie Street
	li $v0, 5;
	j LABEL;
	li $a0, 7;
	li $a1, 6;
LABEL: li $v0, 10;
	   li $t0, 6;
	   syscall;