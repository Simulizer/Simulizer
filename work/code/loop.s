# @{ var i = 0 }@
.text
main:
li $t0 1 # @{ while(true){} }@
loop:
addi $t0 $t0 1 
j loop