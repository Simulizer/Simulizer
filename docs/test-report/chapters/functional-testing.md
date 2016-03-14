Functional testing
==================
The functional tests will be performed from a user-like perspective, in the sense that they will not be automated. We will check that the system satisfies each individual functional requirement in the software requirements specification. The tests will largely be performed by physically using the system and checking that each requirement is satisfied.

*Not sure if this is needed here. Maybe more for usability testing.*
Our intended user base is split in two distinct parties: students of the Computer Systems & Architecture module, and the module lecturer. Since the students will not be very experienced with assembly language, and the module lecturer will not have a lot of time to spend on the software, we need to ensure that the requirements are satisfied *and* they are achieved for the appropriate audience.

For these tests, the description of each requirement will be stated, then we will give a brief overview of how it will be tested, and how we will ensure it is appropriate for our audience.

Requirements
------------
### Requirement 4.1.3.a
**Description**: The system will read annotations from the source file, giving it a precise description of the points of interest in memory/registers.

**Test method**: Test various programs with annotations specifying a certain behaviour, e.g. logging a message containing information about memory. The expected behaviour we be compared with the actual behaviour, and any crashes etc. will be documented.

**Suitability for audience**: The output from interpreting the annotations, and the annotations themselves, should be human readable and presented to the user in some understandable form. This could be either visually represented in a window, or as text printed in a logger.

### Requirement 4.1.3.b
**Description**: If an unknown annotation command is given, the system will warn the user.

**Test method**: Test various programs with a combination of valid and invalid annotation commands. When a program contains an invalid annotation, we will expect the system to present a descriptive error message explaining the problem with the annotation.

**Suitability for audience**: The error should give the user enough information to correct the problem, and should use as little technical language as possible.

### Requirement 4.1.3.c
**Description**: Once the system knows where to look in “memory” it visually represents a generic data structure suitable for the algorithm (i.e. list, tree, graph, etc.).

**How it will be tested**: Test various programs with annotations specifying a high-level visualisation and check that the appropriate visualisation is shown. If any data is specified for the visualisation, e.g. elements of a list, then this data should also be shown.

**Suitability for audience**: The users of the system are expected to know algorithms involving several data structures from first year, e.g. lists/arrays, and previous assignments in the Computer Systems & Architecture module involve writing programs involving these structures. Therefore, displaying and working with these data structures should be easy  through the annotation system.

### Requirement 4.1.3.d
**Description**: The data structure displayed by the visualiser will be animated in accordance with the changing values of the data in memory.

**How it will be tested**: Test various programs with annotations specifying the behaviour of the high-level visualisation. This can quite easily be tested by simply observing the high-level visualisation and ensuring that it is consistent throughout, e.g. run a list sorting algorithm and check that the visualisation looks correct throughout and is sorted at the end.

### Requirement 4.1.3.e
**Description**: The visualisation will stay synchronised with the clock but not necessarily strictly in one clock cycle, i.e. swapping two numbers might take three clock cycles.

**How it will be tested**: The code editor shows which line number is currently being evaluated. By setting the clock speed appropriately, we can check that the visualisation performs the correct animation at the correct time. *Could we test anything with a stop watch? e.g. write a dummy program that just says #swap(0,1) and check that it does it every x seconds or something?*

### Requirement 4.1.3.f
**Description**: The user will be able to specify which content they want to have visualised, e.g. graphs, loop counters, registers, etc., can be added/removed at the user’s wish.

**How it will be tested**: Test various programs specifying different high-level visualisations and check that these are shown and hidden at the correct times.

### Requirement 4.2.3.a
**Description**: The visualisation will display and simulate the following components of our abstract CPU: Program counter, Arithmetic and Logic Unit, Instruction register, Load/store unit, General purpose registers, Simplified buses, Simplified memory.

**How it will be tested**: Checking that the CPU visualisation window always shows these components even after resizing, closing then re-opening, etc.

### Requirement 4.2.3.b
**Description**: The components of the CPU, when selected, will display a description of the purpose/use of that particular component.

**How it will be tested**: By hovering over each component of the CPU and checking that it shows an appropriate tooltip. The tooltips should not be displayed while the CPU is running.

### Requirement 4.2.3.c
**Description**: The visualisation will animate the transportation of data/instructions throughout the processor during execution. This will be primarily through the activity of buses.

**How it will be tested**: Run various small programs with the CPU visualisation window open and check that the data flow is represented between the expected components etc.

### Requirement 4.2.3.d
**Description**: The visualisation will contain abstractions to provide a clearer description of the features we deemed more important, e.g. a collection of related buses will be shown as a single connection between components.

**How it will be tested**: Comparing a complex diagram of a CPU to our own.

### Requirement 4.2.3.e
**Description**: The visualisation, including animations, will be synchronised with the internal clock of the simulation. The animations will adaptable depending on what is feasible given the clock speed.

**How it will be tested**: Similar to requirement 4.1.3.e, we can check that the data flow corresponds to the current instruction, and that they change speed accordingly to the clock speed.

### Requirement 4.2.3.f
**Description**: The visualisation can be played, paused, and the user will have the option to step forward tick by tick.

**How it will be tested**: Test that the visualisation only updates when the user either presses play or steps forward, and check that the visualisation is static when the user pauses.

### Requirement 4.2.3.g
**Description**: The program will be run using a pipeline superscalar architecture, executing a MIPS-compatible RISC instruction set with significantly fewer instructions. The system will not use speculative execution.

**How it will be tested**: ?

### Requirement 4.3.3.a
**Description**: The user will only be able to open and save files with the “.s” extension - the extension for MIPS programs.

**How it will be tested**: Click the `File->Open` menu item and check that only `.s` files are shown and only `.s` can be opened.

### Requirement 4.3.3.b
**Description**: The user will be able to choose between their existing programs they have written for the software (or create new ones), or open demo files bundled with the system.

**How it will be tested**: Check that a `.s` file can be opened from anywhere on the system; that a new blank file can be created; and that the demo files are bundled in suitable folder.

### Requirement 4.3.3.c
**Description**: The user will be able to save their own programs as well as make updates to the bundled ones.

**How it will be tested**: Test saving the text from a new file to the machine and check that this file actually exists on the machine. Also test that updates performed within the software are reflected on the machine.

### Requirement 4.3.3.d
**Description**: Syntax highlighting will be present on the assembly code, making code editing a simpler process.

**How it will be tested**: Visual inspection of several programs to ensure the syntax highlighting is accurate. Test real-time syntax highlighting by modifying existing files to ensure the highlighting remains accurate throughout.

### Requirement 4.3.3.e
**Description**: Line numbers will be shown to help with locating errors identified by the logger.

**How it will be tested**: Visual inspection to ensure line numbers are correct throughout and update when lines are deleted/inserted.

### Requirement 4.3.3.f
**Description**: The code editor will include a primitive logger/error checker, indicating that there is an error at a particular line number.

**How it will be tested**: Load a program with errors and try to assemble the program. The editor should indicate lines with errors and have a corresponding message.

### Requirement 4.3.3.g
**Description**: There will be a run button in the code editor, which will parse the code and then, if successful, will start running on the visualiser. If there is already a simulation running, a prompt will be given to request whether the current process should be halted or not.

**How it will be tested**: Try clicking the run button and a dialog should appear informing the user that the code is being parsed and assembled. Once this has finished, the program should start running and any visualisations, e.g. the CPU visualisation should start running.

### Requirement 4.3.3.h
**Description**: If the user tries to close the code editor having not saved, the system will prompt asking if they wish to save or not.

**How it will be tested**: Try modifying a new file or an existing file and then try to close the program.

### Requirement 4.4.3.a
**Description**: Provides windows for the major components previously mentioned (code editor, CPU visualiser, data structure visualisation).

**How it will be tested**: Test that each window can be opened from the menu bar.

### Requirement 4.4.3.b
**Description**: All components in the main window will be reconfigurable with respect to size and positioning. New widgets can be added or removed at the user’s wish.

**How it will be tested**: Resize the main window and check that the internal windows resize relative to their original positions; and check that the various internal windows can be repeatedly be opened/closed and moved to wherever the user wishes.

### Requirement 4.4.3.c
**Description**: The interface will allow changing of colour schemes by specification of the user, based upon a set of files bundled with the software.

**How it will be tested**: Check that the theme colour scheme of the software changes when the user selects a particular theme, and check that this colour scheme is faithful to the source file for the colour scheme.

### Requirement 4.4.3.d
**Description**: If the user attempts to close the application while a simulation is running, or the code editor is in use, they will be presented with a prompt confirming whether or not they wish to leave.

**How it will be tested**: Try to close the program while a simulation is running and check that a dialog is shown.

### Requirement 4.4.3.e
**Description**: The main window will have a menu bar, allowing the user to carry out tasks, such as change colour scheme, open new widgets, open files, exit the system, run code, etc.

**How it will be tested**: Check that each item in the menu bar responds to clicks and performs the expected task.

### Requirement 4.4.3.f
**Description**: The user will be able to save the current layout of the widgets to an external configuration file, which will be automatically loaded on startup.

**How it will be tested**: Arrange the windows in a random configuration, save the layout, rearrange the windows, then try loading the original layout to check that it orders the windows accordingly.

Dummy appendix
--------------
Requirement: 4.1.3.a
  - Test ID: ?
    - Actions performed: run the program `annotation-test.s` (see appendix).
    - Expected: an alert box should show `5`.
    - Actual: an alert box appeared and showed `5`.
    - Passed: yes.
    - Date tested: 11/03/16

Requirement: 4.1.3.b
  - Test ID: ?
    - Actions performed: run the program `unknown-command-test.s` (see appendix).
    - Expected: the logger should display an error message informing the user that the command is unknown.
    - Actual: the logger displayed `Annotation error: ReferenceError: "unknownCommand" is not defined in <eval> at line number 1
    From the annotation bound to line: 3.`.
    - Passed: yes.
    - Date tested: 11/03/16

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
