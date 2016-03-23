# Unit testing #

Throughout the development of Simulizer, it was made sure that any of the back-end components that could be tested via JUnit would be.  These tests were carried out in the following areas:

- **Data Converter**: Testing that the conversions between long and byte[] were working as intended
- **ALU**: The tests within this class were used to check that any calculation computed by the ALU was as intended.
- **Decode**: The methods in this class went about testing that the decode part of the Instruction Execute cycle was being carried out correctly. Each possible operand format was tested for correctness.
- **Dynamic Data Segment**: This simulated area of memory was tested to ensure that the read/write operations, as well as the allocation method of the heap segment was working properly.
**Memory**: These tests ensured that data is being stored/read correctly from within the simulated memory of the CPU.
- **Execute**: These tests tested every single instruction to ensure that they were executing correctly, in compliance with the SPIM documentation.
- **Stack**: These tests are very similar to the heap tests and check that read/write operations are working correctly as intended.
- **Parser**: Multiple sets of tests have been used to ensure that the parser is parsing the entered program text correctly, so it could be given to a simulation.

All of these tests are available to view in svn via the src/test/java directory.
$\TODO{Insert a screenshot of tests passing?}$
