.data
.align 2

.text
.globl main

main: # Author: Charlie Street
	j MYLABEL;
	
MYLABEL: li $v0, 10;
		 syscall;