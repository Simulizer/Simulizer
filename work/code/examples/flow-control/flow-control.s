	# compiled using GCC with optimisation level 0
	.text
example_if:
	addiu	$sp,$sp,-24
	sw	$fp,20($sp)
	move	$fp,$sp
	li	$2,10			# 0xa
	sw	$2,8($fp)
	li	$2,14			# 0xe
	sw	$2,12($fp)
	# --- Example 1 ---
	lw	$3,8($fp)
	lw	$2,12($fp)
	# <hazard>
	slt	$2,$2,$3
	beq	$2,$0,LBL_2
	nop # @{ log('a>b') }@
LBL_2:
	# --- Example 2 ---
	lw	$3,8($fp)
	lw	$2,12($fp)
	# <hazard>
	slt	$2,$3,$2
	beq	$2,$0,LBL_3
	nop # @{ log('a<b') }@
	b	LBL_4
LBL_3:
	nop # @{ log('a>=b') }@
LBL_4:
	# --- Example 3 ---
	lw	$3,8($fp)
	lw	$2,12($fp)
	# <hazard>
	slt	$2,$2,$3
	bne	$2,$0,LBL_5
	nop # @{ log('a<=b') }@
	b	LBL_8
LBL_5:
	lw	$3,8($fp)
	lw	$2,12($fp)
	# <hazard>
	slt	$2,$3,$2
	bne	$2,$0,LBL_7
	nop # @{ log('a>=b') }@
	b	LBL_8
LBL_7:
	nop # @{ log('a==b') }@
LBL_8:
	nop
	move	$sp,$fp
	lw	$fp,20($sp)
	addiu	$sp,$sp,24
	j	$ra
example_while:
	addiu	$sp,$sp,-24
	sw	$fp,20($sp)
	move	$fp,$sp
	li	$2,1			# 0x1
	sb	$2,8($fp)
	# --- Example 1 ---
LBL_11:
	lbu	$2,8($fp)
	# <hazard>
	beq	$2,$0,LBL_10
	# loop body
	sb	$0,8($fp)
	b	LBL_11
LBL_10:
	# --- Example 2 ---
	li	$2,1			# 0x1
	sb	$2,8($fp)
LBL_13:
	lbu	$2,8($fp)
	# <hazard>
	beq	$2,$0,LBL_14
	# loop body
	sb	$0,8($fp)
	# nested loop
	nop
	b	LBL_13
LBL_14:
	nop
	move	$sp,$fp
	lw	$fp,20($sp)
	addiu	$sp,$sp,24
	j	$ra
example_for:
	addiu	$sp,$sp,-24
	sw	$fp,20($sp)
	move	$fp,$sp
	sw	$0,8($fp)
LBL_17:
	lw	$2,8($fp)
	# <hazard>
	slti $2,$2,10
	beq	$2,$0,LBL_18
	lw	$2,8($fp)
	# <hazard>
	nop # @{ log('i = ' + $2.get()); }@
	lw	$2,8($fp)
	# <hazard>
	addiu	$2,$2,1
	sw	$2,8($fp)
	b	LBL_17
LBL_18:
	nop
	move	$sp,$fp
	lw	$fp,20($sp)
	addiu	$sp,$sp,24
	j	$ra
main:
	addiu	$sp,$sp,-32
	sw	$ra,28($sp)
	sw	$fp,24($sp)
	move	$fp,$sp
	jal	example_if
	jal	example_while
	jal	example_for
	li	$v0, 10 # exit
	syscall
	move	$2,$0
	move	$sp,$fp
	lw	$ra,28($sp)
	lw	$fp,24($sp)
	addiu	$sp,$sp,32
	j	$ra
