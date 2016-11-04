	.text
main:
	la	$3,ina
	li	$v0, 4 # print string
	move	$a0, $3
	syscall
	li	$v0, 5 # read int
	syscall
	move	$3, $v0
	la	$5,inb
	li	$v0, 4 # print string
	move	$a0, $5
	syscall
	li	$v0, 5 # read int
	syscall
	move	$4, $v0
	# test addition
	addu	$2,$3,$4
	nop # @{ print('a + b = ' + $2.get()); }@
	# test multiplication
	mul $3,$3,$4
	#mflo $3
	nop # @{ print('a * b = ' + $3.get()); }@
	# test nesting with brackets
	mul $2,$2,$3
	#mflo $3
	nop # @{ print('c * (a + b) = ' + $3.get()); }@
	li	$v0, 10 # exit
	syscall
	move	$2,$0
	j	$ra
	.data
inb:
	.ascii	"enter a number: b=\000"
ina:
	.ascii	"enter a number: a=\000"
