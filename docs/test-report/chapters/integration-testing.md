Integration Testing
===================

For the testing of Simulizer, a bottom up test approach has been used. That is, firstly all of the unit tests were created for each of the components used and the correctness was checked. After this, tests were created to check the combining of different components. Specific tests were created for the linking of certain components, but as well as this, regression tests were carried out to check that all of the unit tests were still passing when a new component was linked (due to the low coupling of the system, there weren't many cases where the unit tests would even be affected). 
When carrying out integration tests on the back end of the system, because, in order to test the integration of any cpu components programs are needed, the assembler has to be always integrated with the cpu, and additionally in order to test the functionality of certain components integrated, they all need to be linked to the cpu. Therefore, in the case of the backend integration tests, the components, the cpu and the assembler would be integrated as one, but tests would focus on only a select couple of those components (such as the CPU and memory). Due to the many visualisation components having no links to each other whatsoever, these will not be covered in this set of tests. Because the small programs used would make this document very difficult to read, they are stored in the svn directory, under src/test/java/integration. In this document, program input will refer to the file name in this directory.


| TestID | Components Integrated | Program input | Expected                 | Actual                   | Passed (Y/N)| Date passed |
|:-------|:----------------------|:--------------|:-------------------------|:-------------------------|:------------|:------------|
| TC-IT1 |Base CPU/assembler     |BasicTest1.s   |out: hi there4            |out: hi there4            | Y           |14/03/16     |
| TC-IT2 |Base CPU/assembler     |BasicTest2.s   |out: pass                 |out: pass                 | Y           |14/03/16     |
| TC-IT3 |Pipeline/assembler     |BasicTest1.s   |out: hi there4            |out: hi there4            | Y           |14/03/16     |
| TC-IT4 |Pipeline/assembler     |BasicTest2.s   |out: pass                 |out: pass                 | Y           |14/03/16     |
| TC-IT5 |CPU/Register View      |RegViewTest1.s |v0=10,a0=7,s0=9,t4=1      |v0=10,a0=7,s0=9,t4=1      | Y           |14/03/16     |
| TC-IT6 |CPU/Register View      |RegViewTest2.s |t0=1,s0=6,v0=10           |t0=1,s0=6,v0=10           | Y           |14/03/16     |
| TC-IT7 |Pipeline/Register View |RegViewTest1.s |v0=10,a0=7,s0=9,t4=1      |v0=10,a0=7,s0=9,t4=1      | Y           |14/03/16     |
| TC-IT8 |Pipeline/Register View |RegViewTest2.s |t0=1,s0=6,v0=10           |t0=1,s0=6,v0=10           | Y           |14/03/16     |
| TC-IT9 |Editor/Assembler       |SyntaxTest.s   |Correct highlighting      |Correct highlighting      | Y           |14/03/16     |
| TC-IT10|CPU/Logger             |LoggerTest1.s  |out: 7 (= user input)     |out: 7 (= user input)     | Y           |14/03/16     |
| TC-IT11|CPU/Logger             |LoggerTest2.s  |out: This is a test       |out: This is a test       | Y           |14/03/16     |
| TC-IT12|Pipeline/Logger        |LoggerTest1.s  |out: 7 (= user input)     |out: 7 (= user input)     | Y           |14/03/16     |
| TC-IT13|Pipeline/Logger        |LoggerTest2.s  |out: This is a test       |out: This is a test       | Y           |14/03/16     |
| TC-IT14|CPU/Editor             |LineTest.s     |Accurate line highlighting|Accurate line highlighting| Y           |14/03/16     |
| TC-IT15|Pipeline/Editor        |LineTest.s     |Accurate line highlighting|Accurate line highlighting| Y           |14/03/16     |    
| TC-IT16|CPU/ALU                |ALUTest1.s     |out: 11                   |out: 11                   | Y           |14/03/16     |
| TC-IT17|CPU/ALU                |ALUTest2.s     |out: 17                   |out: 17                   | Y           |14/03/16     |
| TC-IT18|CPU/ALU                |ALUTest3.s     |out: 18                   |out: 18                   | Y           |14/03/16     |
| TC-IT19|Pipeline/ALU           |ALUTest1.s     |out: 11                   |out: 11                   | Y           |14/03/16     |
| TC-IT20|Pipeline/ALU           |ALUTest2.s     |out: 17                   |out: 17                   | Y           |14/03/16     |
| TC-IT21|Pipeline/ALU           |ALUTest3.s     |out: 18                   |out: 18                   | Y           |14/03/16     |
| TC-IT22|CPU/Memory             |MemTest1.s     |out: Passed test          |out: Passed test          | Y           |14/03/16     |
| TC-IT23|CPU/Memory             |MemTest2.s     |out: 7                    |out: 7                    | Y           |14/03/16     |
| TC-IT24|CPU/Memory             |MemTest3.s     |out: 19                   |out: 19                   | Y           |14/03/16     |
| TC-IT25|Pipeline/Memory        |MemTest1.s     |out: Passed test          |out: Passed test          | Y           |14/03/16     |
| TC-IT26|Pipeline/Memory        |MemTest2.s     |out: 7                    |out: 7                    | Y           |14/03/16     |
| TC-IT26|Pipeline/Memory        |MemTest3.s     |out: 19                   |out: 19                   | Y           |14/03/16     |
| TC-IT27|CPU/Annotations        |AnnoTest.s     |debug: js:passed          |debug: js:passed          | Y           |14/03/16     |
| TC-IT28|Pipeline/Annotations   |AnnoTest.s     |debug: js:passed          |debug: js:passed          | Y           |14/03/16     |
| TC-IT29|Pipeline/Pipeline View |PipeTest1.s    |3 bubbles on screen       |3 bubbles on screen       | Y           |14/03/16     |
| TC-IT30|Pipeline/Pipeline View |PipeTest2.s    |2 bubbles on screen       |2 bubbles on screen       | Y           |14/03/16     |
| TC-IT31|CPU/CPU Visualisation  |CPUVisTest1.s  |Show R-type datapath      |Show R-type datapath      | Y           |14/03/16     |
| TC-IT32|CPU/CPU Visualisation  |CPUVisTest2.s  |Show I-type datapath      |Show I-type datapath      | Y           |14/03/16     |
| TC-IT33|CPU/CPU Visualisation  |CPUVisTest3.s  |Show J-type datapath      |Show J-type datapath      | Y           |14/03/16     |
| TC-IT33|CPU/High-Level Visual  |HanoiTest.s    |Discs mirror algorithm    |Discs mirror algorithm    | Y           |14/03/16     |
| TC-IT34|CPU/High-Level Visual  |BubbleTest.s   |List mirrors algorithm    |List mirrors algorithm    | Y           |14/03/16     |

