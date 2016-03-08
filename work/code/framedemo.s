# @{ var f = vis.load('frame', true) }@
# @{ setSpeed(10); }@
.text
main:
    nop # @{ f.commit() }@
    j main
    
end:
    nop