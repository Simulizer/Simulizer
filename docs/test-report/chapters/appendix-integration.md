## Integration tests ##

| TestID  | Components Integrated  | Program input  | Expected                   | Actual                     | Passed (Y/N) | Date passed |
|:--------|:-----------------------|:---------------|:---------------------------|:---------------------------|:-------------|:------------|
| TC-IT1  | Base CPU/assembler     | BasicTest1.s   | out: hi there4             | out: hi there4             | Y            | 14/03/16    |
| TC-IT2  | Base CPU/assembler     | BasicTest2.s   | out: pass                  | out: pass                  | Y            | 14/03/16    |
| TC-IT3  | Pipeline/assembler     | BasicTest1.s   | out: hi there4             | out: hi there4             | Y            | 14/03/16    |
| TC-IT4  | Pipeline/assembler     | BasicTest2.s   | out: pass                  | out: pass                  | Y            | 14/03/16    |
| TC-IT5  | CPU/Register View      | RegViewTest1.s | v0=10,a0=7,s0=9,t4=1       | v0=10,a0=7,s0=9,t4=1       | Y            | 14/03/16    |
| TC-IT6  | CPU/Register View      | RegViewTest2.s | t0=1,s0=6,v0=10            | t0=1,s0=6,v0=10            | Y            | 14/03/16    |
| TC-IT7  | Pipeline/Register View | RegViewTest1.s | v0=10,a0=7,s0=9,t4=1       | v0=10,a0=7,s0=9,t4=1       | Y            | 14/03/16    |
| TC-IT8  | Pipeline/Register View | RegViewTest2.s | t0=1,s0=6,v0=10            | t0=1,s0=6,v0=10            | Y            | 14/03/16    |
| TC-IT9  | Editor/Assembler       | SyntaxTest.s   | Correct highlighting       | Correct highlighting       | Y            | 14/03/16    |
| TC-IT10 | CPU/Logger             | LoggerTest1.s  | out: 7 (= user input)      | out: 7 (= user input)      | Y            | 14/03/16    |
| TC-IT11 | CPU/Logger             | LoggerTest2.s  | out: This is a test        | out: This is a test        | Y            | 14/03/16    |
| TC-IT12 | Pipeline/Logger        | LoggerTest1.s  | out: 7 (= user input)      | out: 7 (= user input)      | Y            | 14/03/16    |
| TC-IT13 | Pipeline/Logger        | LoggerTest2.s  | out: This is a test        | out: This is a test        | Y            | 14/03/16    |
| TC-IT14 | CPU/Editor             | LineTest.s     | Accurate line highlighting | Accurate line highlighting | Y            | 14/03/16    |
| TC-IT15 | Pipeline/Editor        | LineTest.s     | Accurate line highlighting | Accurate line highlighting | Y            | 14/03/16    |
| TC-IT16 | CPU/ALU                | ALUTest1.s     | out: 11                    | out: 11                    | Y            | 14/03/16    |
| TC-IT17 | CPU/ALU                | ALUTest2.s     | out: 17                    | out: 17                    | Y            | 14/03/16    |
| TC-IT18 | CPU/ALU                | ALUTest3.s     | out: 18                    | out: 18                    | Y            | 14/03/16    |
| TC-IT19 | Pipeline/ALU           | ALUTest1.s     | out: 11                    | out: 11                    | Y            | 14/03/16    |
| TC-IT20 | Pipeline/ALU           | ALUTest2.s     | out: 17                    | out: 17                    | Y            | 14/03/16    |
| TC-IT21 | Pipeline/ALU           | ALUTest3.s     | out: 18                    | out: 18                    | Y            | 14/03/16    |
| TC-IT22 | CPU/Memory             | MemTest1.s     | out: Passed test           | out: Passed test           | Y            | 14/03/16    |
| TC-IT23 | CPU/Memory             | MemTest2.s     | out: 7                     | out: 7                     | Y            | 14/03/16    |
| TC-IT24 | CPU/Memory             | MemTest3.s     | out: 19                    | out: 19                    | Y            | 14/03/16    |
| TC-IT25 | Pipeline/Memory        | MemTest1.s     | out: Passed test           | out: Passed test           | Y            | 14/03/16    |
| TC-IT26 | Pipeline/Memory        | MemTest2.s     | out: 7                     | out: 7                     | Y            | 14/03/16    |
| TC-IT26 | Pipeline/Memory        | MemTest3.s     | out: 19                    | out: 19                    | Y            | 14/03/16    |
| TC-IT27 | CPU/Annotations        | AnnoTest.s     | debug: js:passed           | debug: js:passed           | Y            | 14/03/16    |
| TC-IT28 | Pipeline/Annotations   | AnnoTest.s     | debug: js:passed           | debug: js:passed           | Y            | 14/03/16    |
| TC-IT29 | Pipeline/Pipeline View | PipeTest1.s    | 3 bubbles on screen        | 3 bubbles on screen        | Y            | 14/03/16    |
| TC-IT30 | Pipeline/Pipeline View | PipeTest2.s    | 2 bubbles on screen        | 2 bubbles on screen        | Y            | 14/03/16    |
| TC-IT31 | CPU/CPU Visualisation  | CPUVisTest1.s  | Show R-type datapath       | Show R-type datapath       | Y            | 14/03/16    |
| TC-IT32 | CPU/CPU Visualisation  | CPUVisTest2.s  | Show I-type datapath       | Show I-type datapath       | Y            | 14/03/16    |
| TC-IT33 | CPU/CPU Visualisation  | CPUVisTest3.s  | Show J-type datapath       | Show J-type datapath       | Y            | 14/03/16    |
| TC-IT33 | CPU/High-Level Visual  | HanoiTest.s    | Discs mirror algorithm     | Discs mirror algorithm     | Y            | 14/03/16    |
| TC-IT34 | CPU/High-Level Visual  | BubbleTest.s   | List mirrors algorithm     | List mirrors algorithm     | Y            | 14/03/16    |
