	# compiled using GCC with optimisation level s
	.text
addArgs:
	addu	$2,$4,$5
	addu	$2,$2,$6
	j	$ra
	.text
main:
	li	$3,6			# 0x6
	li	$v0, 1 # print int
	move	$a0, $3
	syscall
	li	$v0, 10 # exit
	syscall
	move	$2,$0
	j	$ra
