	# compiled using GCC with optimisation level s
	# @{ function fibJs(x) {              }@
	# @{  if(x <= 1) return x;            }@
	# @{  return fibJs(x-1) + fibJs(x-2); }@
	# @{ }                                }@
	.text
fibRec:
	addiu	$sp,$sp,-40
	sw	$ra,36($sp)
	sw	$17,32($sp)
	sw	$16,28($sp)
	move	$17,$0
	move	$16,$4
LBL_3:
	slti $2,$16,2
	bne	$2,$0,LBL_2
	addiu	$4,$16,-1
	jal	fibRec
	addiu	$16,$16,-2
	addu	$17,$17,$2
	b	LBL_3
LBL_2:
	lw	$ra,36($sp)
	addu	$2,$16,$17
	lw	$17,32($sp)
	lw	$16,28($sp)
	addiu	$sp,$sp,40
	j	$ra
fibIt:
	slti $3,$4,2
	bne	$3,$0,LBL_11
	li	$3,2			# 0x2
	li	$2,1			# 0x1
	move	$5,$0
LBL_8:
	addu	$6,$5,$2
	addiu	$3,$3,1
	move	$5,$2
	move	$2,$6
	slt	$6,$4,$3
	beq	$6,$0,LBL_8
	j	$ra
LBL_11:
	move	$2,$4
	j	$ra
	.text
main:
	addiu	$sp,$sp,-32
	sw	$ra,28($sp)
	# get input
	la	$3,input
	li	$v0, 4 # print string
	move	$a0, $3
	syscall
	li	$v0, 5 # read int
	syscall
	move	$7, $v0
	la	$2,nl
	lb	$8,0($2)
	# <hazard>
	li	$v0, 11 # print char
	move	$a0, $8
	syscall
	# recursive implementation
	la	$3,rec
	li	$v0, 4 # print string
	move	$a0, $3
	syscall
	li	$v0, 1 # print int
	move	$a0, $7
	syscall
	la	$9,eq
	li	$v0, 4 # print string
	move	$a0, $9
	syscall
	move	$4,$7
	jal	fibRec
	move	$3,$2
	li	$v0, 1 # print int
	move	$a0, $3
	syscall
	li	$v0, 11 # print char
	move	$a0, $8
	syscall
	# iterative implementation
	la	$3,it
	li	$v0, 4 # print string
	move	$a0, $3
	syscall
	li	$v0, 1 # print int
	move	$a0, $7
	syscall
	li	$v0, 4 # print string
	move	$a0, $9
	syscall
	move	$4,$7
	jal	fibIt
	move	$3,$2
	li	$v0, 1 # print int
	move	$a0, $3
	syscall
	li	$v0, 11 # print char
	move	$a0, $8
	syscall
	# javascript implementation
	la	$3,js
	li	$v0, 4 # print string
	move	$a0, $3
	syscall
	li	$v0, 1 # print int
	move	$a0, $7
	syscall
	li	$v0, 4 # print string
	move	$a0, $9
	syscall
	nop # @{ var js_x = $7.get(); }@
	nop # @{ $3.set(fibJs(js_x)); }@
	li	$v0, 1 # print int
	move	$a0, $3
	syscall
	li	$v0, 11 # print char
	move	$a0, $8
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
eq:
	.ascii	" = \0"
js:
	.ascii	"javascript fibonacci of \0"
it:
	.ascii	"iterative fibonacci of \0"
rec:
	.ascii	"recursive fibonacci of \0"
input:
	.ascii	"enter value: \0"
