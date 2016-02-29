# @{ log("printing register values as the program runs") }@
# @{ var h = loadVis('tower-of-hanoi') }@
# @{ setSpeed(20) }@

# print the numbers from 1 to 100
.data
	mystr: .asciiz "Done"

.text
main:
	li $s0 0
	li $t0 100

LOOP:
    nop            # @{ log('s0 before = '   + $s0.get()) }@
    addi $s0 $s0 1 # @{ log('s0 after    = ' + $s0.get()) }@

	move $a0 $s0 # @{ h.move(0, 1); h.commit(); stop() }@
	li $v0 1
	syscall # print int

	beq $s0 $t0 END
	j LOOP
END:nop

	li $v0 4
	la $a0 mystr
	syscall # print string

	li $v0 10
	syscall

