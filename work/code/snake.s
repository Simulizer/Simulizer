    # compiled with -O3 (with hand-edited fixes involving mflo)
	# @{ var c = vis.load('canvas'); }@
	# @{ c.squareShaped = true;      }@
	# @{ c.showFPS = true;           }@
	# @{ var g = c.ctx;              }@
	# @{ sim.setSpeed(7000); //Hz    }@
	#
	# @{ function randInt(min, max) // result in [min, max) }@
	# @{   { return Math.random()*(max-min)+min }           }@
	.text
render:
	la	$2,pixels
	nop # @{ var pixels = sim.readBoolsFromMem($2.get(), rows*cols); }@
	nop # @{ //c.drawPixels(pixels, cols); }@
	nop # @{ c.drawTiles(pixels, cols, 0.2); }@
	j	$ra
setPixel:
	la	$2,cols
	lw	$2,0($2)
	la	$3,pixels
	mul $2,$4,$2
	sw	$5,4($sp)
	sw	$4,0($sp)
	#mflo $2
	addu	$2,$3,$2
	addu	$5,$2,$5
	sb	$6,0($5)
	j	$ra
placeFood:
	nop # @{ $2.set(randInt(0, rows)); }@
	la	$4,food
	sw	$2,0($4)
	nop # @{ $5.set(randInt(0, cols)); }@
	la	$3,cols
	lw	$3,0($3)
	sw	$5,4($4)
	mul $2,$3,$2
	la	$4,pixels
	#mflo $2
	addu	$3,$4,$2
	addu	$2,$3,$5
	li	$3,1			# 0x1
	sb	$3,0($2)
	j	$ra
removeTail:
	la	$2,snake
	la	$4,cols
	lw	$3,0($2)
	lw	$4,0($4)
	lw	$5,4($2)
	mul $3,$3,$4
	la	$4,headIndex
	lw	$7,0($4)
	la	$4,pixels
	#mflo $3
	addu	$3,$4,$3
	addu	$3,$3,$5
	sb	$0,0($3)
	beq	$7,$0,LBL_7
	addiu	$2,$2,8
	li	$3,1			# 0x1
LBL_10:
	lw	$6,0($2)
	lw	$5,4($2)
	addiu	$3,$3,1
	sltu	$4,$7,$3
	sw	$6,-8($2)
	sw	$5,-4($2)
	addiu	$2,$2,8
	beq	$4,$0,LBL_10
LBL_7:
	j	$ra
checkGameOver:
	la	$2,headIndex
	lw	$5,0($2)
	la	$2,snake
	sll	$3,$5,3
	addu	$3,$2,$3
	la	$4,rows
	lw	$6,0($3)
	lw	$4,0($4)
	# <hazard>
	sltu	$4,$4,$6
	bne	$4,$0,LBL_14
	lw	$7,4($3)
	la	$3,cols
	lw	$3,0($3)
	# <hazard>
	sltu	$3,$3,$7
	bne	$3,$0,LBL_14
	move	$3,$0
	bne	$5,$0,LBL_18
	b	LBL_25
LBL_17:
	addiu	$2,$2,8
	beq	$5,$3,LBL_26
LBL_18:
	lw	$4,0($2)
	addiu	$3,$3,1
	bne	$4,$6,LBL_17
	lw	$4,4($2)
	# <hazard>
	bne	$4,$7,LBL_17
LBL_14:
	li	$3,1			# 0x1
	la	$2,gameOver
	sb	$3,0($2)
	j	$ra
LBL_26:
	j	$ra
LBL_25:
	j	$ra
tick:
	nop # @{ $2.set(c.input); }@
	la	$3,UP
	lw	$3,0($3)
	# <hazard>
	and	$4,$2,$3
	beq	$4,$0,LBL_28
	la	$14,headIndex
	lw	$7,0($14)
	la	$8,snake
	sll	$9,$7,3
	addu	$9,$8,$9
	la	$2,direction
	lw	$11,0($9)
	lw	$13,4($9)
	sw	$3,0($2)
LBL_29:
	addiu	$11,$11,-1
LBL_36:
	la	$2,food
	lw	$3,0($2)
	# <hazard>
	beq	$3,$11,LBL_56
LBL_39:
	la	$2,cols
	lw	$12,0($2)
	lw	$2,0($8)
	la	$10,pixels
	mul $2,$12,$2
	lw	$3,4($8)
	#mflo $2
	addu	$2,$10,$2
	addu	$2,$2,$3
	sb	$0,0($2)
	beq	$7,$0,LBL_43
	la	$2,snake
	addiu	$2,$2,8
	li	$3,1			# 0x1
LBL_44:
	lw	$6,0($2)
	lw	$5,4($2)
	addiu	$3,$3,1
	sltu	$4,$7,$3
	sw	$6,-8($2)
	sw	$5,-4($2)
	addiu	$2,$2,8
	beq	$4,$0,LBL_44
LBL_43:
	mul $3,$11,$12
	addu	$2,$10,$13
	sw	$11,0($9)
	sw	$13,4($9)
	#mflo $3
	addu	$10,$2,$3
	li	$2,1			# 0x1
	sb	$2,0($10)
LBL_41:
	lw	$5,0($14)
	# <hazard>
	sll	$2,$5,3
	addu	$8,$8,$2
	la	$2,rows
	lw	$6,0($8)
	lw	$2,0($2)
	# <hazard>
	sltu	$2,$2,$6
	bne	$2,$0,LBL_45
	lw	$7,4($8)
	# <hazard>
	sltu	$12,$12,$7
	bne	$12,$0,LBL_45
	beq	$5,$0,LBL_57
	la	$2,snake
	move	$3,$0
	b	LBL_49
LBL_48:
	addiu	$2,$2,8
	beq	$5,$3,LBL_58
LBL_49:
	lw	$4,0($2)
	addiu	$3,$3,1
	bne	$6,$4,LBL_48
	lw	$4,4($2)
	# <hazard>
	bne	$7,$4,LBL_48
LBL_45:
	li	$3,1			# 0x1
	la	$2,gameOver
	sb	$3,0($2)
	j	$ra
LBL_28:
	la	$4,DOWN
	lw	$5,0($4)
	# <hazard>
	and	$4,$2,$5
	beq	$4,$0,LBL_30
	la	$14,headIndex
	lw	$7,0($14)
	la	$8,snake
	sll	$9,$7,3
	addu	$9,$8,$9
	la	$2,direction
	sw	$5,0($2)
	lw	$11,0($9)
	lw	$13,4($9)
	beq	$3,$5,LBL_29
LBL_31:
	la	$2,food
	lw	$3,0($2)
	addiu	$11,$11,1
	bne	$3,$11,LBL_39
LBL_56:
	lw	$3,4($2)
	# <hazard>
	bne	$13,$3,LBL_39
	slti $3,$7,19
	beq	$3,$0,LBL_40
	addiu	$7,$7,1
	sll	$3,$7,3
	addu	$3,$8,$3
	sw	$7,0($14)
	sw	$11,0($3)
	sw	$13,4($3)
LBL_40:
	nop # @{ $3.set(randInt(0, rows)); }@
	sw	$3,0($2)
	nop # @{ $4.set(randInt(0, cols)); }@
	la	$5,cols
	lw	$12,0($5)
	sw	$4,4($2)
	mul $2,$12,$3
	la	$3,pixels
	#mflo $2
	addu	$2,$3,$2
	addu	$2,$2,$4
	li	$3,1			# 0x1
	sb	$3,0($2)
	b	LBL_41
LBL_30:
	la	$4,LEFT
	lw	$6,0($4)
	# <hazard>
	and	$4,$2,$6
	bne	$4,$0,LBL_59
	la	$4,RIGHT
	lw	$4,0($4)
	# <hazard>
	and	$2,$2,$4
	bne	$2,$0,LBL_34
	la	$2,direction
	lw	$4,0($2)
LBL_33:
	la	$14,headIndex
	lw	$7,0($14)
	la	$8,snake
	sll	$9,$7,3
	addu	$9,$8,$9
	lw	$11,0($9)
	lw	$13,4($9)
	beq	$3,$4,LBL_29
	beq	$5,$4,LBL_31
	beq	$6,$4,LBL_60
	la	$2,RIGHT
	lw	$2,0($2)
	# <hazard>
	bne	$2,$4,LBL_36
	addiu	$13,$13,1
	b	LBL_36
LBL_58:
	j	$ra
LBL_59:
	la	$2,direction
	sw	$6,0($2)
	move	$4,$6
	b	LBL_33
LBL_60:
	addiu	$13,$13,-1
	b	LBL_36
LBL_34:
	la	$2,direction
	sw	$4,0($2)
	b	LBL_33
LBL_57:
	j	$ra
main:
	la	$2,rows
	lw	$2,0($2)
	# <hazard>
	nop # @{ var rows = $2.get(); }@
	la	$3,cols
	lw	$2,0($3)
	# <hazard>
	nop # @{ var cols = $2.get(); }@
LBL_62:
	nop # @{ c.setFont('Monospace', 24); }@
	nop # @{ c.clear(); }@
	nop # @{ c.centerText('Press Arrows To Move'); }@
	nop # @{ $2.set(c.input); }@
	beq	$2,$0,LBL_62
	nop # @{ $2.set(randInt(0, rows)); }@
	la	$10,food
	sw	$2,0($10)
	nop # @{ $9.set(randInt(0, cols)); }@
	lw	$3,0($3)
	la	$15,pixels
	mul $2,$3,$2
	li	$7,1			# 0x1
	sll	$5,$3,3
	sll	$6,$3,1
	la	$4,snake
	addu	$6,$6,$5
	addu	$3,$5,$3
	la	$24,gameOver
	addu	$3,$15,$3
	addu	$6,$15,$6
	lbu	$11,0($24)
	li	$8,10			# 0xa
	#mflo $2
	addu	$2,$15,$2
	addu	$2,$2,$9
	sb	$7,0($2)
	li	$2,9			# 0x9
	sw	$2,8($4)
	li	$2,8			# 0x8
	sb	$7,10($6)
	addu	$5,$15,$5
	sb	$7,10($3)
	sw	$2,16($4)
	li	$3,2			# 0x2
	la	$2,headIndex
	sw	$9,4($10)
	sw	$8,0($4)
	sw	$8,4($4)
	sw	$8,12($4)
	sw	$8,20($4)
	sw	$3,0($2)
	sb	$7,10($5)
	bne	$11,$0,LBL_72
	addiu	$sp,$sp,-32
	sw	$ra,28($sp)
LBL_67:
	nop # @{ var pixels = sim.readBoolsFromMem($15.get(), rows*cols); }@
	nop # @{ //c.drawPixels(pixels, cols); }@
	nop # @{ c.drawTiles(pixels, cols, 0.2); }@
	jal	tick
	lbu	$2,0($24)
	# <hazard>
	beq	$2,$0,LBL_67
	nop # @{ c.setFont('Monospace', 50); }@
	nop # @{ c.centerText('Game Over!'); }@
	li	$v0, 10 # exit
	syscall
	lw	$ra,28($sp)
	move	$2,$0
	addiu	$sp,$sp,32
	j	$ra
LBL_72:
	nop # @{ c.setFont('Monospace', 50); }@
	nop # @{ c.centerText('Game Over!'); }@
	li	$v0, 10 # exit
	syscall
	move	$2,$0
	j	$ra
	.data
direction:
	.word	1
headIndex:
	.space	4
snake:
	.space	160
food:
	.space	8
	.data
RIGHT:
	.word	8
LEFT:
	.word	4
DOWN:
	.word	2
UP:
	.word	1
gameOver:
	.space	1
pixels:
	.space	400
	.data
cols:
	.word	20
rows:
	.word	20
