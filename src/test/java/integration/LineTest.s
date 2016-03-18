.data 
.align 2

.text
.globl main

main: # Author: Charlie Street
	li $v0, 1;
	j NEWLABEL;
	li $a0, 6;
	add $a0, $a0, $a0;
	
NEWLABEL: li $v0, 10;
		  syscall;
		  li $a0, 10;