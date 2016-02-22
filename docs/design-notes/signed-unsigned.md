# Signed Unsigned #
This document outlines design decisions regarding the handling of both signed
and unsigned integer values in our simulator, despite Java having no native
support for unsigned integers or half words (2 bytes)

## Representation ##
- The Simulizer uses a two's complement representation for signed integer values.
- The Simulizer Stores all its values as big endian.

## Literals ##
In spim, when an integer value is given an initial value that is too large for
its size, the value is truncated to the length of the variable.

## Behaviour ##
When converting to an unsigned integer (whether the source is smaller (widening)
or larger (narrowing)) the result is `dest = src mod 2^n` where `n` is the
number of bits of the destination.

When widening a two's complement integer, the sign bit is copied to all of the
extra bits (sign extension)

One scheme for narrowing a two's complement integer (implementation specific in
c) is to take the `n` least significant bits.


## Loading and Storing as bytes ##
in order to correctly handle overflow, Simulizer internally loads data of any
length into a long, since this is guaranteed to be 64 bits and hence longer than
any integer offered in SmallMips.

If a value is to be loaded as an unsigned variable, then the bytes are simply
copied into the least significant bits of the destination.

If a value is to be loaded as a signed variable, then sign extension must be
performed.

When storing back from a long value, if the value is too large to represent then
the appropriate narrowing operation will be performed as described above.
