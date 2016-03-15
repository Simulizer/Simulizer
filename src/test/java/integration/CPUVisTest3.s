.data
.align 2

.text
.globl main

main:
	j MYLABEL;
	
MYLABEL: li $v0, 10;
		 syscall;