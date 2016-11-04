# A program which adds two numbers and prints the result
.data
.text
main:

li $a0, 5
li $t0, 6
add $a0, $a0, $t0
# <hazard>
li $v0, 1 # print int
syscall

li $v0, 10 # exit
syscall
