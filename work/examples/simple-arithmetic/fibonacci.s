	# compiled using GCC with optimisation level 0
	# @{ function fibJs(x) {              }@
	# @{  if(x <= 1) return x;            }@
	# @{  return fibJs(x-1) + fibJs(x-2); }@
	# @{ }                                }@
	.data
input:
	.ascii	"enter value: \0"
rec:
	.ascii	"recursive fibonacci of \0"
it:
	.ascii	"iterative fibonacci of \0"
js:
	.ascii	"javascript fibonacci of \0"
eq:
	.ascii	" = \0"
nl:
	.byte	10
	.text
fibRec:
	addiu	$sp,$sp,-40
	sw	$ra,36($sp)
	sw	$fp,32($sp)
	sw	$16,28($sp)
	move	$fp,$sp
	sw	$4,40($fp)
	lw	$2,40($fp)
	# <hazard>
	slti $2,$2,2
	beq	$2,$0,LBL_2
	lw	$2,40($fp)
	b	LBL_3
LBL_2:
	lw	$2,40($fp)
	# <hazard>
	addiu	$2,$2,-1
	move	$4,$2
	jal	fibRec
	move	$16,$2
	lw	$2,40($fp)
	# <hazard>
	addiu	$2,$2,-2
	move	$4,$2
	jal	fibRec
	addu	$2,$16,$2
LBL_3:
	move	$sp,$fp
	lw	$ra,36($sp)
	lw	$fp,32($sp)
	lw	$16,28($sp)
	addiu	$sp,$sp,40
	j	$ra
fibIt:
	addiu	$sp,$sp,-32
	sw	$fp,28($sp)
	move	$fp,$sp
	sw	$4,32($fp)
	lw	$2,32($fp)
	# <hazard>
	slti $2,$2,2
	beq	$2,$0,LBL_5
	lw	$2,32($fp)
	b	LBL_6
LBL_5:
	sw	$0,8($fp)
	li	$2,1			# 0x1
	sw	$2,12($fp)
	li	$2,2			# 0x2
	sw	$2,20($fp)
LBL_8:
	lw	$3,20($fp)
	lw	$2,32($fp)
	# <hazard>
	slt	$2,$2,$3
	bne	$2,$0,LBL_7
	lw	$3,8($fp)
	lw	$2,12($fp)
	# <hazard>
	addu	$2,$3,$2
	sw	$2,16($fp)
	lw	$2,12($fp)
	# <hazard>
	sw	$2,8($fp)
	lw	$2,16($fp)
	# <hazard>
	sw	$2,12($fp)
	lw	$2,20($fp)
	# <hazard>
	addiu	$2,$2,1
	sw	$2,20($fp)
	b	LBL_8
LBL_7:
	lw	$2,16($fp)
LBL_6:
	move	$sp,$fp
	lw	$fp,28($sp)
	addiu	$sp,$sp,32
	j	$ra
main:
	addiu	$sp,$sp,-40
	sw	$ra,36($sp)
	sw	$fp,32($sp)
	move	$fp,$sp
	# get input
	la	$3,input
	li	$v0, 4 # print string
	move	$a0, $3
	syscall
	li	$v0, 5 # read int
	syscall
	move	$3, $v0
	sw	$3,24($fp)
	la	$2,nl
	lb	$3,0($2)
	# <hazard>
	li	$v0, 11 # print char
	move	$a0, $3
	syscall
	# recursive implementation
	la	$3,rec
	li	$v0, 4 # print string
	move	$a0, $3
	syscall
	lw	$3,24($fp)
	# <hazard>
	li	$v0, 1 # print int
	move	$a0, $3
	syscall
	la	$3,eq
	li	$v0, 4 # print string
	move	$a0, $3
	syscall
	lw	$4,24($fp)
	jal	fibRec
	move	$3,$2
	li	$v0, 1 # print int
	move	$a0, $3
	syscall
	la	$2,nl
	lb	$3,0($2)
	# <hazard>
	li	$v0, 11 # print char
	move	$a0, $3
	syscall
	# iterative implementation
	la	$3,it
	li	$v0, 4 # print string
	move	$a0, $3
	syscall
	lw	$3,24($fp)
	# <hazard>
	li	$v0, 1 # print int
	move	$a0, $3
	syscall
	la	$3,eq
	li	$v0, 4 # print string
	move	$a0, $3
	syscall
	lw	$4,24($fp)
	jal	fibIt
	move	$3,$2
	li	$v0, 1 # print int
	move	$a0, $3
	syscall
	la	$2,nl
	lb	$3,0($2)
	# <hazard>
	li	$v0, 11 # print char
	move	$a0, $3
	syscall
	# javascript implementation
	la	$3,js
	li	$v0, 4 # print string
	move	$a0, $3
	syscall
	lw	$3,24($fp)
	# <hazard>
	li	$v0, 1 # print int
	move	$a0, $3
	syscall
	la	$3,eq
	li	$v0, 4 # print string
	move	$a0, $3
	syscall
	lw	$2,24($fp)
	# <hazard>
	nop # @{ var js_x = $2.get(); }@
	nop # @{ $2.set(fibJs(js_x)); }@
	sw	$2,28($fp)
	lw	$3,28($fp)
	# <hazard>
	li	$v0, 1 # print int
	move	$a0, $3
	syscall
	la	$2,nl
	lb	$3,0($2)
	# <hazard>
	li	$v0, 11 # print char
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
