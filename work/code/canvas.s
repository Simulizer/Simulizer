# A test of the canvas visualiser
#

# @{ var c = vis.load('canvas'); }@
# @{ var g = c.ctx; }@

.data

.text
main:
    nop # @{ print(c.input); }@
    nop # @{ c.drawPixels([[c.input & c.UP, c.input & c.DOWN], [true, true]]); }@
    j main

    li $v0, 10
    syscall
