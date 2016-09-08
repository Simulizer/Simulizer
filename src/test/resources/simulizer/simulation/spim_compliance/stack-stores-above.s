.text
main:

li $t0 0x0000DEAD

addi $sp $sp -2

sh $t0 ($sp) # should store above (stores lower half of word

# $sp [0xDE, 0xAD] top

lb $a0 1($sp) # little endian => MSB at highest address so should be 0xDE
              # big endian => LSB at highest address so should be 0xAD

li $v0 1
syscall

li $v0 10
syscall
