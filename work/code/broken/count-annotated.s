# print the numbers from 1 to 100
.data
	mystr: .asciiz "Done"

.text
main:
	li $s0 0 #
	li $t0 100

LOOP:   addi $s0 $s0 1 # not @ {} an annotation
                       # asdf@

	move $a0 $s0       # comment @{}@ comment
	li $v0 1           # comment@{var x;}@comment
	syscall # print int

	beq $s0 $t0 END
add $a0, $a1           # @{ var test = 15; alert(test); }@
	j LOOP             # comment @{test}@ comment
END:	nop
                       # @{ alert('hello'); }@
	li $v0 4
	la $a0 mystr       # @{ // can't carry onto the next line
	                   # var x = 10; }@ not valid
	syscall # print string
	li $v0 10
	syscall