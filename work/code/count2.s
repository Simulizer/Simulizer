.text
main:
      li $s0 1
      li $t0 4
      li $v0 1
      
loop: syscall
      
      # set $a0 = 2^$s0
      # @{ var po2 = Math.pow(2, $s0.get())    }@
      # @{ $a0.set(po2)                        }@
      # @{ log("Register a0 is: " + $a0.get()) }@
      
      addi $s0 $s0 1
      
      ble $s0 $t0 loop