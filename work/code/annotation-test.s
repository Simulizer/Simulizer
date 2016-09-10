.data
enabled:  .asciiz "Annotations Enabled"
disabled: .asciiz "Annotations Disabled"
.text
main:
li $v0, 4
la $a0, disabled
la $a1, enabled # @{ $a0.set($a1.get()); }@
syscall
li $v0, 10
syscall
