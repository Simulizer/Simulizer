.data
.align 2

.text
.globl main

main: # Author: Charlie Street
	li $v0, 5;#should all show up in the register window
	li $a0, 7;
	li $s0, 9;
	li $t4, 1;
	li $v0, 10;#v0 in window should be changed
	syscall;
	