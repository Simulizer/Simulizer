.data
.align 2

.text 
.globl main

main: # Author: Charlie Street
	li $v0, 17;
	li $t6, 18;
	seq $t7, $v0, $t6;
	sne $t8, $v0, $t7;
	move $v0, $t7;
	move $v0, $t8;
	move $a0, $t6;
	syscall;
	li $v0, 10;
	syscall;