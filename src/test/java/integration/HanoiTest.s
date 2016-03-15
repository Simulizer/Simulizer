# based on sample code from canvas
#
# calling convention: $s registers saved on entry, restored on exit
#
# Algorithm:
#
# the number of disks are taken to be placed in the first pole of 3
# where the value of the integer repre sents the size of the disk.
#
# the steps of MOVE are:
# 1. move n?1 discs from pole 1 to pole 2. This leaves disc n alone on pole 1
# 2. move disc n from pole 1 to pole 3
# 3. move n?1 discs from pole 2 to pole 3 so they sit on disc n
#
# @{ var h = vis.load('tower-of-hanoi', false) }@


.data    # variables section
    .align 2

# null terminated ASCII strings
prompt:        .asciiz "\nEnter a number of discs: "
arrow:         .asciiz "->"
newline:       .asciiz "\n"


invalid_input: .asciiz "\ninput invalid."
done:          .asciiz "\ndone."



.text         # code section
    .globl main   # global name (can be referenced in other files)

# must be lower case because the OS of spim looks for this symbol specifically
main:

    # prompt
    li $v0, 4       # 4: print string
    la $a0, prompt  # load from address into a0: argument 0
    syscall

    # read input
    li $v0, 5       # 5: read int into $v0
    syscall

    blez $v0, ERROR_EXIT   # if input <= 0: exit

nop # @{ h.show()                 }@
    # @{ h.setNumDisks($v0.get()) }@

    # call function: MOVE
    li   $a0, 1
    li   $a1, 2
    li   $a2, 3
    move $a3, $v0   # number of disks (read from user) into argument 3

    # MOVE(src=1, tmp=2, dest=3, n=input())
    jal MOVE        # jump and store the return address (next instruction) in $ra

    # print done
    li $v0, 4       # 4: print string
    la $a0, done
    syscall

    # exit
    li $v0, 10
    syscall


# when the user enters an invalid number of disks
ERROR_EXIT:
    # print error message
    li $v0, 4       # 4: print string
    la $a0, invalid_input
    syscall

    # exit
    li $v0, 10
    syscall



# MOVE(src_pole, tmp, dest_pole, number_of_disks)
MOVE:

    blez $a3, MOVE_RETURN   # if number_of_disks <= 0: return

    # save/push arguments that may be modified onto the stack (spill)
    #
    # sw $a, offset($b): store word $a into memory at address: $b + offset
    #
    # stack frame: high [return address][src][tmp][dest][number_of_disks] low
    #
    addi $sp, $sp, -20      # grow the stack by 20 bytes
    sw   $ra, 16($sp)       # store the return address

    sw   $a0, 12($sp)       # store arg 0: source pole      (src)
    sw   $a1,  8($sp)       # store arg 1: tmp
    sw   $a2,  4($sp)       # store arg 2: destination pole (dest)
    sw   $a3,   ($sp)       # store arg 3: number of disks  (n)


    # call recursively
    lw   $a1, 4($sp)        # move:tmp  <- dest from the stack
    lw   $a2, 8($sp)        # move:dest <- tmp  from the stack
    addi $a3, $a3, -1       # move:n    <- n - 1
    jal MOVE                # MOVE(src=src, tmp=dest, dest=tmp, n=n-1)

    # print move
    lw  $a0, 12($sp)        # print_move:src  <- src from the stack
    lw  $a1,  4($sp)        # print_move:dest <- dest from the stack
    jal PRINT_MOVE          # PRINT_MOVE(src=src, dest=dest)

    # call recursively
    lw  $a0,  8($sp)        # move:src  <- tmp  from the stack
    lw  $a1, 12($sp)        # move:tmp  <- src  from the stack
    lw  $a2,  4($sp)        # move:dest <- dest from the stack
                            # n is unchanged and so: n=(passed in n)-1
    jal MOVE                # MOVE(src=tmp, tmp=src, dest=dest, n=n-1)

    # return
    lw   $ra, 16($sp)       # load the return address from the stack

    lw   $a0, 12($sp)       # load the arguments from the stack
    lw   $a1,  8($sp)
    lw   $a2,  4($sp)
    lw   $a3,  0($sp)
    addi $sp, $sp, 20       # shrink the stack by 20 bytes

    # fall through to MOVE_RETURN and return to caller


MOVE_RETURN:
    jr $ra          # return to caller




# PRINT_MOVE(src, dest)
PRINT_MOVE:

    # @{ h.move($a0.get()-1, $a1.get()-1) }@

    # save/push arguments that may be modified onto the stack (spill)
    addi $sp, $sp, -4       # grow the stack by 4 bytes
    sw   $a0, 0($sp)        # store argument 0

    # print source pole
    li $v0, 1               # 1: print int
                            # $a0 already contains the integer to print
    syscall

    # print arrow
    li $v0, 4               # 4: print string
    la $a0, arrow
    syscall

    # print destination pole
    li   $v0, 1             # 1: print int
    move $a0, $a1           # a0 <- a1. print the second argument
    syscall

    # print newline
    li $v0, 4               # 4: print string
    la $a0, newline
    syscall

    # return
    lw   $a0, 0($sp)        # restore $a0 from the stack
    addi $sp, $sp, 4        # shrink the stack by 4 bytes
    jr   $ra                # return to caller
