# Bubblesort
# @{ var l = vis.load('list', false) }@

# @{ var startAddress = null }@
# @{ function addressToIndex(addr) { return (addr - startAddress) / 4; } }@


.data
    input1: .asciiz "\nEnter the number of items:"
    input2: .asciiz "\nEnter item "
    input2b:.asciiz ": "
.text
main:
    jal read_input

nop # @{ startAddress = $v0.get() }@
    # @{ if($v1.get() == -1) ret() // no elements }@
    # @{ l.setList(simulation.readUnsignedWordsFromMem($v1.get(), $v1.get()+4*($v0.get()-1))) }@
    # @{ l.show()     }@
    # @{ setSpeed(10) }@


    move $a0 $v0
    move $a1 $v1

    jal bubble_sort



    li $v0 10
    syscall



read_input:
    # prompt for num of entries
    li $v0 4 # print string
    la $a0 input1
    syscall

    # read num of entries into $s0
    li $v0 5 # read int
    syscall
    move $s0 $v0

    # allocate the heap space for the entries
    # store the base address in $s1
    li $v0 9 # sbrk
    li $t0 4
    mul $a0 $s0 $t0
    syscall
    move $s1 $v0


    # read the items
    li $s2 0    # loop counter
    li $s3 -1   # last item address
read_input_START_READING:
    sub $t0 $s2 $s0 # loop count - num entries
    bgez $t0 read_input_RETURN

    # print prompt
    li $v0 4 # print string
    la $a0 input2
    syscall
    li $v0 1 # print int
    move $a0 $s2
    syscall
    li $v0 4 # print string
    la $a0 input2b
    syscall

    # read entry
    li $v0 5 # read int
    syscall

    # calculate address
    li $t0 4
    mul $t0 $s2 $t0
    add $s3 $t0 $s1

    # store the entry on the heap
    sw $v0 ($s3)

    addi $s2 $s2 1
    j read_input_START_READING

read_input_RETURN:
    move $v0 $s0 # num entries
    move $v1 $s1 # first address
    jr $ra



bubble_sort:
    # $a0: number of items
    # $a1: base address
    # $t0: flag
    # $t1: loop counter
    # $t2: current address
    # $t3: current value
    # $t4: next value
    # $t5: tmp values

bubble_sort_START:
    li $t0 0   # keep track of swaps
    li $t1 0   # loop counter
bubble_sort_LOOP:
    addi $t5 $a0 -2
    sub $t5 $t1 $t5
    bgtz $t5 bubble_sort_RETURN

    # calculate current address
    li $t2 4
    mul $t2 $t1 $t2
    add $t2 $t2 $a1

    # load current value and next value
    lw $t3  ($t2)
    lw $t4 4($t2)

    # check if out of order
    sub $t5 $t3 $t4
    blez $t5 bubble_sort_NO_SWAP

    # swap
    sw $t3 4($t2)
    sw $t4 0($t2)
    li $t0 1

    # @{ log('swap ' + $t1.get() + ' and ' + ($t1.get()+1)) }@
    # @{ l.swap($t1.get(), $t1.get()+1) }@


bubble_sort_NO_SWAP:
    addi $t1 $t1 1
    j bubble_sort_LOOP

bubble_sort_RETURN:
    bne $t0 $zero bubble_sort_START # back to the start if sorted flag set
    jr $ra
