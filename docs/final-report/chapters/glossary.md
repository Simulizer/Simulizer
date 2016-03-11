Glossary
========
- ALU -- Arithmetic and Logic Unit: A component in the CPU that carries out the large majority of computations in the CPU

- Assembly Language -- A type of programming language situated in-between high level languages such as Java and machine code. Assembly language programs can directly access registers, memory etc. 

- Binary Search -- An efficient $O(\log n)$ searching algorithm for searching in an ordered list.

- Branch -- Similar to a jump but technically on a smaller address range, and usually based upon the result of some condition being tested.

- Bubble Sort -- A highly inefficient sorting algorithm of $O(n^2)$ complexity.

- Bubbling -- A name given to a pipeline stall. When running a pipelined CPU, there are cases where certain stages of the pipeline have to be stalled in order to prevent certain types of error (such as reading something before it is written to). In order to prevent this, ‘bubbles’ are placed in the pipeline: NOP instructions (these do nothing) are placed into the pipeline to stall a certain stage for a cycle (or more). Visually, it is similar to the idea of a rising bubble.

- Bus -- A method of transferring data between CPU components. It is essentially just a wire.

- Clock -- In the case of the CPU, a clock is used to keep all components in the CPU synchronised. As a rule of thumb with RISC (Reduced Instruction Set Computer) machines, one round of the Instruction Execution cycle lasts one ‘tick’ of the clock.

- CPU -- Central Processing Unit. 

- High-Level Visualisation -- A visualisation of high level algorithms running, such as showing the movement of data in a linked list as a sort is being run.

- Instruction Execution Cycle -- A series of steps that is run in cycles in order to run a program. It consists of multiple stages, usually 4/5 but in the case of Simulizer, simplified down to 3: Fetch, decode and execute.

- Instruction Register -- A register which stores the instruction currently being executed.

- Jump -- Moving to a different point in the program during execution other than sequentially.

- Memory -- When memory is discussed, it refers to the main memory (i.e. RAM). The main memory in a MIPS processor consists of 5 main segments: one for OS reserved data, one for the code to be executed, one for all statically defined data, one for dynamically allocated data (the heap), and one for the stack.

- MIPS -- A type of processor not so common in modern times, but the type of processor used for example in the ‘Computer Systems & Architecture’ module to demonstrate the running of the processor.

- Non-Pipelined Execution: In this case the IE cycle just focuses on one instruction: one instruction is fetched, decoded and then executed, and then the next one and so on.

- Pipelining -- A form of instruction execution that significantly speeds up program execution without changing the clock speed. It does this by (in a simplified sense) executing instruction n, while decoding instruction $n+1$, and fetching instruction $n+2$.
Program Counter -- A register that stores the address of the next instruction to be executed.

- Register -- A very fast piece of memory located in the processor (very close to the components). In a MIPS processor, registers are 4 bytes in size.

- Syscall -- An instruction in the MIPS language, which, when given a code which is stored in the v0 register, will perform a certain operation. A large majority of these operations are related to IO operations but also include, and are not limited to, allocating new heap memory and exiting the program.

- Word -- 4 bytes.

