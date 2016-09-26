	# compiled using GCC with optimisation level s
	.text
main:
	# assigning to a new local variable
	la	$2,a
	la	$3,b
	lw	$2,0($2)
	lw	$3,0($3)
	# <hazard>
	addu	$5,$2,$3
	nop # @{ print('c = ' + $5.get()); }@
	# assigning to a global variable
	subu	$3,$2,$3
	la	$2,res
	sw	$3,0($2)
	nop # @{ print('res = ' + $3.get()); }@
	# print using assembly IO rather than Javascript annotations
	li	$v0, 1 # print int
	move	$a0, $3
	syscall
	# more than 2 operands
	addiu	$5,$5,33
	addu	$3,$5,$3
	nop # @{ var l = $3.get(); }@
	nop # @{ print('large = ' + l + ' = ' + binString(l) + ' = ' + hexString(l)); }@
	li	$v0, 10 # exit
	syscall
	move	$2,$0
	j	$ra
	.data
multiplier:
	.word	13
b:
	.word	4
a:
	.word	18
res:
	.space	4
