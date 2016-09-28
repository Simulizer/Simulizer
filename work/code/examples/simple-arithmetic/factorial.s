	# compiled using GCC with optimisation level s
	.text
factorialRec:
	li	$2,1			# 0x1
LBL_3:
	slti $3,$4,2
	bne	$3,$0,LBL_2
	mult	$2,$4
	addiu	$4,$4,-1
	mflo	$2
	b	LBL_3
LBL_2:
	j	$ra
factorialIt:
	li	$2,1			# 0x1
	blez	$4,LBL_7
	li	$3,1			# 0x1
LBL_8:
	mult	$2,$3
	addiu	$3,$3,1
	slt	$5,$4,$3
	mflo	$2
	beq	$5,$0,LBL_8
LBL_7:
	j	$ra
	.text
main:
	addiu	$sp,$sp,-32
	sw	$ra,28($sp)
	# get input
	la	$3,prompt
	li	$v0, 4 # print string
	move	$a0, $3
	syscall
	li	$v0, 5 # read int
	syscall
	move	$5, $v0
	# calculate results
	move	$4,$5
	jal	factorialRec
	move	$4,$5
	move	$6,$2
	jal	factorialIt
	move	$5,$2
	# print recursive result
	la	$3,resultRec
	li	$v0, 4 # print string
	move	$a0, $3
	syscall
	li	$v0, 1 # print int
	move	$a0, $6
	syscall
	la	$2,nl
	lb	$3,0($2)
	# <hazard>
	li	$v0, 11 # print char
	move	$a0, $3
	syscall
	# print iterative result
	la	$6,resultIt
	li	$v0, 4 # print string
	move	$a0, $6
	syscall
	li	$v0, 1 # print int
	move	$a0, $5
	syscall
	li	$v0, 11 # print char
	move	$a0, $3
	syscall
	li	$v0, 10 # exit
	syscall
	lw	$ra,28($sp)
	move	$2,$0
	addiu	$sp,$sp,32
	j	$ra
	.data
nl:
	.byte	10
resultIt:
	.ascii	"n! (iterative) = \0"
resultRec:
	.ascii	"n! (recursive) = \0"
prompt:
	.ascii	"Enter a number: \0"
