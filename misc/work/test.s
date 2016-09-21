	.data
input:
	.ascii	"Enter the number of items:\000"
globalA:
	.word	5
globalB:
	.space	4
	.text
memset:
	addiu	$sp,$sp,-24
	sw	$fp,20($sp)
	move	$fp,$sp
	sw	$4,24($fp)
	move	$2,$5
	sw	$6,32($fp)
	sb	$2,28($fp)
	lw	$2,24($fp)
	# <hazard>
	sw	$2,8($fp)
	lw	$3,8($fp)
	lw	$2,32($fp)
	# <hazard>
	addu	$2,$3,$2
	sw	$2,12($fp)
LBL_3:
	lw	$3,8($fp)
	lw	$2,12($fp)
	# <hazard>
	beq	$3,$2,LBL_4
	lw	$2,8($fp)
	# <hazard>
	addiu	$3,$2,1
	sw	$3,8($fp)
	lbu	$3,28($fp)
	# <hazard>
	sb	$3,0($2)
	b	LBL_3
LBL_4:
	nop
	move	$sp,$fp
	lw	$fp,20($sp)
	addiu	$sp,$sp,24
	j	$ra
addTwo:
	addiu	$sp,$sp,-8
	sw	$fp,4($sp)
	move	$fp,$sp
	move	$3,$4
	move	$2,$5
	sh	$3,8($fp)
	sb	$2,12($fp)
	lh	$3,8($fp)
	lb	$2,12($fp)
	# <hazard>
	addu	$2,$3,$2
	move	$sp,$fp
	lw	$fp,4($sp)
	addiu	$sp,$sp,8
	j	$ra
addTwoInts:
	addiu	$sp,$sp,-8
	sw	$fp,4($sp)
	move	$fp,$sp
	sw	$4,8($fp)
	sw	$5,12($fp)
	lw	$3,8($fp)
	lw	$2,12($fp)
	# <hazard>
	addu	$2,$3,$2
	move	$sp,$fp
	lw	$fp,4($sp)
	addiu	$sp,$sp,8
	j	$ra
addTwoIntsStatic:
	addiu	$sp,$sp,-8
	sw	$fp,4($sp)
	move	$fp,$sp
	sw	$4,8($fp)
	sw	$5,12($fp)
	lw	$3,8($fp)
	lw	$2,12($fp)
	# <hazard>
	addu	$2,$3,$2
	move	$sp,$fp
	lw	$fp,4($sp)
	addiu	$sp,$sp,8
	j	$ra
doThing:
	addiu	$sp,$sp,-8
	sw	$fp,4($sp)
	move	$fp,$sp
	move	$3,$4
	move	$2,$5
	sh	$3,8($fp)
	sb	$2,12($fp)
	lb	$2,12($fp)
	move	$sp,$fp
	lw	$fp,4($sp)
	addiu	$sp,$sp,8
	j	$ra
doOtherThing:
	addiu	$sp,$sp,-8
	sw	$fp,4($sp)
	move	$fp,$sp
	sw	$4,8($fp)
	sw	$5,12($fp)
	lw	$2,8($fp)
	move	$sp,$fp
	lw	$fp,4($sp)
	addiu	$sp,$sp,8
	j	$ra
offsetTest:
	addiu	$sp,$sp,-56
	sw	$ra,52($sp)
	sw	$fp,48($sp)
	move	$fp,$sp
	la	$2,input
	lbu	$2,2($2)
	# <hazard>
	sb	$2,28($fp)
	li	$v0, 5 # read int
	syscall
	move	$3, $v0
	sw	$3,32($fp)
	lw	$2,32($fp)
	# <hazard>
	andi	$2,$2,0xff
	li	$6,5			# 0x5
	move	$5,$2
	la	$4,input
	jal	memset
	sw	$0,24($fp)
LBL_17:
	lw	$2,24($fp)
	# <hazard>
	slti $2,$2,5
	beq	$2,$0,LBL_16
	lw	$2,32($fp)
	# <hazard>
	andi	$3,$2,0x00ff
	lw	$2,24($fp)
	# <hazard>
	andi	$2,$2,0x00ff
	addu	$2,$3,$2
	andi	$2,$2,0x00ff
	sll	$3,$2,24
	sra	$3,$3,24
	lw	$2,24($fp)
	addiu	$4,$fp,24
	addu	$2,$4,$2
	sb	$3,16($2)
	lw	$2,24($fp)
	# <hazard>
	addiu	$2,$2,1
	sw	$2,24($fp)
	b	LBL_17
LBL_16:
	# b:
	lbu	$2,42($fp)
	# <hazard>
	sb	$2,36($fp)
	lbu	$3,28($fp)
	# <hazard>
	li	$v0, 11 # print char
	move	$a0, $3
	syscall
	lbu	$3,36($fp)
	# <hazard>
	li	$v0, 11 # print char
	move	$a0, $3
	syscall
	nop
	move	$sp,$fp
	lw	$ra,52($sp)
	lw	$fp,48($sp)
	addiu	$sp,$sp,56
	j	$ra
annotationTest:
	addiu	$sp,$sp,-24
	sw	$fp,20($sp)
	move	$fp,$sp
	li	$2,1			# 0x1
	sw	$2,8($fp)
	lw	$2,8($fp)
	# <hazard>
	# @{ print($2.get()) }@
	# @{ $2.set(10) }@
	sw	$2,8($fp)
	lw	$3,8($fp)
	# <hazard>
	li	$v0, 1 # print int
	move	$a0, $3
	syscall
	nop
	move	$sp,$fp
	lw	$fp,20($sp)
	addiu	$sp,$sp,24
	j	$ra
main:
	addiu	$sp,$sp,-48
	sw	$ra,44($sp)
	sw	$fp,40($sp)
	move	$fp,$sp
	jal	offsetTest
	li	$5,3			# 0x3
	li	$4,1			# 0x1
	la	$2,addTwoIntsStatic
	move	$25,$2
	jal	$25
	jal	annotationTest
	la	$3,input
	li	$v0, 4 # print string
	move	$a0, $3
	syscall
	# reading int
	li	$v0, 5 # read int
	syscall
	move	$3, $v0
	sw	$3,24($fp)
	lw	$2,24($fp)
	# <hazard>
	sll	$2,$2,16
	sra	$2,$2,16
	move	$3,$2
	la	$2,globalA
	lw	$2,0($2)
	# <hazard>
	sll	$2,$2,24
	sra	$2,$2,24
	move	$5,$2
	move	$4,$3
	jal	addTwo
	sw	$2,28($fp)
	lw	$3,28($fp)
	# <hazard>
	li	$v0, 1 # print int
	move	$a0, $3
	syscall
	# @{ debug.alert('going to read a string!'); }@
	la	$3,input
	li	$6,5			# 0x5
	li	$v0, 8 # read string
	move	$a0, $3
	move	$a1, $6
	syscall
	la	$3,input
	li	$v0, 4 # print string
	move	$a0, $3
	syscall
	# sbrk:
	li	$3,8			# 0x8
	li	$v0, 9 # sbrk
	move	$a0, $3
	syscall
	move	$3, $v0
	sw	$3,32($fp)
	lw	$3,32($fp)
	# <hazard>
	li	$v0, 1 # print int
	move	$a0, $3
	syscall
	lw	$2,32($fp)
	# <hazard>
	lb	$3,0($2)
	# <hazard>
	li	$v0, 1 # print int
	move	$a0, $3
	syscall
	li	$v0, 10 # exit
	syscall
	move	$2,$0
	move	$sp,$fp
	lw	$ra,44($sp)
	lw	$fp,40($sp)
	addiu	$sp,$sp,48
	j	$ra
_Z41__static_initialization_and_destruction_0ii:
	addiu	$sp,$sp,-8
	sw	$fp,4($sp)
	move	$fp,$sp
	sw	$4,8($fp)
	sw	$5,12($fp)
	lw	$3,8($fp)
	li	$2,1			# 0x1
	bne	$3,$2,LBL_23
	lw	$3,12($fp)
	li	$2,65535			# 0xffff
	bne	$3,$2,LBL_23
	la	$2,globalA
	lw	$3,0($2)
	la	$2,globalB
	sw	$3,0($2)
LBL_23:
	nop
	move	$sp,$fp
	lw	$fp,4($sp)
	addiu	$sp,$sp,8
	j	$ra
_GLOBAL__sub_I_input:
	addiu	$sp,$sp,-32
	sw	$ra,28($sp)
	sw	$fp,24($sp)
	move	$fp,$sp
	li	$5,65535			# 0xffff
	li	$4,1			# 0x1
	la	$2,_Z41__static_initialization_and_destruction_0ii
	move	$25,$2
	jal	$25
	move	$sp,$fp
	lw	$ra,28($sp)
	lw	$fp,24($sp)
	addiu	$sp,$sp,32
	j	$ra
	.word	_GLOBAL__sub_I_input
