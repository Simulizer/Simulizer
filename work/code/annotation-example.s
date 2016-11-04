# @{ log('setup code'); }@
# @{ log('pipelining: ' + (sim.isPipelined() ? ' on' : ' off')); }@
# @{ sim.setSpeed(4) // Hz }@
# @{ print("this is run before the first instruction is run") }@

.data
.text
main:    # @{var x = 10}@ binds to first nop
    nop  # 0:
    nop  # 4: @{ log('x equals ' + x) // custom output }@

    nop  # 8: binds to the next nop
a:       # @{ function fib(i) {                     }@
label:   # @{     if(i<=2){return 1;}               }@
another: # @{     else{return fib(i-1) + fib(i-2);} }@
         # @{ }                                     }@

    nop  # c: @{ var f = function() { return fib(10); } }@

    nop  # 10:@{ debug.log(x*2) // automatic type conversion }@
    nop  # 14:@{ log('fib(10) = ' + f())     }@
         #    @{ log('fib(15) = ' + fib(15)) }@

    li $v0 10 # 18
    syscall   # 1c @{ alert('bye') // API binds alert = debug.alert }@

