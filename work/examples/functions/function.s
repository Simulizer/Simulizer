	# compiled using GCC with optimisation level 0
	.text
addArgs:
	addiu	$sp,$sp,-8
	sw	$fp,4($sp)
	move	$fp,$sp
	sw	$4,8($fp)
	sw	$5,12($fp)
	sw	$6,16($fp)
	lw	$3,8($fp)
	lw	$2,12($fp)
	# <hazard>
	addu	$3,$3,$2
	lw	$2,16($fp)
	# <hazard>
	addu	$2,$3,$2
	move	$sp,$fp
	lw	$fp,4($sp)
	addiu	$sp,$sp,8
	j	$ra
main:
	addiu	$sp,$sp,-40
	sw	$ra,36($sp)
	sw	$fp,32($sp)
	move	$fp,$sp
	li	$6,3			# 0x3
	li	$5,2			# 0x2
	li	$4,1			# 0x1
	jal	addArgs
	sw	$2,24($fp)
	lw	$3,24($fp)
	# <hazard>
	li	$v0, 1 # print int
	move	$a0, $3
	syscall
	li	$v0, 10 # exit
	syscall
	move	$2,$0
	move	$sp,$fp
	lw	$ra,36($sp)
	lw	$fp,32($sp)
	addiu	$sp,$sp,40
	j	$ra
