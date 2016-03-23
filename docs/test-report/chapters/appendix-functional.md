## Functional tests ##

### High-Level Visualisation ###

***Requirement 4.1.3.a***

**Description**: The system will read annotations from the source file, giving it a precise description of the points of interest in memory/registers.

**Test method**: Test various programs with annotations specifying a certain behaviour, e.g. logging a message containing information about memory. The expected behaviour we be compared with the actual behaviour, and any crashes etc. will be documented.

Test ID: TC-FNC1
- **Input**:
```
.text
main:
  li $v0 5 # @{ alert($v0.get()) }@
```
- **Expected**: An alert dialog should appear showing the number 5.
- **Actual**: As expected (see below).

***Requirement 4.1.3.b***

**Description**: If an annotation contains anything unknown to the system, such as variables and unknown commands, the system will warn the user.

**Test method**: Test various programs with a combination of valid and invalid annotation commands. When a program contains an invalid annotation, we will expect the system to present a descriptive error message explaining the problem with the annotation.

Test ID: TC-FNC2
- **Input**:
```
.text
main:
  li $v0 0 # @{ unknownCommand() }@
```
- **Expected**: the logger should display an error message informing the user that the command is unknown.
- **Actual**: the logger displayed `Annotation error: ReferenceError: "unknownCommand" is not defined in <eval> at line number 1
From the annotation bound to line: 3.`.

***Requirement 4.1.3.c***

**Description**: The system visually represents an appropriate data structure for a given algorithm, the data in which is based off of the CPU state.

**How it will be tested**: Test various programs with annotations specifying a high-level visualisation and check that the appropriate visualisation is shown. If any data is specified for the visualisation, e.g. elements of a list, then this data should also be shown.

*See TC-E2E22, TC-E2E23, TC-E2E24*

***Requirement 4.1.3.d***

**Description**: The data structure displayed by the visualiser will be animated in accordance with the annotations found and ‘sent’ to it via a form of message passing.

**How it will be tested**: Test various programs with annotations specifying the behaviour of the high-level visualisation. This can quite easily be tested by simply observing the high-level visualisation and ensuring that it is consistent throughout, e.g. run a list sorting algorithm and check that the visualisation looks correct throughout and is sorted at the end.

*See TC-E2E22, TC-E2E23, TC-E2E24*

***Requirement 4.1.3.e***

**Description**: The visualisation will stay synchronised with the clock but not necessarily strictly in one clock cycle, i.e. swapping two numbers might take three clock cycles.

**How it will be tested**: The code editor shows which line number is currently being evaluated. By setting the clock speed appropriately, we can check that the visualisation performs the correct animation at the correct time. *Could we test anything with a stop watch? e.g. write a dummy program that just says #swap(0,1) and check that it does it every x seconds or something?*

$\TODO{Write a test somehow}$

***Requirement 4.1.3.f***

**Description**: The user will be able to choose whether they want the high level visualisations open at any given time.

**How it will be tested**: Test various programs specifying different high-level visualisations and check that these are shown and hidden at the correct times.

Test ID: TC-FNC3
- **Input**:

```
# @{ var h = vis.loadHidden('tower-of-hanoi') }@

.text
main:

li $v0 4 # @{ h.setNumDisks($v0.get()) }@
         # @{ h.show() }@

li $v0 10; syscall
```

- **Expected**:
  * The high level visualisation should open when `h.show()` is executed. The user should be able to close the window and keep it closed. They should then be able to re-open the window and see the visualisation its current state.
- **Actual**: As expected.

### CPU Visualisation &amp; Simulation ###

***Requirement 4.2.3.a***

**Description**: The visualisation will display and simulate the following components of our abstract CPU: Program counter, Arithmetic and Logic Unit, Instruction register, Load/store unit, General purpose registers, Simplified buses, Simplified memory.

**How it will be tested**: Checking that the CPU visualisation window always shows these components even after resizing, closing then re-opening, etc.

*See below*:
![](segments/functional/cpu.png){width=80%}

***Requirement 4.2.3.b***

**Description**: The components of the CPU, when selected, will display a description of the purpose/use of that particular component.

**How it will be tested**: By hovering over each component of the CPU and checking that it shows an appropriate tooltip. The tooltips should not be displayed while the CPU is running.

*See below*:
![](segments/functional/cpu-tooltip.png){width=80%}

***Requirement 4.2.3.c***

**Description**: The visualisation will animate the transportation of data/instructions throughout the processor during execution. This will be primarily through the activity of buses.

**How it will be tested**: Run various small programs with the CPU visualisation window open and check that the data flow is represented between the expected components etc.

*See TC-E2E20*

***Requirement 4.2.3.d***

**Description**: The visualisation will contain abstractions to provide a clearer description of the features we deemed more important, e.g. a collection of related buses will be shown as a single connection between components.

**How it will be tested**: Comparing a complex diagram of a CPU to our own.

*See below*:
$\TODO{Annotate a picture of the CPU to show how the buses have been grouped together}$

***Requirement 4.2.3.e***

**Description**: The visualisation, including animations, will be synchronised with the internal clock of the simulation. The animations will be adaptable depending on what is feasible given the clock speed.

**How it will be tested**: Similar to requirement 4.1.3.e, we can check that the data flow corresponds to the current instruction, and that they change speed accordingly to the clock speed.

Test ID: TC-FNC4
- **Input**: `count.s`
- **Actions**:
  1. Run the simulation.
  2. Adjust the clock speed throughout execution (max 2Hz)
- **Expected**:
  * The animations in CPU should speed up and slow down according to the clock speed.
- **Actual**: As expected.

***Requirement 4.2.3.f***

**Description**: The visualisation can be played, paused, and the user will have the option to step forward tick by tick.

**How it will be tested**: Test that the visualisation only updates when the user either presses play or steps forward, and check that the visualisation is static when the user pauses.

Test ID: TC-FNC5
- **Input**: `count.s`
- **Actions**:
  1. Run the simulation.
  2. Pause the simulation.
  3. Step forward step by step.
- **Expected**:
  * The visualisation should start animating when the simulation is run, stop animating when paused (or finish the buffered animations), and then perform a single set of animations when the user steps forward one step.
  - **Actual**: As expected.

***Requirement 4.2.3.g***

**Description**: The program will be run using a pipeline superscalar architecture, executing a MIPS-compatible RISC instruction set with significantly fewer instructions. The system will not use speculative execution.

**How it will be tested**: $\TODO{Think of some way to explain this relating to the design, or maybe take it out in this testing section?}$.

***Requirement 4.2.3.g***

**Description**: The execution of a pipelined CPU will also be visualised, displaying information such as the instructions in each stage of the pipeline as well as information about pipeline ‘hazards’.

**How it will be tested**: Execute various programs in pipelined mode and check that the instructions are shown in the pipeline view.

*See TC-E2E17*.

### Code Editor ###

***Requirement 4.3.3.a***

**Description**: The user will only be able to open and save files with the “.s” extension - the extension for MIPS programs.

**How it will be tested**: Click the `File` $\to$ `Open` menu item and check that only `.s` files are shown and only `.s` can be opened.

*See below*:
![](segments/functional/open-s.png){width=80%}

***Requirement 4.3.3.b***

**Description**: The user will be able to choose between their existing programs they have written for the software (or create new ones), or open demo files bundled with the system.

**How it will be tested**: Check that a `.s` file can be opened from anywhere on the system; that a new blank file can be created; and that the demo files are bundled in suitable folder.

*See TC-E2E1*.

***Requirement 4.3.3.c***

**Description**: The user will be able to save their own programs as well as make updates to the bundled ones.

**How it will be tested**: Test saving the text from a new file to the machine and check that this file actually exists on the machine. Also test that updates performed within the software are reflected on the machine.

*See TC-E2E1*.

***Requirement 4.3.3.d***

**Description**: Syntax highlighting will be present on the assembly code, making code editing a simpler process.

**How it will be tested**: Visual inspection of several programs to ensure the syntax highlighting is accurate. Test real-time syntax highlighting by modifying existing files to ensure the highlighting remains accurate throughout.

*See TC-E2E1*.

***Requirement 4.3.3.e***

**Description**: Line numbers will be shown to help with locating errors identified by the logger.

**How it will be tested**: Visual inspection to ensure line numbers are correct throughout and update when lines are deleted/inserted.

*See TC-E2E1, TC-E2E2, TC-E2E3, TC-E2E4*.

***Requirement 4.3.3.f***

**Description**: The code editor will include a primitive logger/error checker, indicating that there is an error at a particular line number.

**How it will be tested**: Load a program with errors and try to assemble the program. The editor should indicate lines with errors and have a corresponding message.

*See TC-E2E4*.

***Requirement 4.3.3.g***

**Description**: There will be a run button in the menu bar, which will cause the code to be parsed and then, if successful, will start running on the visualiser. Any existing, running simulation must be halted (manually or by finishing execution) before a new one is run.

**How it will be tested**: Try clicking the run button and a dialog should appear informing the user that the code is being parsed and assembled. Once this has finished, the program should start running and any visualisations, e.g. the CPU visualisation should start running.

*See the end-to-end tests*.

***Requirement 4.3.3.h***

**Description**: If the user tries to close the code editor having not saved, the system will prompt asking if they wish to save or not.

**How it will be tested**: Try modifying a new file or an existing file and then try to close the program.

*See below*:
![](segments/functional/editor-unsaved.png){width=80%}

### Interface ###

***Requirement 4.4.3.a***

**Description**: Provides windows for the major components previously mentioned (code editor, CPU visualiser, data structure visualisation).

**How it will be tested**: Test that each window can be opened from the menu bar.

*Requirement satisfied*.

***Requirement 4.4.3.b***

**Description**: All components in the main window will be reconfigurable with respect to size and positioning. New windows can be added or removed at the user’s wish.

**How it will be tested**: Resize the main window and check that the internal windows resize relative to their original positions; and check that the various internal windows can be repeatedly be opened/closed and moved to wherever the user wishes.

*See TC-E2E13, TC-E2E14*.

***Requirement 4.4.3.c***

**Description**: The interface will allow changing of colour schemes by specification of the user, based upon a set of files bundled with the software.

**How it will be tested**: Check that the theme colour scheme of the software changes when the user selects a particular theme, and check that this colour scheme is faithful to the source file for the colour scheme.

*See below*.
$\TODO{Kelsey doesn't know how to test this}$.

***Requirement 4.4.3.d***

**Description**: If the user attempts to close the application while the code editor is in use, they will be presented with a prompt confirming whether or not they wish to leave.

**How it will be tested**: Try to close the program while a simulation is running and check that a dialog is shown.

*See below*.
![](segments/functional/application-editor-close.png){width=80%}

***Requirement 4.4.3.e***

**Description**: The main window will have a menu bar, allowing the user to carry out tasks, such as change colour scheme, open new windows, open files, exit the system, run code, etc.

**How it will be tested**: Check that each item in the menu bar responds to clicks and performs the expected task.

*Requirement satisfied*.

***Requirement 4.4.3.f***

**Description**: The user will be able to save the current layout of the widgets to an external configuration file, which can then be selected when the user returns to the system at a later date.

**How it will be tested**: Arrange the windows in a random configuration, save the layout, rearrange the windows, then try loading the original layout to check that it orders the windows accordingly.

*See TC-E2E11, TC-E2E12*.

***Requirement 4.4.3.g***

**Description**: A full options menu will be provided with the system, to allow the user to change their preferences of the system in one place.

**How it will be tested**: Change various parameters using the options menu and check that these are reflected in the system.

*See TC-E2E15*.
