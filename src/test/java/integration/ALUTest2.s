.data
.align 2

.text
.globl main

main:
	j FIRSTLABEL;
	
PASSLABEL: li $a0, 17;
		   li $v0, 1;
		   syscall;
		   li $v0, 10;
		   syscall;
FIRSTLABEL : li $t1, 7;
			 li $t2, 7;
			 beq $t1, $t2, PASSLABEL;
			 move $a0, $t1;
			 li $v0, 1;
			 syscall;
			 li $v0, 10;
			 syscall;