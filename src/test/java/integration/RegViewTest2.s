.data
.align 2

.text
.globl main

main:
	li $t0, 1;
	addi $s0, $t0, 5;#so should be 6 in window
	li $v0, 10;
	syscall;