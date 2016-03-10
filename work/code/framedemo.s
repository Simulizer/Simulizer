# @{ vis.show() }@
# @{ var f = vis.load('frame', true) }@
# @{ setSpeed(100); }@
.text
main:
    nop # @{ f.commit() }@
    j main
    
end:
    nop