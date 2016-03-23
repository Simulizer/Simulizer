.text
main:

addi $sp $sp -4

li $t0 0xDEADBEEF

li $v0 1
move $a0 $t0
syscall



li $v0 10
syscall