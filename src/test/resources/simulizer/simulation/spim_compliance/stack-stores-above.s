.text
main:

li $t0 0xDEAD

addi $sp $sp -2

sh $t0 ($sp) # should store above

lb $a0 1($sp) # big endian => LSB at highest address so should be 0xAD

li $v0 1
syscall

li $v0 10
syscall
