# print the numbers from 1
# to 100... except it doesn't
.data
	mystr: .asciiz "hello there"
.text
main:
	li $s0 0
	li $t0 100

LOOP:   addi $s0 $s0 1

	move $a0 $s0
	li $v0 1
	syscall # print int

	beq $s0 $t0 END # @
	j LOOP
END:	nop

	li $v0 10
	syscall