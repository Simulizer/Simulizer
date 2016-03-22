## Integration tests ##

Date tested: 14/03/16

| TestID  | Components Integrated  | Program input  | Expected                   | Actual                     | Passed (y/n) |
|:--------|:-----------------------|:---------------|:---------------------------|:---------------------------|:-------------|
| TC-IT1  | Base CPU/assembler     | BasicTest1.s   | out: hi there4             | out: hi there4             | y            |
| TC-IT2  | Base CPU/assembler     | BasicTest2.s   | out: pass                  | out: pass                  | y            |
| TC-IT3  | Pipeline/assembler     | BasicTest1.s   | out: hi there4             | out: hi there4             | y            |
| TC-IT4  | Pipeline/assembler     | BasicTest2.s   | out: pass                  | out: pass                  | y            |
| TC-IT5  | CPU/Register View      | RegViewTest1.s | v0=10,a0=7,s0=9,t4=1       | v0=10,a0=7,s0=9,t4=1       | y            |
| TC-IT6  | CPU/Register View      | RegViewTest2.s | t0=1,s0=6,v0=10            | t0=1,s0=6,v0=10            | y            |
| TC-IT7  | Pipeline/Register View | RegViewTest1.s | v0=10,a0=7,s0=9,t4=1       | v0=10,a0=7,s0=9,t4=1       | y            |
| TC-IT8  | Pipeline/Register View | RegViewTest2.s | t0=1,s0=6,v0=10            | t0=1,s0=6,v0=10            | y            |
| TC-IT9  | Editor/Assembler       | SyntaxTest.s   | Correct highlighting       | Correct highlighting       | y            |
| TC-IT10 | CPU/Logger             | LoggerTest1.s  | out: 7 (= user input)      | out: 7 (= user input)      | y            |
| TC-IT11 | CPU/Logger             | LoggerTest2.s  | out: This is a test        | out: This is a test        | y            |
| TC-IT12 | Pipeline/Logger        | LoggerTest1.s  | out: 7 (= user input)      | out: 7 (= user input)      | y            |
| TC-IT13 | Pipeline/Logger        | LoggerTest2.s  | out: This is a test        | out: This is a test        | y            |
| TC-IT14 | CPU/Editor             | LineTest.s     | Accurate line highlighting | Accurate line highlighting | y            |
| TC-IT15 | Pipeline/Editor        | LineTest.s     | Accurate line highlighting | Accurate line highlighting | y            |
| TC-IT16 | CPU/ALU                | ALUTest1.s     | out: 11                    | out: 11                    | y            |
| TC-IT17 | CPU/ALU                | ALUTest2.s     | out: 17                    | out: 17                    | y            |
| TC-IT18 | CPU/ALU                | ALUTest3.s     | out: 18                    | out: 18                    | y            |
| TC-IT19 | Pipeline/ALU           | ALUTest1.s     | out: 11                    | out: 11                    | y            |
| TC-IT20 | Pipeline/ALU           | ALUTest2.s     | out: 17                    | out: 17                    | y            |
| TC-IT21 | Pipeline/ALU           | ALUTest3.s     | out: 18                    | out: 18                    | y            |
| TC-IT22 | CPU/Memory             | MemTest1.s     | out: Passed test           | out: Passed test           | y            |
| TC-IT23 | CPU/Memory             | MemTest2.s     | out: 7                     | out: 7                     | y            |
| TC-IT24 | CPU/Memory             | MemTest3.s     | out: 19                    | out: 19                    | y            |
| TC-IT25 | Pipeline/Memory        | MemTest1.s     | out: Passed test           | out: Passed test           | y            |
| TC-IT26 | Pipeline/Memory        | MemTest2.s     | out: 7                     | out: 7                     | y            |
| TC-IT26 | Pipeline/Memory        | MemTest3.s     | out: 19                    | out: 19                    | y            |
| TC-IT27 | CPU/Annotations        | AnnoTest.s     | debug: js:passed           | debug: js:passed           | y            |
| TC-IT28 | Pipeline/Annotations   | AnnoTest.s     | debug: js:passed           | debug: js:passed           | Y            |
| TC-IT29 | Pipeline/Pipeline View | PipeTest1.s    | 3 bubbles on screen        | 3 bubbles on screen        | y            |
| TC-IT30 | Pipeline/Pipeline View | PipeTest2.s    | 2 bubbles on screen        | 2 bubbles on screen        | y            |
| TC-IT31 | CPU/CPU Visualisation  | CPUVisTest1.s  | Show R-type datapath       | Show R-type datapath       | y            |
| TC-IT32 | CPU/CPU Visualisation  | CPUVisTest2.s  | Show I-type datapath       | Show I-type datapath       | y            |
| TC-IT33 | CPU/CPU Visualisation  | CPUVisTest3.s  | Show J-type datapath       | Show J-type datapath       | y            |
| TC-IT33 | CPU/High-Level Visual  | HanoiTest.s    | Discs mirror algorithm     | Discs mirror algorithm     | y            |
| TC-IT34 | CPU/High-Level Visual  | BubbleTest.s   | List mirrors algorithm     | List mirrors algorithm     | y            |
