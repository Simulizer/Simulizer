Functional testing
==================
The functional tests will be performed from the user's perspective, in the sense that they will not be automated. We will check that the system satisfies each individual functional requirement in the software requirements specification. The corresponding reference is given at the start of each test case.

**TODO: add more tests for each requirement**

- **Requirement 4.1.3.a**: The system will read annotations from the source file, giving it a precise description of the points of interest in memory/registers.
  - Test ID: ?
    - Actions performed: run the program `annotation-test.s` (see appendix).
    - Expected: an alert box should show `5`.
    - Actual: an alert box appeared and showed `5`.
    - Passed: yes.
    - Date tested: 11/03/16

- **Requirement 4.1.3.b**: If an unknown annotation command is given, the system will warn the user.
  - Test ID: ?
    - Actions performed: run the program `unknown-command-test.s` (see appendix).
    - Expected: the logger should display an error message informing the user that the command is unknown.
    - Actual: the logger displayed `Annotation error: ReferenceError: "unknownCommand" is not defined in <eval> at line number 1
    From the annotation bound to line: 3.`.
    - Passed: yes.
    - Date tested: 11/03/16

- **Requirement 4.1.3.c**:
  - Test ID: ?
    - Actions performed:
    - Expected:
    - Actual:
    - Passed:
    - Date tested:

  - **Requirement 4.1.3.d**: The data structure displayed by the visualiser will be animated in accordance with the changing values of the data in memory.
    - Test ID: ?
      - Actions performed:
      - Expected:
      - Actual:
      - Passed:
      - Date tested:


- Requirement :
  - Test ID: ?
    - Actions performed:
    - Expected:
    - Actual:
    - Passed:
    - Date tested:

Dummy appendix
--------------
- `annotation-test.s`

  ```
  .text
  main:
    li $v0 5 # @{ alert($v0.get()) }@
  ```

- `unknown-command-test.s`

  ```
  .text
  main:
    li $v0 0 # @{ unknownCommand() }@
  ```
