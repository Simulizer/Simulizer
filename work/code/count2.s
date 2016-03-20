.text
main:
      li $a0 1
      li $t0 2
      li $v0 1
      
loop: syscall # @{ log("Register a0 is: " + $a0.get()) }@
      addi $a0 $a0 1 
      ble $a0 $t0 loop
            
      li $v0 10 # @{ pause() }@
      syscall 