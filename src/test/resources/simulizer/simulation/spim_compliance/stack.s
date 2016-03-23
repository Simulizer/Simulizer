.text
main:

li $t0 0xDE
li $t1 0xAD
li $t2 0xBE
li $t3 0xEF

addi $sp -4

sb $t0 ($sp)

li $v0 10
syscall