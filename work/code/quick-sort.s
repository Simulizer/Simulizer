#Implementation of quick sort in MIPS assembly code
#Author: Charlie Street

# @{ var l = vis.load('list') }@
# @{ sim.setSpeed(50) }@
# @{ var startAddress = null }@
# @{ function addressToIndex(addr) { return (addr - startAddress) / 4; } }@
# @{ function swapIfDifferent(addr1, addr2){if(addr1 !== addr2) {l.swap(addressToIndex(addr1), addressToIndex(addr2))} } }@
# @{ function setPivot(addr1) { l.clearMarkers(); l.setMarkers("P",addressToIndex(addr1)); l.emphasise(addressToIndex(addr1))} }@

.data
.align 2

input:     .asciiz "\nEnter array items. Enter 0 to finish entry."
emptyArray:.asciiz "\nEmpty array. Nothing is in the array so quick sort can't be carried out. System will now exit"
sorted:    .asciiz "\nThe array has been sorted. The sorted array is as follows:"

.text
.globl main


#Main Function: Carries out the entire program of input, sort and then output
main:
        move $s3, $zero         ; #storing zero into s3, this will be used to keep track of heap size
        jal IN                  ; #go to the in function and return back here once finished
        la $s2, ($s1)           ; #s2 will store the end address of the array
        bne $s3, $zero, NEmpty  ; #if user has actually entered something
        li $v0, 4               ; #syscall for print string
        la $a0, emptyArray      ; #string telling user that quick sort can't be done
        syscall                 ; #print the string
        j EXIT                  ; #exit the program


#NEmpty: Function called if array isn't empty, will sort the non empty array and will then output the sorted array        
NEmpty: li $t0, 4               ; #setting t0 to 4, will let me go back to the start of the heap using back
        jal BACK                ; #will take heap pointer back to start of array
        la $s0, ($s1)           ; #storing the start of the heap into $s0
        
        nop  # @{ startAddress = $s0.get() }@
             # @{ l.setList(simulation.readUnsignedWordsFromMem($s0.get(), $s2.get())) }@
             # @{ l.show()     }@
             # @{ setSpeed(10) }@
        
        jal QSORT               ; #jumping to the QSort subroutine which will sort the array
        jal PRINTARR            ; #printing out the sorted array
        j EXIT                  ; #will jump to the label which will cause the system to exit


#IN: Function takes the input of the array from the user
IN:     li $v0, 4               ; #syscall code for print string
        la $a0, input           ; #load the string to print into a0
        syscall                 ; #print to the user
        li $v0, 5               ; #syscall code for read int
        syscall                 ; #read the number
        beq $zero,$v0,RETURN    ; #if number entered is 0, break out of the input loop by jumping back to original point
        move $s0, $v0           ; #move input into s0
        li $v0, 9               ; #syscall code for sbrk
        li $a0, 4               ; #adding 4 bytes (a word) to the heap
        syscall                 ; #allocating on the heap
        move $s1, $v0           ; #storing the address of the start of this heap block into s1
        sw $s0, ($s1)           ; #storing the first input into the heap position
        addi $s3, $s3, 4        ; #adding 4 to keep track of the array size
        j IN                    ; #jumping back to start of loop


#BACK: Given the end address of the array and the number of items, the BACK function moves back to the start of the array
BACK:   beq $t0, $s3, RETURN    ; #condition for exiting this sub routine
        addi $t0, $t0, 4        ; #incrementing t0
        la $s1, -4($s1)         ; #going back in sets of bytes
        j BACK                  ; #go round again
      
        
#QSORT: Function carries out the quick sort algorithm
		#$s0 stores the start address of the array
		#$s1 will store the pivot address
		#$s2 will store the address of the last item in the array
		#$s3 will store the size of the array in bytes
QSORT:  addi $sp, $sp, -20      ; #stack frame
        sw $s0, 16($sp)         ; 
        sw $s1, 12($sp)         ; 
        sw $s2, 8($sp)          ; 
        sw $s3, 4($sp)          ; 
        sw $ra, 0($sp)          ;
        li $t6, 4               ;
        sub $t0, $s3, $t6       ; #checking if left < right
        blez $t0, RETURNQ       ; #return back to original program 
        move $a0, $s0           ; #storing start address in $a0
        move $a1, $s2           ; #storing end address in $a1
        move $a2, $s3           ; #storing current array size
        jal PARTITION           ; #call partition function
        sw $s1, 12($sp)         ; #will now store the partition address in $s1
        sub $s3, $s1, $s0       ; #new size of array 
        addi $s2, $s1, -4       ; #right = pivot - 1 (in terms of items)
        jal QSORT               ; 
        lw $ra, 0($sp)          ; 
        lw $s3, 4($sp)          ; 
        lw $s2, 8($sp)          ; 
        lw $s1, 12($sp)         ; 
        lw $s0, 16($sp)         ;
        addi $s0, $s1, 4        ; #set start of array as partition address + 1 (in terms of items)
        sub $s3, $s2, $s0       ; #new size of array
        addi $s3, $s3, 4        ;
        jal QSORT               ; 
        lw $ra, 0($sp)          ; #restoring registers
        lw $s3, 4($sp)          ; 
        lw $s2, 8($sp)          ;
        lw $s1, 12($sp)         ; 
        lw $s0, 16($sp)         ; 
        addi $sp, $sp, 20       ; #restoring stack pointer to original value
        j RETURN                ; #return from function



#RETURNQ: Like RETURN, only the stack pointer is increased before hand
RETURNQ: addi $sp, $sp, 20      ;
         j RETURN               ;
   
   
   
#PARTITION: Method for partitioning the array for the quick sort algorithm
			#$a0 stores start of array
			#$a1 stores address of last array element
			#$a2 stores size of array in bytes
PARTITION: addi $sp, $sp, -16   ; #stack frame
           sw $ra, 12($sp)      ; 
           sw $a2, 8($sp)       ;
           sw $a1, 4($sp)       ;
           sw $a0, 0($sp)       ;
           li $t2, 8            ; #for division
           div $a2, $a2, $t2    ; #size/2 (in terms of items, not addresses)
           jal GETMID           ; #v0 now contains the pivot, v1 has the address of the pivot
           lw $ra, 12($sp)      ; #restoring registers before iteration
           lw $a2, 8($sp)       ;
           lw $a1, 4($sp)       ;
           lw $a0, 0($sp)       ;
           addi $sp, $sp, 16    ; #restoring old stack pointer
           lw $t0, ($a0)        ; #load left item into t0 @{ swapIfDifferent($a0.get(),$v1.get()); setPivot($a0.get()) }@
           sw $v0, ($a0)        ; #store pivot at left of array
           sw $t0, ($v1)        ; #store left contents in middle of array
           addi $t0, $a0, 4     ; #lo = left + 1 (in terms of items)
           move $t1, $a1        ; #hi = right
PARTITR:   sub $t2, $t0, $t1    ; #while(lo <= hi)
           bgtz $t2, RETPART    ; #return from partition
HIPIVOT:   lw $t3, ($t1)        ; #value at hi
           ble $t3, $v0, LOPIVOT; #branch if A[hi] <= pivot 
           addi $t1, $t1, -4    ; #hi = hi-1 (items wise again)
           j HIPIVOT            ; #iterate back round
LOPIVOT:   bgt $t0, $t1, LOHIGH ; #if lo > hi dont loop
           lw $t3, ($t0)        ; #A[lo]
           bgt $t3, $v0, LOHIGH ; #if A[lo] > pivot
           addi $t0, $t0, 4     ; #lo = lo + 1 (items wise)
           j LOPIVOT            ; #loop back round
LOHIGH:    bgt $t0, $t1, PARTITR; #loop back round master loop if lo > hi
           lw $t3, ($t0)        ; #t3 = A[lo] 
           lw $t4, ($t1)        ; #t4 = A[hi] @{ swapIfDifferent($t0.get(),$t1.get()) }@
           sw $t4, ($t0)        ; #doing a swap
           sw $t3, ($t1)        ; #more swaps # 
           addi $t0, $t0, 4     ; #lo = lo + 1 (items-wise)
           addi $t1, $t1, -4    ; #hi = hi - 1 (items-wise)
           j PARTITR            ; #go back round main loop
RETPART:   lw $t3, ($a0)        ; #left item
           lw $t4, ($t1)        ; #hi item @{ swapIfDifferent($a0.get(),$t1.get()); setPivot($t1.get()) }@
           sw $t4, ($a0)        ; #doing a swap
           sw $t3, ($t1)        ; #more swaps 
           move $s1, $t1        ; #s1 will store the mid point for QSORT
           j RETURN             ; #return from function
           
           

#GETMID: Function gets the middle item of some sub-array, used as pivot item in PARTITION
GETMID: beqz $a2, RETMID        ; #if a1 reduced to 0
        addi $a0, $a0, 4        ; #moving address along 4
        addi $a2, $a2, -1       ; #decrementing a1, left to move 
        j GETMID                ; #looping back around
RETMID: lw $v0, ($a0)           ; #return the middle item
        move $v1, $a0           ; #store it's address in v1 @{setPivot($v1.get())}@
        j RETURN                ; #return from function


#PRINTARR: Prints an array, given the number of items in the array and the start address of the array
PRINTARR: move $t1, $s3         ; #t1 holds items left to print
          move $t2, $s0         ; #t2 stores current array address
PLOOP:    beqz $t1, RETURN      ; #if all items printed
          lw $a0, ($t2)         ; #putting array item into a0 for printing
          li $v0, 1             ; #print int
          syscall               ; #do the printing
          addi $t1, $t1, -4     ; #decrement items to print
          addi $t2, $t2, 4;     ; #go to next array item
          j PLOOP               ; #iterate back around


#RETURN: Function jumps to contents of $ra register
RETURN: jr $ra                  ; #jumping back to return address


#EXIT: Function cleanly exits the program
EXIT:   li $v0, 10              ; #syscall code for exiting system
        syscall                 ; #calling syscall will exit the program