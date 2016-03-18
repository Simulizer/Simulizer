# binary search
# enter numbers in ascending order, using 0 as an end marker
# @{ var l = vis.loadHidden('list') }@
# @{ sim.setSpeed(2000) }@
# @{ var startAddress = null }@
# @{ function addressToIndex(addr) { return (addr - startAddress) / 4; } }@

.data

prompt: .ascii  "please enter integers in ascending order\n"
        .asciiz "one per line. Enter 0 to denote the end of the input\n"

search_prompt:
        .asciiz "\nNow enter numbers to search in the array:\n"

yes:    .asciiz "YES\n"
no:     .asciiz "NO\n"

eol:    .asciiz "\n"
space:  .asciiz " "

.text
main:

    # $v0 <- first element address
    # $v1 <- last element address
    jal read_and_store

    # debugging: print the list
    #move $a0, $v0
    #move $a1, $v1
    #jal print_list

    # read_and_search(first_elem, last_elem)
    move $a0, $v0
    move $a1, $v1

 # @{ startAddress = $a0.get() }@
 # @{ if(startAddress == -1) ret() // no elements }@
 # @{ l.setList(simulation.readUnsignedWordsFromMem($a0.get(), $a1.get())) }@
 # @{ l.show()     }@
 # @{ setSpeed(15) }@

    jal read_and_search

    li $v0, 10      # 10: exit
    syscall



# read_and_store()
# returns the address of the first and last element of the array in $v0 and $v1
# respectively
read_and_store:

    # print prompt
    li $v0, 4       # 4: print_string
    la $a0, prompt
    syscall

    # s0: address of the first element of the array
    # s1: address of the last element of the array
    li $s0, -1      # indicate not yet set
    li $s1, -1      # indicate not yet set

read_and_store_START_READING:

    li $v0, 5       # 5: read_int
    syscall

    move $t0, $v0   # store temporarily

    # zero => return
    beq $t0, $zero, read_and_store_RETURN

    li $v0, 9       # 9: sbrk
    li $a0, 4       # allocate space for a single word
    syscall

    # store the new value.
    # $v0 contains the newly allocated address
    sw $t0, ($v0)

    # if $s0 > 0 then this is not the first item to be stored
    bgtz $s0, read_and_store_NOT_FIRST

read_and_store_FIRST:

    # this is the first element to be added, so set $s0 and $s1
    move $s0, $v0

    # fall through and set $s1

read_and_store_NOT_FIRST:

    move $s1, $v0    # set the last element of the array
    j read_and_store_START_READING


read_and_store_RETURN:
    move $v0, $s0   # return the address of the first element
    move $v1, $s1   # return the address of the last element
    jr $ra          # return to caller





# read_and_search(first_arr, last_arr)
# read integers from the user and search for it in the array using binary
# search. Quit once zero is entered
read_and_search:

    # store ra, first_arr, last_arr on the stack
    addi $sp, $sp, -12  # grow the stack by 3 words
    sw   $ra, 8($sp)    # return address
    sw   $a0, 4($sp)    # first_arr
    sw   $a1,  ($sp)    # last_arr

    # print prompt
    li $v0, 4       # 4: print_string
    la $a0, search_prompt
    syscall

read_and_search_START_READING:
    li $v0, 5       # 5: read_int
    syscall

    # 0 is never in the list so use it again as the terminator
    beq $v0, $zero, read_and_search_RETURN

    # search for the value in the array using binary search
    move $a0, $v0    # search_for
    lw   $a1, 4($sp) # first_arr
    lw   $a2,  ($sp) # last_arr

    jal binary_search

    # $v0 holds whether the element is in the array, 1: yes, 0: no

    beq $v0, $zero, read_and_search_PRINT_NO

read_and_search_PRINT_YES:
    li $v0, 4       # 4: print string
    la $a0, yes
    syscall

    j read_and_search_START_READING

read_and_search_PRINT_NO:
    li $v0, 4       # 4: print string
    la $a0, no
    syscall

    j read_and_search_START_READING

read_and_search_RETURN:

    # restore the return address from the stack and jump back
    lw   $ra, 8($sp)
    addi $sp, $sp, 12 # shrink the stack by 3 words
    jr   $ra





# binary_search(search_for, first_arr, last_arr)
# returns whether the item was found (1|0) in $v0
binary_search:
    # $a1 (first_addr) == -1  => no elements
    li $t0, -1
    beq $a1, $t0 binary_search_NOT_FOUND

nop 
    # @{ l.clearMarkers() }@
    # @{ l.setMarkers("Left", addressToIndex($a1.get())) }@
    # @{ l.setMarkers("Right", addressToIndex($a2.get())) }@

    # no other calls are made so no need to store registers on the stack

    # if equal then there is still one element to be checked.
    # if first > last then the search ends
    sub $t0, $a1, $a2
    bgtz $t0, binary_search_NOT_FOUND

    # first + (last - first) / 2 to avoid overflow
    # that might be caused by (first + last) / 2
    sub $t0, $a2, $a1  # last - first in bytes

    li  $t1, 4
    div $t0, $t0, $t1  # last - first in words (floored)

    li  $t1, 2
    div $t0, $t0, $t1  # half way distance in words (floored)

    li  $t1, 4
    mul $t0, $t0, $t1  # half way distance in bytes

    add $t0, $t0, $a1  # the address of the half way element
    
    # @{ l.emphasise(addressToIndex($t0.get())) }@
    
    # $t0 now contains the adress of the middle element (floored)
    lw  $t1, ($t0)

    beq $t1, $a0, binary_search_FOUND
    sub $t2, $t1, $a0
    bltz $t2, binary_search_HIGHER

# the element being searched for is in the first half
binary_search_LOWER:
    move $a2, $t0
    addi $a2, $a2, -4   # don't include the element at address $t0
    j binary_search

# the element being searched for is in the second half
binary_search_HIGHER:
    move $a1, $t0
    addi $a1, $a1, 4   # don't include the element at address $t0
    j binary_search


# return found
binary_search_FOUND:
    li $v0, 1
    jr $ra

# return not found
binary_search_NOT_FOUND:
    li $v0, 0
    jr $ra



# for debugging:
# print_list(first_arr, last_arr)
print_list:
    move $s0, $a0
    move $s1, $a1

    # if start > end: break
    sub $t0, $s0, $s1
    bgtz $t0, print_list_RETURN

    li $v0, 1       # 1: print_int
    lw $a0, ($s0)
    syscall

    li $v0, 4       # 4: print_string
    la $a0, space
    syscall

    move $a0, $s0
    addi $a0, $a0, 4    # advance to the next element
    move $a1, $s1
    j print_list

print_list_RETURN:
    jr $ra
