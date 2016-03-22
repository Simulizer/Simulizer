## Unit Tests ##

**Name of Component/Class**: `ALU`

| Test ID | Method  | Instruction | Word 1     | Word 2     | Expected    | Actual      | Pass (y/n) | Test Date |
|:--------|:--------|:------------|:-----------|:-----------|:------------|:------------|:-----------|:----------|
| ALU1    | execute | abs         | -10        | empty      | 10          | 10          | y          | 16/2/16   |
| ALU2    | execute | abs         | 27         | empty      | 27          | 27          | y          | 16/2/16   |
| ALU3    | execute | abs         | 0          | empty      | 0           | 0           | y          | 16/2/16   |
| ALU4    | execute | and         | 0          | 17         | 0           | 0           | y          | 16/2/16   |
| ALU5    | execute | and         | 17         | 24         | 16          | 16          | y          | 16/2/16   |
| ALU6    | execute | and         | -5         | -2         | -6          | -6          | y          | 16/2/16   |
| ALU7    | execute | add         | 4          | 7          | 11          | 11          | y          | 16/2/16   |
| ALU8    | execute | add         | -4         | -10        | -14         | -14         | y          | 16/2/16   |
| ALU9    | execute | add         | -7         | 6          | -1          | -1          | y          | 16/2/16   |
| ALU10   | execute | addu        | $2^{31}$   | $2^{31}-1$ | $2^{32}-1$  | $2^{32}-1$  | y          | 16/2/16   |
| ALU11   | execute | addu        | $2^{30}$   | $2^{30}$   | $2^{31}$    | $2^{31}$    | y          | 16/2/16   |
| ALU12   | execute | addu        | 4          | 0          | 4           | 4           | y          | 16/2/16   |
| ALU13   | execute | addi        | 4          | 7          | 11          | 11          | y          | 16/2/16   |
| ALU14   | execute | addi        | -4         | -10        | -14         | -14         | y          | 16/2/16   |
| ALU15   | execute | addi        | -7         | 6          | -1          | -1          | y          | 16/2/16   |
| ALU16   | execute | addiu       | $2^{31}$   | $2^{31}-1$ | $2^{32}-1$  | $2^{32}-1$  | y          | 16/2/16   |
| ALU17   | execute | addiu       | $2^{30}$   | $2^{30}$   | $2^{31}$    | $2^{31}$    | y          | 16/2/16   |
| ALU18   | execute | addiu       | 4          | 0          | 4           | 4           | y          | 16/2/16   |
| ALU19   | execute | sub         | 4          | 7          | -3          | -3          | y          | 16/2/16   |
| ALU20   | execute | sub         | 7          | 4          | 3           | 3           | y          | 16/2/16   |
| ALU21   | execute | sub         | -4         | -10        | 6           | 6           | y          | 16/2/16   |
| ALU22   | execute | subu        | $2^{31}$   | $2^{31}$   | 0           | 0           | y          | 16/2/16   |
| ALU23   | execute | subu        | $2^{31}$   | 0          | 2^{31}      | 2^{31}      | y          | 16/2/16   |
| ALU24   | execute | subu        | $2^{32}-1$ | 1          | $2^{31}-2$  | $2^{31}-2$  | y          | 16/2/16   |
| ALU25   | execute | subi        | 4          | 7          | -3          | -3          | y          | 16/2/16   |
| ALU26   | execute | subi        | 7          | 4          | 3           | 3           | y          | 16/2/16   |
| ALU27   | execute | subi        | -4         | -10        | 6           | 6           | y          | 16/2/16   |
| ALU28   | execute | subiu       | $2^{31}$   | $2^{31}$   | 0           | 0           | y          | 16/2/16   |
| ALU29   | execute | subiu       | $2^{31}$   | 0          | $2^{31}$    | $2^{31}$    | y          | 16/2/16   |
| ALU30   | execute | subiu       | $2^{32}-1$ | 1          | $2^{31}-2$  | $2^{31}-2$  | y          | 16/2/16   |
| ALU31   | execute | mul         | $2^{15}$   | $2^{15}-1$ | 1083709056  | 1083709056  | y          | 16/2/16   |
| ALU32   | execute | mul         | 0          | $2^{15}$   | 0           | 0           | y          | 16/2/16   |
| ALU33   | execute | mul         | -4         | -3         | 12          | 12          | y          | 16/2/16   |
| ALU34   | execute | mulo        | $2^{15}$   | $2^{15}-1$ | 1083709056  | 1083709056  | y          | 16/2/16   |
| ALU35   | execute | mulo        | 0          | $2^{15}$   | 0           | 0           | y          | 16/2/16   |
| ALU36   | execute | mulo        | -4         | -3         | 12          | 12          | y          | 16/2/16   |
| ALU37   | execute | mulou       | $2^16$     | $2^16-1$   | 4294901760  | 4294901760  | y          | 16/2/16   |
| ALU38   | execute | mulou       | 0          | $2^16$     | 0           | 0           | y          | 16/2/16   |
| ALU39   | execute | mulou       | 4          | 3          | 12          | 12          | y          | 16/2/16   |
| ALU40   | execute | div         | 0          | 4          | 0           | 0           | y          | 16/2/16   |
| ALU41   | execute | div         | 4          | 2          | 2           | 2           | y          | 16/2/16   |
| ALU42   | execute | div         | 4          | -2         | -2          | -2          | y          | 16/2/16   |
| ALU43   | execute | divu        | 0          | 4          | 0           | 0           | y          | 16/2/16   |
| ALU44   | execute | divu        | $2^{32}-1$ | $2^{32}-1$ | 1           | 1           | y          | 16/2/16   |
| ALU45   | execute | divu        | 4          | 2          | 2           | 2           | y          | 16/2/16   |
| ALU46   | execute | neg         | 0          | empty      | 0           | 0           | y          | 16/2/16   |
| ALU47   | execute | neg         | 1          | empty      | -1          | -1          | y          | 16/2/16   |
| ALU48   | execute | neg         | -1         | empty      | 1           | 1           | y          | 16/2/16   |
| ALU49   | execute | nor         | 0          | 0          | 0           | 0           | y          | 16/2/16   |
| ALU50   | execute | nor         | 0          | 1          | $2^{30}$    | $2^{30}$    | y          | 16/2/16   |
| ALU51   | execute | nor         | -1         | 1          | 1           | 1           | y          | 16/2/16   |
| ALU52   | execute | not         | 0          | empty      | -1          | -1          | y          | 16/2/16   |
| ALU53   | execute | not         | -1         | empty      | 0           | 0           | y          | 16/2/16   |
| ALU54   | execute | not         | 1          | empty      | -2          | -2          | y          | 16/2/16   |
| ALU55   | execute | or          | 0          | 1          | 1           | 1           | y          | 16/2/16   |
| ALU56   | execute | or          | 1          | 4          | 5           | 5           | y          | 16/2/16   |
| ALU57   | execute | or          | 16         | 4          | 20          | 20          | y          | 16/2/16   |
| ALU58   | execute | ori         | 0          | 1          | 1           | 1           | y          | 16/2/16   |
| ALU59   | execute | ori         | 1          | 4          | 5           | 5           | y          | 16/2/16   |
| ALU60   | execute | ori         | 16         | 4          | 20          | 20          | y          | 16/2/16   |
| ALU61   | execute | xor         | 4          | 14         | 10          | 10          | y          | 16/2/16   |
| ALU62   | execute | xor         | 3          | 1          | 2           | 2           | y          | 16/2/16   |
| ALU63   | execute | xor         | 3          | 0          | 3           | 3           | y          | 16/2/16   |
| ALU64   | execute | xori        | 4          | 14         | 10          | 10          | y          | 16/2/16   |
| ALU65   | execute | xori        | 3          | 1          | 2           | 2           | y          | 16/2/16   |
| ALU66   | execute | xori        | 3          | 0          | 3           | 3           | y          | 16/2/16   |
| ALU67   | execute | b           | 0          | empty      | branchTrue  | branchTrue  | y          | 16/2/16   |
| ALU68   | execute | b           | -20        | empty      | branchTrue  | branchTrue  | y          | 16/2/16   |
| ALU69   | execute | b           | 20         | empty      | branchTrue  | branchTrue  | y          | 16/2/16   |
| ALU70   | execute | beq         | 0          | 1          | branchFalse | branchFalse | y          | 16/2/16   |
| ALU71   | execute | beq         | 0          | 0          | branchTrue  | branchTrue  | y          | 16/2/16   |
| ALU72   | execute | beq         | 0          | -1         | branchFalse | branchFalse | y          | 16/2/16   |
| ALU73   | execute | bne         | 0          | 1          | branchTrue  | branchTrue  | y          | 16/2/16   |
| ALU74   | execute | bne         | 0          | 0          | branchFalse | branchFalse | y          | 16/2/16   |
| ALU75   | execute | bne         | 0          | -1         | branchTrue  | branchTrue  | y          | 16/2/16   |
| ALU76   | execute | bgez        | 0          | 1          | branchTrue  | branchTrue  | y          | 16/2/16   |

**Name of Component/Class**: `DynamicDataSegment`

Date tested: 17/12/16

| Test ID | Method name                                                        | Input 1         | Input 2 | Expected                        | Actual                          | Pass (y/n) |
|:--------|:------------------------------------------------------------|:----------------|:-----------|:--------------------------|:--------------------------|:-----------|
| DDS1    | size                                                               | n/a             | n/a     | 0                               | 0                               | y          |
| DDS2    | sbrk                                                               | 8               | n/a     | 10                              | 10                              | y          |
| DDS3    | sbrk                                                               | 4               | n/a     | 18                              | 18                              | y          |
| DDS4    | size                                                               | n/a             | n/a     | 12                              | 12                              | y          |
| DDS5    | size                                                               | n/a             | n/a     | 0                               | 0                               | y          |
| DDS6    | sbrk                                                               | 1048576         | n/a     | 10                              | 10                              | y          |
| DDS7    | size                                                               | n/a             | n/a     | 1048576                         | 1048576                         | y          |
| DDS8    | sbrk                                                               | 4               | n/a     | Exception                       | Exception                       | y          |
| DDS9    | size                                                               | n/a             | n/a     | 0                               | 0                               | y          |
| DDS10   | sbrk                                                               | 3               | n/a     | Exception                       | Exception                       | y          |
| DDS11   | sbrk                                                               | 4               | n/a     | 10                              | 10                              | y          |
| DDS12   | size                                                               | n/a             | n/a     | 4                               | 4                               | y          |
| DDS13   | size                                                               | n/a             | n/a     | 0                               | 0                               | y          |
| DDS14   | sbrk                                                               | 8 then - 4      | n/a     | 14                              | 14                              | y          |
| DDS15   | size                                                               | n/a             | n/a     | 8                               | 8                               | y          |
| DDS16   | size                                                               | n/a             | n/a     | 0                               | 0                               | y          |
| DDS17   | sbrk                                                               | -4              | n/a     | Exception                       | Exception                       | y          |
| DDS18   | getBytes <br> (after `sbrk(8)`)<br/> and set 0x11 <br> at 5)       | 5               | 1       | 0x11                            | 0x11                            | y          |
| DDS19   | getBytes (after `sbrk(8)`) <br/> and setting 5 bytes starting at 2 | 2               | 4       | 0x11                            | 0x11                            | y          |
| DDS20   | getBytes                                                           | 2               | 4       | 0x10                            | 0x10                            | y          |
| DDS21   | getBytes                                                           | 2               | 4       | 0x78                            | 0x78                            | y          |
| DDS22   | getBytes                                                           | 2               | 4       | 0x65                            | 0x65                            | y          |
| DDS23   | getBytes                                                           | 0               | 5       | Exception                       | Exception                       | y          |
| DDS24   | setBytes <br> (test by retrieval<br> at position)                  | 4 element array | 2       | result[0] = 0x11                | result[0] = 0x11                | y          |
| DDS25   | setBytes                                                           | 4 element array | 2       | result[1] = 0x10                | result[1] = 0x10                | y          |
| DDS26   | setBytes                                                           | 4 element array | 2       | result[2] = 0x78                | result[2] = 0x78                | y          |
| DDS27   | setBytes                                                           | 4 element array | 2       | result[3] = 0x65                | result[3] = 0x65                | y          |
| DDS28   | setBytes                                                           | empty array     | 2       | result[0] = 0x11                | result[0] = 0x11                | y          |
| DDS29   | setBytes                                                           | 5 element array | 4       | Exception (out of bounds) | Exception (out of bounds) | y          |
