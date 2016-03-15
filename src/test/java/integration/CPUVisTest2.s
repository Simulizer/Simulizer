.data
.align 2

.text
.globl main

main:
	beqz $zero, MYLABEL;
	
MYLABEL: li $v0, 10;
		 syscall;