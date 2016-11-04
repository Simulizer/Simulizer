	.text
main:
	# test addition
	la	$3,plus
	li	$v0, 4 # print string
	move	$a0, $3
	syscall
	li	$3,33			# 0x21
	li	$v0, 1 # print int
	move	$a0, $3
	syscall
	la	$2,nl
	lb	$3,0($2)
	# <hazard>
	li	$v0, 11 # print char
	move	$a0, $3
	syscall
	# test multiplication
	la	$5,multiply
	li	$v0, 4 # print string
	move	$a0, $5
	syscall
	li	$5,260			# 0x104
	li	$v0, 1 # print int
	move	$a0, $5
	syscall
	li	$v0, 11 # print char
	move	$a0, $3
	syscall
	# test nesting with brackets
	la	$5,nested
	li	$v0, 4 # print string
	move	$a0, $5
	syscall
	li	$5,462			# 0x1ce
	li	$v0, 1 # print int
	move	$a0, $5
	syscall
	li	$v0, 11 # print char
	move	$a0, $3
	syscall
	li	$v0, 10 # exit
	syscall
	move	$2,$0
	j	$ra
	.data
nested:
	.ascii	"c * (a + b) = \000"
	mul
	.ascii	"a * b = \000"
plus:
	.ascii	"a + b = \000"
nl:
	.byte	10
