# A demo of the canvas visualisation
#
# @{ var c = vis.load('canvas'); }@
# @{ var g = c.ctx; }@
# @{ sim.setSpeed(10); // Hz }@

.data

.text
main:
    # press the UP and DOWN arrow keys to affect the canvas
    nop # @{ print('input vector: ' + c.input); }@
        # @{ var up   = c.input & c.UP; }@
        # @{ var down = c.input & c.DOWN; }@
        # @{ c.drawPixels([up, down, !down, !up], 2/*cols*/); }@
    j main

    li $v0, 10
    syscall
