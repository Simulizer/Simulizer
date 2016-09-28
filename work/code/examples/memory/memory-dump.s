	# compiled using GCC with optimisation level s
	.text
memset:
	andi	$5,$5,0x00ff
	addu	$6,$4,$6
LBL_3:
	beq	$4,$6,LBL_1
	addiu	$4,$4,1
	sb	$5,-1($4)
	b	LBL_3
LBL_1:
	j	$ra
free:
	la	$3,malloc_free_list
	lw	$2,0($3)
	addiu	$5,$4,-8
	move	$6,$3
LBL_7:
	lw	$3,0($2)
	sltu	$7,$2,$5
	sltu	$8,$2,$3
	bne	$8,$0,LBL_8
	beq	$7,$0,LBL_19
	b	LBL_9
LBL_8:
	bne	$7,$0,LBL_19
LBL_10:
	move	$2,$3
	b	LBL_7
LBL_19:
	sltu	$7,$5,$3
	beq	$7,$0,LBL_10
LBL_9:
	lw	$9,-4($4)
	# <hazard>
	sll	$8,$9,3
	addu	$8,$5,$8
	bne	$3,$8,LBL_11
	lw	$7,4($3)
	# <hazard>
	addu	$7,$7,$9
	sw	$7,-4($4)
	lw	$3,0($3)
	# <hazard>
LBL_11:
	sw	$3,-8($4)
	lw	$8,4($2)
	# <hazard>
	sll	$7,$8,3
	addu	$7,$2,$7
	bne	$5,$7,LBL_13
	lw	$3,-4($4)
	# <hazard>
	addu	$3,$3,$8
	sw	$3,4($2)
	lw	$3,-8($4)
	# <hazard>
	sw	$3,0($2)
	b	LBL_14
LBL_13:
	sw	$5,0($2)
LBL_14:
	sw	$2,0($6)
	j	$ra
request_mem:
	addiu	$sp,$sp,-32
	sw	$ra,28($sp)
	move	$5,$4
	sll	$3,$4,3
	li	$v0, 9 # sbrk
	move	$a0, $3
	syscall
	move	$3, $v0
	sw	$5,4($3)
	addiu	$4,$3,8
	jal	free
	lw	$ra,28($sp)
	la	$2,malloc_free_list
	lw	$2,0($2)
	addiu	$sp,$sp,32
	j	$ra
malloc:
	addiu	$sp,$sp,-32
	sw	$ra,28($sp)
	la	$11,malloc_free_list
	addiu	$4,$4,7
	lw	$2,0($11)
	srl	$10,$4,3
	addiu	$10,$10,1
	bne	$2,$0,LBL_23
	la	$2,malloc_first_block
	sw	$2,0($2)
	sw	$0,4($2)
	sw	$2,0($11)
LBL_23:
	lw	$4,0($11)
	# <hazard>
	lw	$2,0($4)
	# <hazard>
LBL_28:
	lw	$3,4($2)
	# <hazard>
	sltu	$5,$3,$10
	bne	$5,$0,LBL_24
	bne	$10,$3,LBL_25
	lw	$3,0($2)
	# <hazard>
	sw	$3,0($4)
	b	LBL_26
LBL_25:
	subu	$3,$3,$10
	sw	$3,4($2)
	sll	$3,$3,3
	addu	$2,$2,$3
	sw	$10,4($2)
	b	LBL_26
LBL_24:
	lw	$3,0($11)
	# <hazard>
	bne	$2,$3,LBL_27
	move	$4,$10
	jal	request_mem
LBL_27:
	move	$4,$2
	lw	$2,0($2)
	b	LBL_28
LBL_26:
	lw	$ra,28($sp)
	addiu	$2,$2,8
	sw	$4,0($11)
	addiu	$sp,$sp,32
	j	$ra
	.text
main:
	addiu	$sp,$sp,-32
	sw	$ra,28($sp)
	# allocate a and fill with 0xAB
	li	$4,5			# 0x5
	jal	malloc
	move	$4,$2
	la	$12,memA
	li	$6,5			# 0x5
	li	$5,171			# 0xab
	sw	$2,0($12)
	jal	memset
	# allocate b and fill with 0xCD
	li	$4,10			# 0xa
	jal	malloc
	move	$4,$2
	la	$3,memB
	li	$6,10			# 0xa
	li	$5,205			# 0xcd
	sw	$2,0($3)
	jal	memset
	li	$2,8			# 0x8
	nop # @{ var header_size = $2.get(); }@
	nop # @{ log('free list header size = ' + header_size + ' bytes'); }@
	# transfer memA and memB to Javascript
	lw	$2,0($12)
	# <hazard>
	nop # @{ var memA = $2.get(); }@
	lw	$2,0($3)
	# <hazard>
	nop # @{ var memB = $2.get(); }@
	nop # @{ log('memA = ' + memA); }@
	nop # @{ log('memB = ' + memB); }@
	nop # @{ var data_start  = memA - header_size; }@
	nop # @{ var data_length = 16 + 24; // see source code for explanation }@
	nop # @{ var mem = sim.readBytesFromMem(data_start, data_length); }@
	nop # @{ log('heap memory:'); }@
	nop # @{ log(debug.hex(mem, 8)); // spaces every 8 digits (4 bytes) }@
	li	$v0, 10 # exit
	syscall
	lw	$ra,28($sp)
	move	$2,$0
	addiu	$sp,$sp,32
	j	$ra
	.data
malloc_free_list:
	.space	4
malloc_first_block:
	.space	8
memB:
	.space	4
memA:
	.space	4
