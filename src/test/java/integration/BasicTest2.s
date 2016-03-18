.data
	pass: .asciiz "pass"
	fail: .asciiz "fail"
	
.align 2

.text
.globl main

main: # Author: Charlie Street
	bgez $zero, SUCCESS;
	li $v0, 4;
	la $a0, fail;
	syscall;
SUCCESS: li $v0, 4;
		 la $a0, pass;
		 syscall;
		 li $v0, 10;
		 syscall;